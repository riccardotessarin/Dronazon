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
				.setIsMaster(droneProperty.isMaster()).setIsElecting(droneProperty.isParticipant()).build();

		// Give response to stream
		responseObserver.onNext(response);

		// Complete and end communication
		responseObserver.onCompleted();

		// TODO: ensure that at least one drone will stay inside the network while a new drone joins
	}

	@Override
	public void dispatchOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
		System.out.println(request);

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
		DroneDeliveryThread deliveryThread = new DroneDeliveryThread(droneProperty, orderData);
		deliveryThread.start();
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

		int batteryLevel = 0;
		if (!droneProperty.isDelivering()) {
			batteryLevel = droneProperty.getBatteryLevel();
		} else {
			batteryLevel = droneProperty.getBatteryLevel() - 10;
		}

		//System.out.println("Drone: " + droneProperty.getDroneID() + " Best: " + bestID);

		if (droneProperty.getDroneID() == bestID) {
			System.out.println("I'm the designed master! Sending elected message...");
			droneProperty.setParticipant(false);
			DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneProperty.getNextInRing(), bestID);
			serviceThread.start();
		} else if (batteryLevel > bestBattery && !droneProperty.isParticipant()) {
			System.out.println("My battery is higher, sending my info in election");
			droneProperty.setParticipant(true);
			DroneServiceThread serviceThread =
					new DroneServiceThread(droneProperty, droneProperty.getNextInRing(), batteryLevel, droneProperty.getDroneID());
			serviceThread.start();
		} else if (batteryLevel == bestBattery && droneProperty.getDroneID() > bestID && !droneProperty.isParticipant()) {
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
			return;
		}

		droneProperty.setMasterDroneByID(electedID);
		DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneProperty.getNextInRing(), electedID);
		serviceThread.start();
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

	public DroneProperty getDroneProperty() {
		return droneProperty;
	}

	public void setDroneProperty(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}
}
