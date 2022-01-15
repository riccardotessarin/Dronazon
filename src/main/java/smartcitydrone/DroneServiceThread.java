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


	@Override
	public void run() {
		try {
			if (message.equalsIgnoreCase("join")) {
				joinNetwork();
			} else if (message.equalsIgnoreCase("delivery")) {
				dispatchOrder();
			} else if (message.equalsIgnoreCase("stat")) {
				sendDroneStat();
			}
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

				// If the drone who wants to join doesn't already know the master
				// and the one who is talking with it is master, set the master
				if (senderDrone.getMasterDrone() == null && value.getIsMaster()) {
					//senderDrone.setMasterDroneByID(value.getDroneID());
					senderDrone.setMasterDrone(receiverDrone);
				}

				// If the drone who wants to join doesn't already know there is an ongoing election
				// and the one who is talking with it is in election, set the election true
				if (!senderDrone.isElecting() && value.getIsElecting()) {
					senderDrone.setElecting(true);
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
				if (value.getDroneAvailable().equalsIgnoreCase("BUSY")) {
					System.out.println("Drone answered with BUSY response, adding order to queue");
					senderDrone.addToOrdersQueue(orderData);
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
				//TODO: Place stats in a pending place, remove master local info and start an election
				channel.shutdownNow();
			}

			@Override
			public void onCompleted() {
				channel.shutdownNow();
			}
		});

	}
}
