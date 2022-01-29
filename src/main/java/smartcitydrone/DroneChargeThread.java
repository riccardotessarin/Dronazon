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

		// The drone can't charge if it is delivering, and can't deliver if it is charging
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

		// If it is participant in an election, there's no point in telling the master since master is down
		// If this is master it already knows when it starts to charge
		if (!droneProperty.isParticipant() && !droneProperty.isMaster()) {
			ChargeServiceThread startChargeThread =
					new ChargeServiceThread(droneProperty, droneProperty.getMasterDrone(), "startcharge");
			startChargeThread.start();
		}

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

		droneProperty.removeFromChargingQueue(droneProperty.getDroneID());

		// If this was the only drone who wanted to charge then there's nothing more to do
		// We just send to the master drone the end of charge message
		// Charging queue is already empty so there's nothing to clear and no one else to inform
		if (droneProperty.getChargingQueue().size() == 0) {
			if (!droneProperty.isMaster() && !droneProperty.isParticipant() && droneProperty.getMasterDrone() != null) {
				ChargeServiceThread serviceThread = new ChargeServiceThread(droneProperty, droneProperty.getMasterDrone(), "endcharge");
				serviceThread.start();
				try {
					serviceThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return;
		}

		// Sending to the drones GRPC network the charge request message in broadcast (myself included)
		List<ChargeServiceThread> threadList = new ArrayList<>();
		List<ChargeInfo> chargeInfos = droneProperty.getChargingQueue();
		List<DroneInfo> dronesToCall = new ArrayList<>();
		dronesInNetworkCopy = droneProperty.getDronesInNetwork(); // The list may be different now

		// If this drone is (or has become meanwhile) master, it isn't inside the list because we removed it earlier
		// anyway. If this is master we don't need to send anything because it already updated its properties
		if (!droneProperty.isMasterInChargingQueue() && !droneProperty.isMaster()) {
			System.out.println("Adding master to queue");
			dronesToCall.add(droneProperty.getMasterDrone());
		}

		for (ChargeInfo chargeInfo : chargeInfos) {
			dronesInNetworkCopy.stream().filter(c -> c.getDroneID() == chargeInfo.getDroneID()).findFirst().ifPresent(dronesToCall::add);
		}

		for (DroneInfo droneInfo : dronesToCall) {
			ChargeServiceThread serviceThread = new ChargeServiceThread(droneProperty, droneInfo, "endcharge");
			serviceThread.start();
			threadList.add(serviceThread);
		}

		// Joining all threads
		for (ChargeServiceThread thread : threadList) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		droneProperty.clearChargingQueue();
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
