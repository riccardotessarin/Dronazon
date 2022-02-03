package smartcitydrone;

import beans.DroneInfo;
import com.smartcitydrone.crashservice.CrashServiceGrpc;
import com.smartcitydrone.crashservice.CrashServiceGrpc.*;
import com.smartcitydrone.crashservice.CrashServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class CrashServiceThread extends Thread {
	private DroneProperty senderDrone;
	private DroneInfo receiverDrone;
	private String message;

	public CrashServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone, String message) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			if (message.equalsIgnoreCase("checkcharge")) {
				checkCharge();
			} else if (message.equalsIgnoreCase("restartelection")) {
				restartElection();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void checkCharge() throws InterruptedException {
		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		CrashServiceStub stub = CrashServiceGrpc.newStub(channel);

		CheckMessage request = CheckMessage.newBuilder().setMessage("OK").build();

		System.out.println("Checking drone in " + receiverDrone.getDroneID());

		stub.checkCharge(request, new StreamObserver<CheckMessage>() {
			@Override
			public void onNext(CheckMessage value) {
				System.out.println("Drone waiting for charge is online");
			}

			@Override
			public void onError(Throwable t) {
				if (!senderDrone.isParticipant() && senderDrone.getMasterDrone().getDroneID() == receiverDrone.getDroneID()) {
					System.out.println("Master waiting for charge is down. Starting election...");
					senderDrone.removeFromNetwork(receiverDrone);
					senderDrone.startElection();
				} else {
					System.out.println("Drone " + receiverDrone.getDroneID() + " waiting for charge is down.");
					senderDrone.removeFromNetwork(receiverDrone);
				}
				senderDrone.removeFromChargingQueue(receiverDrone.getDroneID());
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

	public void restartElection() throws InterruptedException {
		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		CrashServiceStub stub = CrashServiceGrpc.newStub(channel);

		ResetElection request = ResetElection.newBuilder().setMessage("OK").build();

		stub.restartElection(request, new StreamObserver<ResetElection>() {
			@Override
			public void onNext(ResetElection value) {
				System.out.println("Sent correctly");
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Drone is down, removing it");
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
}
