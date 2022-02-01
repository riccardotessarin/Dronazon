package smartcitydrone;

import beans.DroneInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryCheckThread extends Thread {
	private DroneProperty droneProperty;
	private boolean quit = false;

	public DeliveryCheckThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {
		while(!quit) {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!quit) {
				System.out.println("Checking deliveries integrity...");

				List<DroneInfo> deliveringDrones = droneProperty.getDronesInNetwork()
						.stream().filter(droneInfo -> droneProperty.isDelivering()).collect(Collectors.toList());

				// We check all the drones who are currently delivering looking for crashed drones
				if (deliveringDrones.size() != 0) {
					List<DroneServiceThread> threads = new ArrayList<>();

					for (DroneInfo droneInfo : deliveringDrones) {
						// This is the master, we want to exclude it (since it is obviously online)
						if (droneInfo.getDroneID() != droneProperty.getDroneID()) {
							DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneInfo, "check");
							serviceThread.start();
							threads.add(serviceThread);
						}
					}

					// Joining all threads
					for (DroneServiceThread thread : threads) {
						try {
							thread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void quit() {
		quit = true;
	}

	public DroneProperty getDroneProperty() {
		return droneProperty;
	}

	public void setDroneProperty(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}
}
