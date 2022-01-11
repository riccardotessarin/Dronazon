package smartcitydrone;

import beans.DroneInfo;
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

		// Here the receiver drone elaborates the join request from the new drone
		DroneInfo droneInfo = new DroneInfo(request.getDroneID(), request.getIpAddress(), request.getPort());
		droneInfo.setDronePosition(new int[]{request.getDronePositionX(), request.getDronePositionY()});

		// So it can be added to the grpc network
		droneProperty.addToNetwork(droneInfo);

		JoinResponse response = JoinResponse.newBuilder()
				.setDroneID(droneProperty.getDroneID()).setDronePositionX(droneProperty.getDronePosition()[0])
				.setDronePositionY(droneProperty.getDronePosition()[1]).setBatteryLevel(droneProperty.getBatteryLevel())
				.setIsMaster(droneProperty.isMaster()).setIsElecting(droneProperty.isElecting()).build();

		// Give response to stream
		responseObserver.onNext(response);

		// Complete and end communication
		responseObserver.onCompleted();
	}




	public DroneProperty getDroneProperty() {
		return droneProperty;
	}

	public void setDroneProperty(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}
}
