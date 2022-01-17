package smartcitydrone;

import beans.DroneInfo;
import com.sun.jersey.api.client.Client;
import sensors.Measurement;
import smartcity.OrderData;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DroneProperty {
	// Drone info for the peer to peer connection
	private int droneID;
	private String ipAddress;
	private int port = -1;

	private Client clientHTTP;
	private int batteryLevel = 100;
	private int[] dronePosition = new int[2];

	// Drone network properties
	private List<DroneInfo> dronesInNetwork;
	private boolean isMaster = false;
	private DroneInfo masterDrone = null;
	private boolean isElecting = false;
	private boolean isDelivering = false;
	private List<OrderData> ordersQueue;
	private List<Double> averageBufferPM;
	private double traveledKM = 0.0;
	private int deliveryCount = 0;
	private List<DroneStat> dronesStatistics;
	private DroneStat pendingDroneStat = null;

	// Variables useful for shorter functions
	private DroneInfo thisDroneInfo;
	private int droneNetworkIdx;

	// Variables for mutex lock
	private Object masterMux = new Object();
	private Object kmMux = new Object();
	private Object delCountMux = new Object();
	private Object pendingStatMux = new Object();

	// Invoked threads handlers, used to call class functions from outside class
	private DroneMasterThread masterThread = null;
	private DroneServerThread serverThread = null;

	//region Constructors
	// Constructor with randomly generated ID and socket port
	public DroneProperty(Client clientHTTP) {
		this.droneID = (int) (Math.random() * 100); //The ID is generated between 0 and 99
		this.ipAddress = "localhost";
		try {
			ServerSocket socket = new ServerSocket(0);
			// here's your free port
			this.port = socket.getLocalPort();
			socket.close();
		} catch (IOException ioe) {
			System.out.println("No available sockets.");
		}
		this.clientHTTP = clientHTTP;
		this.ordersQueue = new ArrayList<>();
		this.averageBufferPM = new ArrayList<>();
		this.dronesStatistics = new ArrayList<>();
	}


	// Constructor with ID and socket port defined by the user
	public DroneProperty(int droneID, int port, Client clientHTTP) {
		this.droneID = droneID;
		this.ipAddress = "localhost";
		this.port = port;
		this.clientHTTP = clientHTTP;
		this.ordersQueue = new ArrayList<>();
		this.averageBufferPM = new ArrayList<>();
		this.dronesStatistics = new ArrayList<>();
	}
	//endregion

	//region Utility functions

	// This will make the ring construction and communication easier
	public void sortDronesInNetwork() {
		synchronized (dronesInNetwork) {
			dronesInNetwork.sort(Comparator.comparingInt(DroneInfo::getDroneID));
		}
	}

	// If it finds a drone with the same ID it returns the drone info
	public DroneInfo findDroneInfoByID(int droneID) {
		List<DroneInfo> dronesInfoCopy = getDronesInNetwork();
		for (DroneInfo dInfo: dronesInfoCopy) {
			if (dInfo.getDroneID() == droneID) {
				return dInfo;
			}
		}
		return null;
	}

	// It updates the position of the drone inside the list of drones
	public void updatePositionInNetwork() {
		synchronized (dronesInNetwork) {
			dronesInNetwork.get(dronesInNetwork.indexOf(findDroneInfoByID(this.droneID)))
					.setDronePosition(this.dronePosition);
		}
	}

	public void updateBatteryLevelInNetwork() {
		synchronized (dronesInNetwork) {
			dronesInNetwork.get(dronesInNetwork.indexOf(findDroneInfoByID(this.droneID)))
					.setBatteryLevel(this.batteryLevel);
		}
	}

	public void updateIsDeliveringInNetwork(boolean delivering) {
		synchronized (dronesInNetwork) {
			dronesInNetwork.get(dronesInNetwork.indexOf(findDroneInfoByID(this.droneID)))
					.setDelivering(this.isDelivering);
		}
	}

	public double distance(int[] dronePosition, int[] pickUpPoint) {
		double x = Math.pow((pickUpPoint[0] - dronePosition[0]), 2);
		double y = Math.pow((pickUpPoint[1] - dronePosition[1]), 2);
		return Math.sqrt(x + y);
	}
	//endregion

	//region Drone network functions
	public void addToNetwork(DroneInfo droneInfo) {
		if (dronesInNetwork.contains(droneInfo)) {
			System.out.println("Drone " + droneInfo.getDroneID() + " already in network");
			return;
		}
		System.out.println("GRPC network is adding drone " + droneInfo.getDroneID());
		synchronized (dronesInNetwork) {
			dronesInNetwork.add(droneInfo);
		}
		sortDronesInNetwork();
	}

	public void removeFromNetwork(DroneInfo droneInfo) {
		System.out.println("GRPC network is removing drone " + droneInfo.getDroneID());
		synchronized (dronesInNetwork) {
			dronesInNetwork.remove(droneInfo);
		}
	}

	public void makeMaster() {
		// Set the master as itself, it will be useful when new drones want to know who's master
		synchronized (masterMux) {
			isMaster = true;
			//this.masterDrone = new DroneInfo(this.droneID, this.ipAddress, this.port);
			this.masterDrone = findDroneInfoByID(this.droneID);
		}

		// All of the new deliveries produced by publisher meanwhile will just be ignored, so no need to get them
		// BUT we still need to get the stats produced by the drones while there was no master
		// TODO: Send to drones the end of election message (send all the network info to new master)
		// TODO: New master gets the stats produced from drones while there was no master

		masterThread = new DroneMasterThread(this);
		masterThread.start();
	}


	// Check if some locks are needed
	public void quit() {
		DroneSafeQuitThread safeQuitThread = new DroneSafeQuitThread(this);
		safeQuitThread.start();
	}

	// This function returns a list of drones currently not delivering nor charging
	public List<DroneInfo> getAvailableDronesInNetwork() {
		List<DroneInfo> dronesInNetworkCopy = getDronesInNetwork();
		return dronesInNetworkCopy.stream()
				.filter(droneInfo -> !droneInfo.isDelivering() && !droneInfo.isCharging())
				.collect(Collectors.toList());
	}

	public DroneInfo getBestDeliveryDrone(int[] pickUpPoint) {
		List<DroneInfo> availableDrones = getAvailableDronesInNetwork();

		// If no drone is currently free it returns null
		if (availableDrones.size() == 0) {
			return null;
		} else if (availableDrones.size() == 1) {
			return availableDrones.get(0);  // If there is just one drone, no point in making calculations
		}

		DroneInfo bestDrone = null;
		double min = Double.MAX_VALUE;
		for (DroneInfo droneInfo : availableDrones) {
			double newDistance = distance(droneInfo.getDronePosition(), pickUpPoint);
			if (newDistance < min) {
				min = newDistance;
				bestDrone = droneInfo;
			} else if (newDistance == min) {
				if (bestDrone == null) {
					System.out.println("Uh-oh, something went wrong");
					return null;
				}
				if (droneInfo.getBatteryLevel() < bestDrone.getBatteryLevel()) {
					min = newDistance;
					bestDrone = droneInfo;
				} else if (droneInfo.getBatteryLevel() == bestDrone.getBatteryLevel()) {
					if (bestDrone.getDroneID() < droneInfo.getDroneID()) {
						min = newDistance;
						bestDrone = droneInfo;
					}
				}
			}

		}
		return bestDrone;
	}

	public void updateDroneBatteryLevel(int droneID, int batteryLeft) {
		DroneInfo droneDelivery = findDroneInfoByID(droneID);
		if (droneDelivery == null) {
			System.out.println("Couldn't find the drone inside the list, can't update battery level");
			return;
		}
		synchronized (dronesInNetwork) {
			droneDelivery.setBatteryLevel(batteryLeft);
		}
	}

	public void updateDronePosition(int droneID, int[] dronePosition) {
		DroneInfo droneDelivery = findDroneInfoByID(droneID);
		if (droneDelivery == null) {
			System.out.println("Couldn't find the drone inside the list, can't update position");
			return;
		}
		synchronized (dronesInNetwork) {
			droneDelivery.setDronePosition(dronePosition);
		}
	}

	public void setDroneIsDelivering(int droneID, boolean start) {
		DroneInfo droneDelivery = findDroneInfoByID(droneID);
		if (droneDelivery == null) {
			System.out.println("Couldn't find the drone inside the list, can't update delivery status");
			return;
		}
		synchronized (dronesInNetwork) {
			droneDelivery.setDelivering(start);
		}
	}

	public void addToOrdersQueue(OrderData orderData) {
		synchronized (ordersQueue) {
			ordersQueue.add(orderData);
		}
		System.out.println("Order added to the queue");
	}

	public OrderData getOrderFromQueue() {
		synchronized (ordersQueue) {
			OrderData orderData = ordersQueue.get(0);
			ordersQueue.remove(0);
			return orderData;
		}
	}

	public void addSensorMeasurement(List<Measurement> bufferPM) {
		if (bufferPM.size() == 0) {
			System.out.println("PM Buffer error!");
			return;
		} else if (bufferPM.size() < 8) {
			System.out.println("Buffer has less than 8 elements, the average may be incorrect");
		}

		double sum = 0;
		for (Measurement measurement : bufferPM) {
			sum += measurement.getValue();
		}
		double average = sum/bufferPM.size();

		synchronized (averageBufferPM) {
			averageBufferPM.add(average);
		}
	}

	public void incrementTraveledKM(double orderKM) {
		synchronized (kmMux) {
			this.traveledKM += orderKM;
		}
	}

	public void incrementDeliveryCount() {
		synchronized (delCountMux) {
			this.deliveryCount ++;
		}
	}

	// We don't need to know the drone id when giving the average stats to S.A.
	public void addDroneStat(DroneStat droneStat) {
		synchronized (dronesStatistics) {
			dronesStatistics.add(droneStat);
		}
	}
	//endregion

	//region Getters & Setters
	public int getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public int[] getDronePosition() {
		return dronePosition;
	}

	public void setDronePosition(int[] dronePosition) {
		this.dronePosition = dronePosition;
		updatePositionInNetwork();
	}

	// Setter used on drone initialization
	public void setDronePosition(int dronePositionX, int dronePositionY) {
		this.dronePosition = new int[]{dronePositionX, dronePositionY};
	}

	public int getDroneID() {
		return droneID;
	}

	public void setDroneID(int droneID) {
		this.droneID = droneID;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Client getClientHTTP() {
		return clientHTTP;
	}

	public void setClientHTTP(Client clientHTTP) {
		this.clientHTTP = clientHTTP;
	}

	public List<DroneInfo> getDronesInNetwork() {
		synchronized (dronesInNetwork) {
			return dronesInNetwork;
		}
	}

	public void setDronesInNetwork(List<DroneInfo> dronesInNetwork) {
		this.dronesInNetwork = dronesInNetwork;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean master) {
		isMaster = master;
	}

	public DroneInfo getMasterDrone() {
		return masterDrone;
	}

	public void setMasterDrone(DroneInfo masterDrone) {
		if (dronesInNetwork.contains(masterDrone)) {
			synchronized (masterMux) {
				this.masterDrone = masterDrone;
			}
		}
	}

	// Custom setter
	public void setMasterDroneByID(int masterDroneID) {
		DroneInfo masterDrone = findDroneInfoByID(droneID);
		if (masterDrone != null) {
			synchronized (masterMux) {
				this.masterDrone = masterDrone;
			}
		}
	}

	public void setNoMasterDrone() {
		synchronized (masterMux) {
			this.masterDrone = null;
		}
	}

	public boolean isElecting() {
		return isElecting;
	}

	public void setElecting(boolean electing) {
		isElecting = electing;
	}

	public boolean isDelivering() {
		return isDelivering;
	}

	public void setDelivering(boolean delivering) {
		isDelivering = delivering;
		updateIsDeliveringInNetwork(delivering);
	}

	public DroneInfo getThisDroneInfo() {
		return thisDroneInfo;
	}

	public void setThisDroneInfo(DroneInfo thisDroneInfo) {
		this.thisDroneInfo = thisDroneInfo;
	}

	public int getDroneNetworkIdx() {
		return droneNetworkIdx;
	}

	public void setDroneNetworkIdx(int droneNetworkIdx) {
		this.droneNetworkIdx = droneNetworkIdx;
	}

	public DroneMasterThread getMasterThread() {
		return masterThread;
	}

	public void setMasterThread(DroneMasterThread masterThread) {
		this.masterThread = masterThread;
	}

	public DroneServerThread getServerThread() {
		return serverThread;
	}

	public void setServerThread(DroneServerThread serverThread) {
		this.serverThread = serverThread;
	}

	public List<OrderData> getOrdersQueue() {
		return ordersQueue;
	}

	public void setOrdersQueue(List<OrderData> ordersQueue) {
		this.ordersQueue = ordersQueue;
	}

	// Whenever we need to retrieve the buffer, we also want to clear it (every completed delivery)
	public List<Double> getAverageBufferPM() {
		synchronized (averageBufferPM) {
			List<Double> averageBufferPMCopy = averageBufferPM;
			averageBufferPM.clear();
			return averageBufferPMCopy;
		}
	}

	public void setAverageBufferPM(List<Double> averageBufferPM) {
		this.averageBufferPM = averageBufferPM;
	}

	public double getTraveledKM() {
		synchronized (kmMux) {
			return traveledKM;
		}
	}

	public void setTraveledKM(double traveledKM) {
		this.traveledKM = traveledKM;
	}

	public int getDeliveryCount() {
		synchronized (delCountMux) {
			return deliveryCount;
		}
	}

	public void setDeliveryCount(int deliveryCount) {
		this.deliveryCount = deliveryCount;
	}

	public List<DroneStat> getDronesStatistics() {
		synchronized (dronesStatistics) {
			return dronesStatistics;
		}
	}

	public void setDronesStatistics(List<DroneStat> dronesStatistics) {
		this.dronesStatistics = dronesStatistics;
	}

	public DroneStat getPendingDroneStat() {
		synchronized (pendingStatMux) {
			return pendingDroneStat;
		}
	}

	public void setPendingDroneStat(DroneStat pendingDroneStat) {
		synchronized (pendingStatMux) {
			this.pendingDroneStat = pendingDroneStat;
		}
	}

	//endregion
}
