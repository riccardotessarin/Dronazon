package smartcitydrone;

import smartcity.OrderData;

import java.sql.Timestamp;

public class DroneDeliveryThread extends Thread {
	private DroneProperty droneProperty;
	private OrderData orderData;

	public DroneDeliveryThread(DroneProperty droneProperty, OrderData orderData) {
		this.droneProperty = droneProperty;
		this.orderData = orderData;
	}

	@Override
	public void run() {
		System.out.println("Delivery started");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String timeOfArrival = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("Delivery completed");

		double orderKM = droneProperty.distance(droneProperty.getDronePosition(), orderData.getPickUpPoint()) +
				droneProperty.distance(orderData.getPickUpPoint(), orderData.getDeliveryPoint());
		droneProperty.incrementTraveledKM(orderKM);

		// This also updates position inside network
		droneProperty.setDronePosition(orderData.getDeliveryPoint());
		droneProperty.incrementDeliveryCount();

		// After every delivery the drone loses 10% of battery
		int batteryLeft = droneProperty.getBatteryLevel() - 10;
		droneProperty.setBatteryLevel(batteryLeft);
		droneProperty.updateBatteryLevelInNetwork();

		DroneStat droneStat = new DroneStat(timeOfArrival,
				droneProperty.getDronePosition(), orderKM,
				droneProperty.getAverageBufferPM(), droneProperty.getBatteryLevel());

		DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneProperty.getMasterDrone(), droneStat);
		serviceThread.start();

		try {
			serviceThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Since the master could be using this as well, we wait for the thread that sends stats to finish before upd
		droneProperty.setDelivering(false);

		if (batteryLeft < 15) {
			System.out.println("Low battery detected!\nQuitting network...");
			droneProperty.quit();
		}
	}
}
