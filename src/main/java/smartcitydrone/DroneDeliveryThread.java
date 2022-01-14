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

		// Or maybe send them separate?
		double orderKM = droneProperty.distance(droneProperty.getDronePosition(), orderData.getPickUpPoint()) +
				droneProperty.distance(orderData.getPickUpPoint(), orderData.getDeliveryPoint());

		// This also updates position inside network
		droneProperty.setDronePosition(orderData.getDeliveryPoint());
		String deliveryTime = new Timestamp(System.currentTimeMillis()).toString();

		// After every delivery the drone loses 10% of battery
		int batteryLeft = droneProperty.getBatteryLevel() - 10;
		droneProperty.setBatteryLevel(batteryLeft);
		droneProperty.updateBatteryLevelInNetwork();

		//TODO: Add PM measurements

		System.out.println("Delivery completed");

		//TODO: Send data to master and update drones status in network

		droneProperty.setDelivering(false);

		if (batteryLeft < 15) {
			System.out.println("Low battery detected!");
		}
	}
}
