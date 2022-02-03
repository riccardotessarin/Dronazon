package smartcitydrone;

import beans.DroneInfo;

import java.util.ArrayList;
import java.util.List;

public class TokenLossCheckThread extends Thread {
	private DroneProperty droneProperty;
	private boolean quit = false;

	public TokenLossCheckThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {
		int numberOfDrones = droneProperty.getDronesInNetwork().size();
		int roundOver = 5000;

		// Assuming the worst case scenario, it will take 2*n messages to receive
		// the elected message with which it will become non participant. We add some seconds for possible
		// failed connections with crashed drones.
		int timeToWait = numberOfDrones * 2000 + roundOver;
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (quit) {
			System.out.println("Drone is no longer participant, closing thread");
			return;
		}

		if (!droneProperty.isParticipant()) {
			System.out.println("Drone not participant, closing thread");
			return;
		}

		// If it reaches here, then most likely we had a token loss, so we restart the election
		droneProperty.restartElection();
	}

	public void quit() {
		quit = true;
	}
}
