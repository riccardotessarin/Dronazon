package smartcitydrone;

import beans.DroneInfo;

public class DroneSafeQuitThread extends Thread {
	private DroneProperty droneProperty;

	public DroneSafeQuitThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {

		// If the drone is master, we need to do some additional stuff...
		if (droneProperty.isMaster()) {
			// Disconnect from MQTT broker
			droneProperty.getMasterThread().disconnectMQTT();

			//TODO: Assign pending deliveries to drones

		}

		// End drone delivery (if any)
		if (droneProperty.isDelivering()) {
			//TODO: Wait for thread that handles the delivery to finish
		}

		//TODO: Pass delivery, PM stats and drone new info to master (just save if this is master)

		// Close connection with drones network
		droneProperty.getServerThread().closeServer();

		//TODO: If master, send global stats to S.A.

		// Ask Server Amministratore permission to exit
		String serverAddress = "http://localhost:1337";
		String deletePath = "/amministratore/remove";
		// The function needs a new DroneInfo since the list managed by S.A. is all of new (it wouldn't find it)
		DroneInfo droneInfo = new DroneInfo(droneProperty.getDroneID(), droneProperty.getIpAddress(), droneProperty.getPort());
		Drone.removeRequest(droneProperty.getClientHTTP(), serverAddress + deletePath, droneInfo);

		// Terminate the process
		System.exit(0);
	}
}
