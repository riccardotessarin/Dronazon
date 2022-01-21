package smartcitydrone;

public class DronePrintThread extends Thread {
	private DroneProperty droneProperty;
	private boolean quit = false;

	public DronePrintThread(DroneProperty droneProperty) {
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

			droneProperty.printStat();
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
