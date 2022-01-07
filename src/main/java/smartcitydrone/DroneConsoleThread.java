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
			//System.out.println("Input a command for the drone:");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String consoleInput = br.readLine();
				if (consoleInput.equalsIgnoreCase("quit")) {
					droneProperty.quit();
					System.out.println("Preparing to remove drone from network...");
					quit = true;
				} else {
					System.out.println("Unknown command.");
				}
				/*
				TODO: recharge console input
				else if (consoleInput.equalsIgnoreCase("recharge"))

				 */
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
