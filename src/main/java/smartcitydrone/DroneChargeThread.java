package smartcitydrone;

import beans.DroneInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DroneChargeThread extends Thread {
	private DroneProperty droneProperty;
	private ChargeInfo chargeInfo;

	public DroneChargeThread(DroneProperty droneProperty, ChargeInfo chargeInfo) {
		this.droneProperty = droneProperty;
		this.chargeInfo = chargeInfo;
	}

	@Override
	public void run() {
		// Sending to the drones GRPC network the charge request message in broadcast (myself included)
		List<ChargeServiceThread> threads = new ArrayList<>();
		List<DroneInfo> dronesInNetworkCopy = droneProperty.getDronesInNetwork();

		for (DroneInfo droneInfo : dronesInNetworkCopy) {
			ChargeServiceThread serviceThread = new ChargeServiceThread(droneProperty, droneInfo, chargeInfo);
			serviceThread.start();
			threads.add(serviceThread);
		}

		// Joining all threads
		for (ChargeServiceThread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		while (droneProperty.getChargingQueue().size() > 0 && !droneProperty.getChargingQueue().get(0).equals(chargeInfo)) {
			try {
				System.out.println("WAITING");
				synchronized (droneProperty.getChargeQueueMux()) {
					droneProperty.getChargeQueueMux().wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (droneProperty.getDeliveryThread() != null && droneProperty.isDelivering()) {
			try {
				droneProperty.getDeliveryThread().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		droneProperty.setWaitingCharge(false);
		droneProperty.setCharging(true);

		System.out.println("CHARGE START");


		//TODO: Send message to master telling it has started the charge (so this is unavailable for deliveries)
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("CHARGE STOP");

		droneProperty.setDronePosition(new int[]{0, 0});
		droneProperty.setBatteryLevel(100);
		droneProperty.updateBatteryLevelInNetwork();

		droneProperty.setCharging(false);

		//TODO: Send end of charge message to all waiting and master
	}

	public DroneProperty getDroneProperty() {
		return droneProperty;
	}

	public void setDroneProperty(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	public ChargeInfo getChargeInfo() {
		return chargeInfo;
	}

	public void setChargeInfo(ChargeInfo chargeInfo) {
		this.chargeInfo = chargeInfo;
	}
}
