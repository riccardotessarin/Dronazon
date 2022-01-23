package smartcitydrone;

import beans.DroneInfo;

import java.util.ArrayList;
import java.util.List;

public class DroneLookForMasterThread extends Thread {
	private DroneProperty droneProperty;

	public DroneLookForMasterThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {
		while(droneProperty.isElectionInProgress()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// If meanwhile we got info about the master or we still don't know but the drone is already
			// participant in a new election we just close this thread (it will get the info anyway)
			if(droneProperty.getMasterDrone() != null || droneProperty.isParticipant()) {
				System.out.println("Got info or new election in progress, no need to look for master");
				droneProperty.setElectionInProgress(false);
				return;
			}

			if (droneProperty.getMasterDrone() == null) {
				// If it still doesn't know, it looks for master info
				System.out.println("Looking for master info on my own");

				// Sending to the drones GRPC network the look for master message
				List<DroneServiceThread> threads = new ArrayList<>();
				List<DroneInfo> dronesInNetworkCopy = droneProperty.getDronesInNetwork();

				// If this is the only one left, then it becomes master
				if (dronesInNetworkCopy.size() == 1) {
					droneProperty.makeMaster();
					return;
				}

				for (DroneInfo droneInfo : dronesInNetworkCopy) {
					if (droneInfo.getDroneID() != droneProperty.getDroneID()) {
						DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneInfo, "lookformaster");
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

				// If meanwhile we got info about the master or we still don't know but the drone is already
				// participant in a new election we just close this thread (it will get the info anyway)
				if(droneProperty.getMasterDrone() != null || droneProperty.isParticipant()) {
					System.out.println("Got info or new election in progress, no need to look for master");
					droneProperty.setElectionInProgress(false);
					return;
				}
			}
		}
	}
}
