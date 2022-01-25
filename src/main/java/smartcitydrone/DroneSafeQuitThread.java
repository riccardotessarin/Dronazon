package smartcitydrone;

import beans.DroneInfo;
import smartcity.OrderData;

public class DroneSafeQuitThread extends Thread {
	private DroneProperty droneProperty;

	public DroneSafeQuitThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {

		// When it finishes all possible deliveries it closes the print stats thread
		if (droneProperty.getPrintThread() != null) {
			droneProperty.getPrintThread().quit();
			droneProperty.setPrintThread(null);
		}

		// Close check thread
		if (droneProperty.getCheckThread() != null) {
			droneProperty.getCheckThread().quit();
			droneProperty.setCheckThread(null);
		}

		// End drone delivery (if any)
		if (droneProperty.isDelivering() && droneProperty.getDeliveryThread() != null) {
			try {
				droneProperty.getDeliveryThread().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			droneProperty.setDeliveryThread(null);
		}

		// Wait for any possible ongoing election to finish
		if (droneProperty.isParticipant()) {
			synchronized (droneProperty.getPendingElectionMux()) {
				try {
					droneProperty.getPendingElectionMux().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// If the drone is master, we need to do some additional stuff...
		// We place it here because this can also become master AFTER the last election ended
		if (droneProperty.isMaster() && droneProperty.getMasterThread() != null) {
			assignPendingOrders();

			// End drone delivery (if any)
			if (droneProperty.isDelivering() && droneProperty.getDeliveryThread() != null) {
				try {
					droneProperty.getDeliveryThread().join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				droneProperty.setDeliveryThread(null);
			}
		}

		// If it still has pending stats to send, we wait for the drone to safely send them to the master (or store them
		// if this is master)
		if (droneProperty.getPendingDroneStat() != null) {
			synchronized (droneProperty.getPendingStatQuittingMux()) {
				try {
					droneProperty.getPendingStatQuittingMux().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (droneProperty.getSensorThread() != null) {
			droneProperty.getSensorThread().stopMeGently();
			droneProperty.setSensorThread(null);
		}

		// Close connection with drones network
		if (droneProperty.getServerThread() != null) {
			droneProperty.getServerThread().closeServer();
			droneProperty.setServerThread(null);
		}

		// If it is master and it still has global stats to send, we wait for it to send them to S.A.
		if (droneProperty.isMaster() && droneProperty.getMasterStatThread() != null) {
			if (droneProperty.getPendingDronesStatistics().size() > 0) {
				synchronized (droneProperty.getPostPendingStatMux()) {
					try {
						droneProperty.getPostPendingStatMux().wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (droneProperty.getMasterStatThread() != null) {
				droneProperty.getMasterStatThread().quit();
				droneProperty.setMasterStatThread(null);
			}

			// Grpc network already finds master offline when we close the server thread
			if (droneProperty.getMasterThread() != null) {
				droneProperty.setMasterThread(null);
			}
		}

		// Ask Server Amministratore permission to exit
		String serverAddress = "http://localhost:1337";
		String deletePath = "/amministratore/remove";
		Drone.removeRequest(droneProperty.getClientHTTP(), serverAddress + deletePath, droneProperty.getDroneID());

		// Terminate the process
		System.exit(0);
	}

	private void assignPendingOrders() {
		// Disconnect from MQTT broker
		droneProperty.getMasterThread().disconnectMQTT();

		// Here we need to wait either for a new drone to join or all the drones currently delivering and available
		// to handle all of the orders from the pending list
		// With this we dispatch orders to all the free drones without stopping, and then we resume only if a new
		// drone joins or all the orders get the order from the pending list by themselves
		while (droneProperty.getOrdersQueue().size() > 0) {
			System.out.println("Sending new order from pending list before master quits...");
			OrderData order = droneProperty.getOrderFromQueue();

			// This time, since we're quitting the network we want to be sure that each order will be delivered
			// so instead of just adding it back to the queue and hope for someone to take it, we wait for the
			// first drone who tells us it is available again
			DroneInfo bestDrone = droneProperty.getBestDeliveryDrone(order.getPickUpPoint());
			if (bestDrone == null) {
				System.out.println("No drone currently available for delivery, waiting for a drone...");
				droneProperty.addToOrdersQueue(order);
				synchronized (droneProperty.getDroneForDeliveryMux()) {
					try {
						droneProperty.getDroneForDeliveryMux().wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, bestDrone, order);
				serviceThread.start();
				try {
					serviceThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
