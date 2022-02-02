package smartcitydrone;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class DroneServerThread extends Thread {

	Server server;
	private DroneProperty droneProperty;

	public DroneServerThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {

		try {
			server = ServerBuilder.forPort(droneProperty.getPort()).addService(new DroneServiceImpl(droneProperty))
					.addService(new ChargeServiceImpl(droneProperty))
					.addService(new CrashServiceImpl(droneProperty)).build();
			server.start();

			System.out.println("Drone server started!");
			server.awaitTermination();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void closeServer() {
		if (server != null) {
			System.out.println("Closing drone's grpc server...");
			server.shutdown();
		}
	}
}
