package smartcitydrone;

import beans.DroneInfo;
import com.google.gson.Gson;
import com.smartcitydrone.droneservice.DroneServiceGrpc;
import com.smartcitydrone.droneservice.DroneServiceGrpc.*;
import com.smartcitydrone.droneservice.DroneServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import smartcity.OrderData;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DroneServiceThread extends Thread {
	private String message;
	private DroneProperty senderDrone;
	private DroneInfo receiverDrone;
	private OrderData orderData;
	private DroneStat droneStat;
	private int bestBattery;
	private int bestID;
	private int electedID;

	public DroneServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.message = "join";
	}

	public DroneServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone, OrderData orderData) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.orderData = orderData;
		this.message = "delivery";
	}

	public DroneServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone, DroneStat droneStat) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.droneStat = droneStat;
		this.message = "stat";
	}

	public DroneServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone, int bestBattery, int bestID) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.bestBattery = bestBattery;
		this.bestID = bestID;
		this.message = "election";
	}

	public DroneServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone, int electedID) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.electedID = electedID;
		this.message = "elected";
	}

	public DroneServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone, String message) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.message = message;
	}


	@Override
	public void run() {
		try {
			if (message.equalsIgnoreCase("join")) {
				joinNetwork();
			} else if (message.equalsIgnoreCase("delivery")) {
				dispatchOrder();
			} else if (message.equalsIgnoreCase("stat")) {
				sendDroneStat();
			} else if (message.equalsIgnoreCase("election")) {
				election();
			} else if (message.equalsIgnoreCase("elected")) {
				elected();
			} else if (message.equalsIgnoreCase("pending")) {
				sendPendingDroneStat();
			} else if (message.equalsIgnoreCase("check")) {
				check();
			}
			/*
			else if (message.equalsIgnoreCase("lookformaster")) {
				lookForMaster();
			}
			 */
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void joinNetwork() throws InterruptedException {
		System.out.println("Sending a request to join the grpc network");

		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		DroneServiceStub stub = DroneServiceGrpc.newStub(channel);

		JoinRequest request = JoinRequest.newBuilder()
				.setDroneID(senderDrone.getDroneID()).setIpAddress(senderDrone.getIpAddress())
				.setPort(senderDrone.getPort()).setDronePositionX(senderDrone.getDronePosition()[0])
				.setDronePositionY(senderDrone.getDronePosition()[1]).build();

		stub.joinNetwork(request, new StreamObserver<JoinResponse>() {
			// If it receives a response we keep the drone in the list and update its values
			// We also acquire info about the master and if there is an election
			@Override
			public void onNext(JoinResponse value) {
				List<DroneInfo> dronesInNetworkCopy = senderDrone.getDronesInNetwork();
				DroneInfo droneInfo = dronesInNetworkCopy.stream()
						.filter(dInfo -> dInfo.getDroneID() == value.getDroneID()).findFirst().orElse(null);
				if (droneInfo == null) {
					System.out.println("ID of drone not found in network");
					channel.shutdownNow();
					return;
				}
				droneInfo.setBatteryLevel(value.getBatteryLevel());
				droneInfo.setDronePosition(new int[]{value.getDronePositionX(), value.getDronePositionY()});

				// If the drone who wants to join doesn't already know the master
				// and the one who is talking with it is master, set the master
				if (senderDrone.getMasterDrone() == null && value.getIsMaster()) {
					//senderDrone.setMasterDroneByID(value.getDroneID());
					senderDrone.setMasterDrone(receiverDrone);
				}

				// If the drone who wants to join doesn't already know there is an ongoing election
				// and the one who is talking with it is participant, set the election in progress true
				// With this we ensure that the new drone doesn't start an election that may cause issues
				if (!senderDrone.isElectionInProgress() && value.getIsElecting()) {
					senderDrone.setElectionInProgress(true);
				}

			}

			// If it doesn't receive a response, it means the receiver drone is offline so we remove it
			@Override
			public void onError(Throwable t) {
				System.out.println("Error! " + t.getMessage());
				senderDrone.removeFromNetwork(receiverDrone);
				channel.shutdownNow();
			}

			@Override
			public void onCompleted() {
				channel.shutdownNow();
			}
		});

		//you need this. otherwise the method will terminate before that answers from the server are received
		channel.awaitTermination(10, TimeUnit.SECONDS);
	}

	// Only the master calls this function
	private void dispatchOrder() throws InterruptedException {
		System.out.println("Sending order " + orderData.getOrderID() + " to drone " + receiverDrone.getDroneID());

		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		DroneServiceStub stub = DroneServiceGrpc.newStub(channel);

		String orderGson = new Gson().toJson(orderData);
		OrderRequest request = OrderRequest.newBuilder().setOrderInfo(orderGson).build();

		stub.dispatchOrder(request, new StreamObserver<OrderResponse>() {
			@Override
			public void onNext(OrderResponse value) {
				// In the unfortunate case the exiting master drone and/or two drones who simultaneously get the
				// receiver drone as the best drone for two different orders, it answers with BUSY and we awake
				// the quitting master drone (if it is doing so) so it can look for another drone to handle the delivery
				if (value.getDroneAvailable().equalsIgnoreCase("BUSY")) {
					System.out.println("Drone answered with BUSY response, adding order to queue");
					senderDrone.addToOrdersQueue(orderData);
					senderDrone.notifyDroneForDelivery();
					channel.shutdownNow();
					return;
				}
				//Set the drone is now delivering inside master's network list
				senderDrone.setDroneIsDelivering(receiverDrone.getDroneID(), true);

			}

			// If it can't communicate with the drone it is likely that it has crashed/quitted network
			@Override
			public void onError(Throwable t) {
				System.out.println("Error! " + t.getMessage());
				System.out.println("Drone unavailable, adding order to queue and removing drone from network");
				senderDrone.removeFromNetwork(receiverDrone);
				senderDrone.addToOrdersQueue(orderData);
				channel.shutdownNow();
			}

			@Override
			public void onCompleted() {
				channel.shutdownNow();
			}
		});

		//you need this. otherwise the method will terminate before that answers from the server are received
		channel.awaitTermination(10, TimeUnit.SECONDS);
	}

	// The drone sends the stats to the master, if it gives an error the master is down
	public void sendDroneStat() throws InterruptedException {
		// If the master made the delivery, there's no point in opening the connection
		if (senderDrone.getDroneID() == receiverDrone.getDroneID()) {
			System.out.println("Master made the delivery, saving stats");
			senderDrone.addDroneStat(droneStat);
			// If this is master, then position, battery and delivery status are already updated inside DeliveryThread
			return;
		}

		System.out.println("Sending stats to master with ID " + receiverDrone.getDroneID());

		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		DroneServiceStub stub = DroneServiceGrpc.newStub(channel);

		String statGson = new Gson().toJson(droneStat);
		StatRequest request = StatRequest.newBuilder()
				.setDroneID(senderDrone.getDroneID()).setDroneStat(statGson).build();

		stub.sendDroneStat(request, new StreamObserver<StatResponse>() {
			@Override
			public void onNext(StatResponse value) {
				if (value.getMasterResponse().equalsIgnoreCase("OK")) {
					System.out.println("Received ok from master");
				}
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Master is down! Starting an election...");
				senderDrone.setPendingDroneStat(droneStat);
				senderDrone.setNoMasterDrone();
				senderDrone.removeFromNetwork(receiverDrone);
				senderDrone.setParticipant(true);
				DroneServiceThread serviceThread =
						new DroneServiceThread(senderDrone, senderDrone.getNextInRing(), senderDrone.getBatteryLevel(), senderDrone.getDroneID());
				serviceThread.start();
				channel.shutdownNow();
			}

			@Override
			public void onCompleted() {
				channel.shutdownNow();
			}
		});

		//you need this. otherwise the method will terminate before that answers from the server are received
		channel.awaitTermination(10, TimeUnit.SECONDS);
	}

	public void election() throws InterruptedException {
		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		DroneServiceStub stub = DroneServiceGrpc.newStub(channel);

		ElectionRequest request = ElectionRequest.newBuilder()
				.setBatteryLevel(bestBattery).setDroneID(bestID).build();

		stub.election(request, new StreamObserver<ElectionResponse>() {
			@Override
			public void onNext(ElectionResponse value) {
				System.out.println(value.getDroneResponse());
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Next drone in the ring is down! Removing it and trying with next one...");
				senderDrone.removeFromNetwork(receiverDrone);
				DroneInfo nextDrone = senderDrone.getNextInRing();
				//TODO: (CRASH) Check if original receiver was the designed master (or already removed), if it was restart the election

				/*
				// Code to test for crashes
				if (receiverDrone.getDroneID() == bestID) {
					System.out.println("Drone designed to be master crashed! Restarting election...");
					DroneServiceThread serviceThread =
							new DroneServiceThread(senderDrone, nextDrone, senderDrone.getBatteryLevel(), senderDrone.getDroneID());
					serviceThread.start();
				} else {}
				 */
				DroneServiceThread serviceThread = new DroneServiceThread(senderDrone, nextDrone, bestBattery, bestID);
				serviceThread.start();
				channel.shutdown();
			}

			@Override
			public void onCompleted() {
				channel.shutdown();
			}
		});

		//you need this. otherwise the method will terminate before that answers from the server are received
		channel.awaitTermination(10, TimeUnit.SECONDS);
	}

	private void elected() throws InterruptedException {
		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		DroneServiceStub stub = DroneServiceGrpc.newStub(channel);

		ElectedRequest request = ElectedRequest.newBuilder().setDroneID(electedID).build();

		stub.elected(request, new StreamObserver<ElectedResponse>() {
			@Override
			public void onNext(ElectedResponse value) {
				System.out.println(value.getDroneResponse());
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Next drone crashed! Removing and sending to next in ring...");
				senderDrone.removeFromNetwork(receiverDrone);
				DroneInfo nextDrone = senderDrone.getNextInRing();

				// If the one who had to receive was the new master, start a new election (everyone already not participant)
				if (senderDrone.getMasterDrone() != null && senderDrone.getMasterDrone().getDroneID() == receiverDrone.getDroneID()) {
					System.out.println("Elected master down! Starting new election...");
					senderDrone.setNoMasterDrone();
					senderDrone.setParticipant(true);
					DroneServiceThread serviceThread =
							new DroneServiceThread(senderDrone, nextDrone, senderDrone.getBatteryLevel(), senderDrone.getDroneID());
					serviceThread.start();
				} else {
					DroneServiceThread serviceThread =
							new DroneServiceThread(senderDrone, nextDrone, electedID);
					serviceThread.start();
				}
				channel.shutdown();
			}

			@Override
			public void onCompleted() {
				channel.shutdown();
			}
		});

		//you need this. otherwise the method will terminate before that answers from the server are received
		channel.awaitTermination(10, TimeUnit.SECONDS);
	}

	public void sendPendingDroneStat() throws InterruptedException {
		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		DroneServiceStub stub = DroneServiceGrpc.newStub(channel);

		// Check if there are pending stats to send
		String pendingStat = "";
		DroneStat pendingDroneStat = senderDrone.getPendingDroneStat();
		if (pendingDroneStat != null) {
			pendingStat = new Gson().toJson(pendingDroneStat);
		}

		PendingStatRequest request = PendingStatRequest.newBuilder().setDroneID(senderDrone.getDroneID())
				.setDronePositionX(senderDrone.getDronePosition()[0]).setDronePositionY(senderDrone.getDronePosition()[1])
				.setBatteryLevel(senderDrone.getBatteryLevel()).setIsDelivering(senderDrone.isDelivering())
				.setIsCharging(senderDrone.isCharging()).setDroneStat(pendingStat).build();


		stub.sendPendingDroneStat(request, new StreamObserver<PendingStatResponse>() {
			@Override
			public void onNext(PendingStatResponse value) {
				if (value.getMasterResponse().equalsIgnoreCase("OK")) {
					System.out.println("Received ok from master");
					// If master is online, we can safely clear pending stats
					senderDrone.setPendingDroneStat(null);
					senderDrone.notifyPendingStatQuittingMux();
				}
			}

			// If this is triggered, then new master is already down so we start an election
			@Override
			public void onError(Throwable t) {
				System.out.println("New master is down! Couldn't send pending stat\nRestarting election...");
				senderDrone.setNoMasterDrone();
				senderDrone.removeFromNetwork(receiverDrone);
				senderDrone.setParticipant(true);
				DroneInfo nextDrone = senderDrone.getNextInRing();
				DroneServiceThread serviceThread =
						new DroneServiceThread(senderDrone, nextDrone, senderDrone.getBatteryLevel(), senderDrone.getDroneID());
				serviceThread.start();

				channel.shutdown();
			}

			@Override
			public void onCompleted() {
				channel.shutdown();
			}
		});

		//you need this. otherwise the method will terminate before that answers from the server are received
		channel.awaitTermination(10, TimeUnit.SECONDS);
	}

	public void check() throws InterruptedException {
		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		DroneServiceStub stub = DroneServiceGrpc.newStub(channel);

		CheckMessage request = CheckMessage.newBuilder().setMessage("OK").build();

		stub.check(request, new StreamObserver<CheckMessage>() {
			@Override
			public void onNext(CheckMessage value) {
				System.out.println("Check ok");
			}

			// Check if the drone who's down was the master
			@Override
			public void onError(Throwable t) {
				if (senderDrone.getMasterDrone().getDroneID() == receiverDrone.getDroneID()) {
					System.out.println("Master down after check. Starting election...");
					senderDrone.setNoMasterDrone();
					senderDrone.removeFromNetwork(receiverDrone);
					senderDrone.setParticipant(true);
					DroneInfo nextDrone = senderDrone.getNextInRing();
					DroneServiceThread serviceThread =
							new DroneServiceThread(senderDrone, nextDrone, senderDrone.getBatteryLevel(), senderDrone.getDroneID());
					serviceThread.start();
				} else {
					System.out.println("Drone " + receiverDrone.getDroneID() + " down after check.");
					senderDrone.removeFromNetwork(receiverDrone);
				}
				channel.shutdown();
			}

			@Override
			public void onCompleted() {
				channel.shutdown();
			}
		});

		//you need this. otherwise the method will terminate before that answers from the server are received
		channel.awaitTermination(10, TimeUnit.SECONDS);
	}

	/*
	// This resolves the edge case in which the elected message is past the new drone in the ring
	private void lookForMaster() throws InterruptedException {
		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		DroneServiceStub stub = DroneServiceGrpc.newStub(channel);

		CheckMessage request = CheckMessage.newBuilder().setMessage("OK").build();

		stub.lookForMaster(request, new StreamObserver<LookForMasterResponse>() {
			@Override
			public void onNext(LookForMasterResponse value) {
				// If the master has been elected, we set the master
				if (senderDrone.getMasterDrone() == null && value.getIsMaster()) {
					//senderDrone.setMasterDroneByID(value.getDroneID());
					senderDrone.setMasterDrone(receiverDrone);
				}

				// If someone is still participant, we set the ongoing election true and try
				// to find the master again later
				if (!senderDrone.isElectionInProgress() && value.getIsParticipant()) {
					senderDrone.setElectionInProgress(true);
				}
			}

			@Override
			public void onError(Throwable t) {
				senderDrone.removeFromNetwork(receiverDrone);
				channel.shutdown();
			}

			@Override
			public void onCompleted() {
				channel.shutdown();
			}
		});

		//you need this. otherwise the method will terminate before that answers from the server are received
		channel.awaitTermination(10, TimeUnit.SECONDS);
	}

	 */

}
