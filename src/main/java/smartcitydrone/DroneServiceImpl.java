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
				.setIsMaster(droneProperty.isMaster()).setIsElecting(droneProperty.isElecting()).build();

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

	public DroneProperty getDroneProperty() {
		return droneProperty;
	}

	public void setDroneProperty(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}
}
