package smartcitydrone;

import beans.DroneInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DroneCheckThread extends Thread {
	private DroneProperty droneProperty;
	private DroneInfo droneInfo;
	private boolean quickCheck;
	private boolean quit = false;

	public DroneCheckThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
		this.quickCheck = false;
	}

	public DroneCheckThread(DroneProperty droneProperty, DroneInfo droneInfo) {
		this.droneProperty = droneProperty;
		this.droneInfo = droneInfo;
		this.quickCheck = true;
	}

	@Override
	public void run() {
		if(!quickCheck) {
			while(!quit) {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if(droneProperty.isMaster()) {
					System.out.println("I'm master now so I'm closing check thread");
					quit();
					return;
				}

				System.out.println("Started check thread");

				if (droneProperty.getMasterDrone() != null && !droneProperty.isParticipant() && !quit) {
					startCheck(droneProperty, droneProperty.getMasterDrone());
				}
			}
		} else {
			System.out.println("Doing a quick check");
			startCheck(droneProperty, droneInfo);
		}
	}

	private void startCheck(DroneProperty droneProperty, DroneInfo droneInfo) {
		DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneInfo, "check");
		serviceThread.start();
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
