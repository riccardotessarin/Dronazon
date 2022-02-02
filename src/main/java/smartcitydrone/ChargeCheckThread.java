package smartcitydrone;

import beans.DroneInfo;

import java.util.ArrayList;
import java.util.List;

public class ChargeCheckThread extends Thread {
	private DroneProperty droneProperty;
	private ChargeInfo chargeInfo;
	private boolean quit = false;

	public ChargeCheckThread(DroneProperty droneProperty, ChargeInfo chargeInfo) {
		this.droneProperty = droneProperty;
		this.chargeInfo = chargeInfo;
	}

	@Override
	public void run() {
		while (droneProperty.getChargingQueueBeforeMe(chargeInfo) != null) {
			List<ChargeInfo> chargingBeforeMe = droneProperty.getChargingQueueBeforeMe(chargeInfo);
			if(chargingBeforeMe == null) {
				System.out.println("Drone is first in charging list, so no need to check, closing thread");
				break;
			}
			int dronesBeforeMe = chargingBeforeMe.size();
			int roundOver = 5000;

			// We make it wait for the charging time multiplied by the number of drones in queue before this one
			int timeToWait = dronesBeforeMe * 10000 + roundOver;
			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (quit) {
				System.out.println("Drone is not in charging queue anymore, all went well, closing thread");
				break;
			}

			// Retrieve again the list of drones before me
			chargingBeforeMe = droneProperty.getChargingQueueBeforeMe(chargeInfo);
			if(chargingBeforeMe == null) {
				System.out.println("Drone is first in charging list, so no need to check, closing thread");
				break;
			}

			// Retrieving drone infos to call check on them
			List<CrashServiceThread> threadList = new ArrayList<>();
			List<DroneInfo> dronesToCall = new ArrayList<>();
			List<DroneInfo> dronesInNetworkCopy = droneProperty.getDronesInNetwork();

			for (ChargeInfo chargeInfo : chargingBeforeMe) {
				DroneInfo drone = dronesInNetworkCopy.stream().filter(c -> c.getDroneID() == chargeInfo.getDroneID()).findFirst().orElse(null);
				if (drone == null) {
					System.out.println("Drone is already outside network, so I'm removing it from charge list");
					droneProperty.removeFromChargingQueue(chargeInfo.getDroneID());
				} else {
					dronesToCall.add(drone);
				}
			}

			for (DroneInfo droneInfo : dronesToCall) {
				CrashServiceThread serviceThread = new CrashServiceThread(droneProperty, droneInfo, "checkcharge");
				serviceThread.start();
				threadList.add(serviceThread);
			}

			// Joining all threads
			for (CrashServiceThread thread : threadList) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void quit() {
		quit = true;
	}
}
