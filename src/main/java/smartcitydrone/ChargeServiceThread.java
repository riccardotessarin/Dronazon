package smartcitydrone;

import beans.DroneInfo;

import com.google.gson.Gson;
import com.smartcitydrone.chargeservice.ChargeServiceGrpc;
import com.smartcitydrone.chargeservice.ChargeServiceGrpc.*;
import com.smartcitydrone.chargeservice.ChargeServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class ChargeServiceThread extends Thread {
	private String message;
	private DroneProperty senderDrone;
	private DroneInfo receiverDrone;
	private ChargeInfo chargeInfo;

	public ChargeServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone, ChargeInfo chargeInfo) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.chargeInfo = chargeInfo;
		this.message = "charge";
	}

	public ChargeServiceThread(DroneProperty senderDrone, DroneInfo receiverDrone) {
		this.senderDrone = senderDrone;
		this.receiverDrone = receiverDrone;
		this.message = "endcharge";
	}

	@Override
	public void run() {
		try {
			if (message.equalsIgnoreCase("charge")) {
				charge();
			} else if (message.equalsIgnoreCase("endcharge")) {
				endCharge();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	// This message is sent in broadcast to tell the network need for charge
	private void charge() throws InterruptedException {
		System.out.println("Sending a charge request");

		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		ChargeServiceStub stub = ChargeServiceGrpc.newStub(channel);

		String chargeMessage = new Gson().toJson(chargeInfo);
		ChargeRequest request = ChargeRequest.newBuilder().setRequest(chargeMessage).build();

		stub.charge(request, new StreamObserver<ChargeResponse>() {
			@Override
			public void onNext(ChargeResponse value) {
				if (!value.getResponse().equalsIgnoreCase("OK")) {
					// We add the drone to the charging queue with -1 as timestamp since we don't know its timestamp,
					// but it is surely better than ours
					if (receiverDrone.getDroneID() != senderDrone.getDroneID()) {
						System.out.println("received nothing, so it's using");
						senderDrone.addToChargingQueue(new ChargeInfo(receiverDrone.getDroneID(), -1));
					} else {
						senderDrone.addToChargingQueue(chargeInfo);
					}
				}
				System.out.println("received OK");
			}

			@Override
			public void onError(Throwable t) {
				if(receiverDrone.getDroneID() == senderDrone.getMasterDrone().getDroneID() && !senderDrone.isParticipant()) {
					senderDrone.setNoMasterDrone();
					senderDrone.removeFromNetwork(receiverDrone);
					senderDrone.setParticipant(true);
					DroneInfo nextDrone = senderDrone.getNextInRing();
					DroneServiceThread serviceThread =
							new DroneServiceThread(senderDrone, nextDrone, senderDrone.getBatteryLevel(), senderDrone.getDroneID());
					serviceThread.start();
				} else {
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

	private void endCharge() throws InterruptedException {
		System.out.println("Sending end of charge message");

		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget(receiverDrone.getIpAddress() + ":" + receiverDrone.getPort()).usePlaintext().build();

		//creating an asynchronous stub on the channel
		ChargeServiceStub stub = ChargeServiceGrpc.newStub(channel);

		String chargeMessage = "end";
		ChargeRequest request = ChargeRequest.newBuilder().setRequest(chargeMessage).build();

		stub.charge(request, new StreamObserver<ChargeResponse>() {
			@Override
			public void onNext(ChargeResponse value) {

			}

			@Override
			public void onError(Throwable t) {
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

	public DroneProperty getSenderDrone() {
		return senderDrone;
	}

	public void setSenderDrone(DroneProperty senderDrone) {
		this.senderDrone = senderDrone;
	}

	public DroneInfo getReceiverDrone() {
		return receiverDrone;
	}

	public void setReceiverDrone(DroneInfo receiverDrone) {
		this.receiverDrone = receiverDrone;
	}

	public ChargeInfo getChargeInfo() {
		return chargeInfo;
	}

	public void setChargeInfo(ChargeInfo chargeInfo) {
		this.chargeInfo = chargeInfo;
	}
}
