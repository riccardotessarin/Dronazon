package smartcitydrone;

import beans.DroneInfo;
import com.smartcitydrone.droneservice.DroneServiceGrpc;
import com.smartcitydrone.droneservice.DroneServiceGrpc.*;
import com.smartcitydrone.droneservice.DroneServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DroneServiceThread extends Thread {
	private String message;
	private DroneProperty senderDrone;
	private DroneInfo receiverDrone;

	public DroneServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.message = "join";
	}



	@Override
	public void run() {
		try {
			joinNetwork();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void joinNetwork() throws InterruptedException {
		System.out.println("Sending a request to join the grpc network");

		final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();

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
						.filter(dInfo -> dInfo.getDroneID() == receiverDrone.getDroneID()).findFirst().get();
				droneInfo.setBatteryLevel(value.getBatteryLevel());

				// TODO: set the master
				// TODO: set if there is an ongoing election

			}

			// If it doesn't receive a response, it means the receiver drone is offline so we remove it
			@Override
			public void onError(Throwable t) {
				System.out.println("Error! " + t.getMessage());
				// TODO: remove drone from network
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
}
