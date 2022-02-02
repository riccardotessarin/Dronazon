package smartcitydrone;

import com.smartcitydrone.crashservice.CrashServiceGrpc.CrashServiceImplBase;
import com.smartcitydrone.crashservice.CrashServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

public class CrashServiceImpl extends CrashServiceImplBase {
	private DroneProperty droneProperty;

	public CrashServiceImpl(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	// Drone just gives an ok, meaning it's still online
	@Override
	public void checkCharge(CheckMessage request, StreamObserver<CheckMessage> responseObserver) {
		CheckMessage response = CheckMessage.newBuilder().setMessage("OK").build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
