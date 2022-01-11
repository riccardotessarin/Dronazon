package smartcitydrone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DroneConsoleThread extends Thread {
	private DroneProperty droneProperty;
	private boolean quit = false;

	public DroneConsoleThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {
		while(!quit) {
			System.out.println("Drone ready for console input:");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String consoleInput = br.readLine();
				if (consoleInput.equalsIgnoreCase("quit")) {
					System.out.println("Preparing to safely remove drone from network...");
					droneProperty.quit();
					quit = true;
				} else {
					System.out.println("Unknown command.");
				}
				/*
				TODO: recharge console input
				else if (consoleInput.equalsIgnoreCase("recharge")) {
					// If the drone isn't charging already and is not into the process of
					// safely exiting from the network, it will try to start the charge
					// Something on the line of:
					if (!droneProperty.getIsCharging() && !droneProperty.getIsQuitting()) {
						droneProperty.startCharging();
						// NO quit = true, because after the charge it will still be in the network
					}
				}

				 */
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
