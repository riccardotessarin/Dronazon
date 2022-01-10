package smartcitydrone;

import com.smartcitydrone.droneservice.DroneServiceGrpc.DroneServiceImplBase;
import com.smartcitydrone.droneservice.DroneServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

public class DroneServiceImpl extends DroneServiceImplBase {
	private DroneProperty droneProperty;

	public DroneServiceImpl(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}


	@Override
	public void joinNetwork(JoinRequest request, StreamObserver<JoinResponse> responseObserver) {
		System.out.println(request);

		JoinResponse response = JoinResponse.newBuilder().build();

		//passo la risposta nello stream
		responseObserver.onNext(response);

		//completo e finisco la comunicazione
		responseObserver.onCompleted();
	}




	public DroneProperty getDroneProperty() {
		return droneProperty;
	}

	public void setDroneProperty(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}
}
