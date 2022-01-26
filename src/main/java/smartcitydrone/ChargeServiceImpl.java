package smartcitydrone;

import com.google.gson.Gson;
import com.smartcitydrone.chargeservice.ChargeServiceGrpc.ChargeServiceImplBase;
import com.smartcitydrone.chargeservice.ChargeServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

public class ChargeServiceImpl extends ChargeServiceImplBase {
	private DroneProperty droneProperty;

	public ChargeServiceImpl(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void charge(ChargeRequest request, StreamObserver<ChargeResponse> responseObserver) {
		ChargeResponse response = null;

		// If this drone doesn't need to charge, it just answers ok
		if (!droneProperty.isCharging() && !droneProperty.isWaitingCharge()) {
			System.out.println("Not using charge");
			response = ChargeResponse.newBuilder().setResponse("OK").build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
			return;
		}

		String chargeMessage = request.getRequest();
		ChargeInfo chargeInfo = new Gson().fromJson(chargeMessage, ChargeInfo.class);

		// If this is the same drone, we close connection without telling OK right away (we save in the list with the response)
		if (droneProperty.getDroneID() == chargeInfo.getDroneID()) {
			response = ChargeResponse.newBuilder().setResponse("ME").build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
			return;
		}

		// If the drone is charging it closes communications without telling OK
		if (droneProperty.isCharging()) {
			response = ChargeResponse.newBuilder().setResponse("CHARGING").build();
			droneProperty.addToChargingQueue(chargeInfo);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
			return;
		}

		// If the drone is waiting and the timestamp received is smaller it tells OK,
		// otherwise it closes communications without telling OK
		if (droneProperty.isWaitingCharge() && droneProperty.getChargeThread() != null) {
			if (chargeInfo.getTimestamp() < droneProperty.getChargeThread().getChargeInfo().getTimestamp()) {
				response = ChargeResponse.newBuilder().setResponse("OK").build();
			} else {
				response = ChargeResponse.newBuilder().setResponse("BETTER").build();
			}
		} else {
			System.out.println("Charge thread unavailable");
			response = ChargeResponse.newBuilder().setResponse("OK").build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
			return;
		}

		droneProperty.addToChargingQueue(chargeInfo);

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
