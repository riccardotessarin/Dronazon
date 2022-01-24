package smartcitydrone;

import beans.DroneInfo;
import beans.GlobalStat;
import com.sun.jersey.api.client.ClientResponse;

import static java.lang.System.exit;
import static smartcitydrone.Drone.postRequest;

public class DroneMasterStatThread  extends Thread {
	private DroneProperty droneProperty;
	private boolean quit = false;
	String postPath = "/amministratore/send_stat";
	String serverAddress = "http://localhost:1337";

	public DroneMasterStatThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {
		while(!quit) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			GlobalStat globalStat = droneProperty.produceGlobalStat();

			// Posting global stats to S.A.
			ClientResponse clientResponse = postRequest(droneProperty.getClientHTTP(), serverAddress + postPath, globalStat);
			if (clientResponse == null){
				exit(0);
			}

			// If we have no more stats to post to S.A. and the master is quitting, we let it quit
			if (droneProperty.isQuitting() && droneProperty.getPendingDronesStatistics().size() == 0) {
				droneProperty.notifyPostPendingStatMux();
			}

			System.out.println(clientResponse.toString());

		}
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
