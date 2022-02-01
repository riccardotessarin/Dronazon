package smartcitydrone;

import beans.DroneInfo;
import com.google.gson.Gson;
import com.smartcitydrone.droneservice.DroneServiceGrpc.DroneServiceImplBase;
import com.smartcitydrone.droneservice.DroneServiceOuterClass.*;
import io.grpc.stub.StreamObserver;
import smartcity.OrderData;

public class DroneServiceImpl extends DroneServiceImplBase {
	private DroneProperty droneProperty;

	public DroneServiceImpl(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}


	@Override
	public void joinNetwork(JoinRequest request, StreamObserver<JoinResponse> responseObserver) {
		System.out.println(request);

		// Here the receiver drone elaborates the join request from the new drone
		DroneInfo droneInfo = new DroneInfo(request.getDroneID(), request.getIpAddress(), request.getPort());
		droneInfo.setDronePosition(new int[]{request.getDronePositionX(), request.getDronePositionY()});

		// So it can be added to the grpc network
		droneProperty.addToNetwork(droneInfo);

		JoinResponse response = JoinResponse.newBuilder()
				.setDroneID(droneProperty.getDroneID()).setDronePositionX(droneProperty.getDronePosition()[0])
				.setDronePositionY(droneProperty.getDronePosition()[1]).setBatteryLevel(droneProperty.getBatteryLevel())
				.setIsMaster(droneProperty.isMaster()).setIsParticipant(droneProperty.isParticipant()).build();

		// Give response to stream
		responseObserver.onNext(response);

		// Complete and end communication
		responseObserver.onCompleted();

		// A new drone has joined so we can immediately assign to it a pending order if this drone is trying to exit and
		// is master
		droneProperty.notifyDroneForDelivery();
	}

	@Override
	public void dispatchOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
		System.out.println(request);

		// If the drone is trying to safe quit, we don't send it more orders than the one it is eventually
		// already finishing (worst case: drone may receive an order immediately after it finishes the latter)
		// We don't include master, since its deliveries are fully controlled and leaves only after all are dispatched
		if (!droneProperty.isMaster() && droneProperty.isQuitting()) {
			System.out.println("This drone is in safe quit process, try another one");
			OrderResponse orderResponse = OrderResponse.newBuilder().setDroneAvailable("QUITTING").build();
			responseObserver.onNext(orderResponse);
			responseObserver.onCompleted();
			return;
		}

		// First condition: it is not delivering. If it is, we just answer no and close connection
		if (droneProperty.isDelivering()) {
			System.out.println("This drone is already delivering, try another one");
			OrderResponse orderResponse = OrderResponse.newBuilder().setDroneAvailable("BUSY").build();
			responseObserver.onNext(orderResponse);
			responseObserver.onCompleted();
			return;
		}

		OrderData orderData = new Gson().fromJson(request.getOrderInfo(), OrderData.class);
		OrderResponse orderResponse = OrderResponse.newBuilder().setDroneAvailable("AVAILABLE").build();
		responseObserver.onNext(orderResponse);
		responseObserver.onCompleted();

		// Set the drone is now delivering and start delivery thread
		droneProperty.setDelivering(true);
		droneProperty.setDeliveryThread(new DroneDeliveryThread(droneProperty, orderData));
		droneProperty.getDeliveryThread().start();
	}

	// This is for the master, it receives the message from the drones after they end a delivery
	@Override
	public void sendDroneStat(StatRequest request, StreamObserver<StatResponse> responseObserver) {
		System.out.println(request);

		// Master gives ok signal to drone, meaning it's still online
		StatResponse response = StatResponse.newBuilder().setMasterResponse("OK").build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();

		DroneStat droneStat = new Gson().fromJson(request.getDroneStat(), DroneStat.class);
		int droneID = request.getDroneID();

		droneProperty.addDroneStat(droneStat);
		droneProperty.updateDroneBatteryLevel(droneID, droneStat.getBatteryLeft());
		droneProperty.updateDronePosition(droneID, droneStat.getDronePosition());
		droneProperty.setDroneIsDelivering(droneID, false);


		// After we receive stats from a drone we need to look for pending orders inside the queue

		if (droneProperty.getOrdersQueue().size() == 0) {
			System.out.println("No orders in pending list");
			// If we have no more orders to dispatch we can wake the master and complete the exit
			droneProperty.notifyDroneForDelivery();
			return;
		}

		System.out.println("Sending new order from pending list...");
		OrderData order = droneProperty.getOrderFromQueue();

		DroneInfo bestDrone = droneProperty.getBestDeliveryDrone(order.getPickUpPoint());
		if (bestDrone == null) {
			System.out.println("No drone currently available for delivery");
			droneProperty.addToOrdersQueue(order);
		} else {
			DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, bestDrone, order);
			serviceThread.start();
		}
	}

	@Override
	public void election(ElectionRequest request, StreamObserver<ElectionResponse> responseObserver) {
		int bestBattery = request.getBatteryLevel();
		int bestID = request.getDroneID();

		// Drone gives ok signal to its preceding in the ring, meaning it's still online
		ElectionResponse response = ElectionResponse.newBuilder().setDroneResponse("OK").build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();

		/*
		// When a re-election occurs, another (correct) election could have been already in progress
		// (ex: a new election started without taking the crashed master into account)
		// the elected message is already in network, so no need to send more
		if (droneProperty.isMaster()) {
			System.out.println("New master is stopping a duplicate election");
			return;
		}

		 */

		int batteryLevel = 0;
		if (!droneProperty.isDelivering()) {
			batteryLevel = droneProperty.getBatteryLevel();
		} else {
			batteryLevel = droneProperty.getBatteryLevel() - 10;
		}

		//System.out.println("Drone: " + droneProperty.getDroneID() + " Best: " + bestID);

		if (droneProperty.getDroneID() == bestID /*&& !droneProperty.isMaster*/) {
			System.out.println("I'm the designed master! Sending elected message...");
			// We set it at false when making master, so we avoid the joining drone with a nearly elected issue
			// (if it joins while the second-last drone sets its own isParticipant to false and there is still no master,
			// it would start a new election)
			droneProperty.setParticipant(false);

			// By setting the isMaster to true immediately we can avoid the edge case of the elected message past
			// the newly joined drone (remember the LookForMaster thread and grpc message you made? This is better)
			// It doesn't receive deliveries and doesn't need to communicate but with this we can start the check thread
			droneProperty.setMaster(true);
			DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneProperty.getNextInRing(), bestID);
			serviceThread.start();
		} else if (batteryLevel > bestBattery && !droneProperty.isParticipant() /*&& !droneProperty.isMaster*/) {
			System.out.println("My battery is higher, sending my info in election");
			droneProperty.setParticipant(true);
			DroneServiceThread serviceThread =
					new DroneServiceThread(droneProperty, droneProperty.getNextInRing(), batteryLevel, droneProperty.getDroneID());
			serviceThread.start();
		} else if (batteryLevel == bestBattery && droneProperty.getDroneID() > bestID && !droneProperty.isParticipant() /*&& !droneProperty.isMaster*/) {
			System.out.println("Same battery but higher ID, sending my info in election");
			droneProperty.setParticipant(true);
			DroneServiceThread serviceThread =
					new DroneServiceThread(droneProperty, droneProperty.getNextInRing(), batteryLevel, droneProperty.getDroneID());
			serviceThread.start();
		} else if (batteryLevel < bestBattery || (batteryLevel == bestBattery && droneProperty.getDroneID() < bestID)) {
			System.out.println("Battery/ID worse, forwarding");
			droneProperty.setParticipant(true);
			DroneServiceThread serviceThread =
					new DroneServiceThread(droneProperty, droneProperty.getNextInRing(), bestBattery, bestID);
			serviceThread.start();
		} else {
			System.out.println("Stopping election message, better and already participant");
		}
	}

	@Override
	public void elected(ElectedRequest request, StreamObserver<ElectedResponse> responseObserver) {
		// Drone gives ok signal to its preceding in the ring, meaning it's still online
		ElectedResponse response = ElectedResponse.newBuilder().setDroneResponse("OK").build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();

		int electedID = request.getDroneID();
		droneProperty.setParticipant(false);

		// When this is true, the elected message chain stops
		if (droneProperty.getDroneID() == electedID) {
			System.out.println("I'm becoming the new master after the election");
			droneProperty.makeMaster();
			// Now we can wake the master if it wants to quit
			droneProperty.notifyPendingElectionMux();
			return;
		}

		droneProperty.setMasterDroneByID(electedID);
		DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneProperty.getNextInRing(), electedID);
		serviceThread.start();

		// We always need to send this to update the new master's network info
		DroneServiceThread pendingStatsThread = new DroneServiceThread(droneProperty, droneProperty.getMasterDrone(), "pending");
		pendingStatsThread.start();
	}

	// Master receives all the updated drones properties along with the pending stats (if any)
	@Override
	public void sendPendingDroneStat(PendingStatRequest request, StreamObserver<PendingStatResponse> responseObserver) {
		// Master gives ok signal to drone, meaning it's still online
		PendingStatResponse response = PendingStatResponse.newBuilder().setMasterResponse("OK").build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();

		// Get pending stats (if any)
		if (!request.getDroneStat().equalsIgnoreCase("")) {
			DroneStat pendingStat = new Gson().fromJson(request.getDroneStat(), DroneStat.class);
			droneProperty.addDroneStat(pendingStat);
		}

		int droneID = request.getDroneID();

		// Update properties inside the new master's drone network
		droneProperty.updateDronePosition(droneID, new int[]{request.getDronePositionX(), request.getDronePositionY()});
		droneProperty.updateDroneBatteryLevel(droneID, request.getBatteryLevel());
		droneProperty.setDroneIsDelivering(droneID, request.getIsDelivering());
		droneProperty.setDroneIsCharging(droneID, request.getIsCharging());

	}

	// Drone just gives an ok, meaning it's still online
	@Override
	public void check(CheckMessage request, StreamObserver<CheckMessage> responseObserver) {
		// Master gives ok signal to drone, meaning it's still online
		CheckMessage response = CheckMessage.newBuilder().setMessage("OK").build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/*
	@Override
	public void lookForMaster(CheckMessage request, StreamObserver<LookForMasterResponse> responseObserver) {
		// We give info about the master to the drone who still doesn't know
		LookForMasterResponse response = LookForMasterResponse.newBuilder().setDroneID(droneProperty.getDroneID())
				.setIsMaster(droneProperty.isMaster()).setIsParticipant(droneProperty.isParticipant()).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	 */

	public DroneProperty getDroneProperty() {
		return droneProperty;
	}

	public void setDroneProperty(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}
}
