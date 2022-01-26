package smartcitydrone;

import beans.DroneInfo;
import beans.GlobalStat;
import com.sun.jersey.api.client.Client;
import sensors.Measurement;
import sensors.PM10Simulator;
import smartcity.OrderData;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Timestamp;
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
	private boolean isParticipant = false;
	private boolean electionInProgress = false;
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

	// For charging functionality
	private boolean isCharging = false;
	private boolean isWaitingCharge = false;
	private DroneChargeThread chargeThread = null;
	private Object chargeInfoMux = new Object();
	private List<ChargeInfo> chargingQueue;
	private Object chargeQueueMux = new Object();

	// Variables for mutex lock
	private Object masterMux = new Object();
	private Object kmMux = new Object();
	private Object delCountMux = new Object();
	private Object pendingStatMux = new Object();
	private Object batteryMux = new Object();
	private Object participantMux = new Object();
	private Object ongoingElectionMux = new Object();
	private Object droneForDeliveryMux = new Object();
	private Object deliveringMux = new Object();
	private Object quittingMux = new Object();
	private Object pendingElectionMux = new Object();
	private Object pendingStatQuittingMux = new Object();
	private Object postPendingStatMux = new Object();

	// Invoked threads handlers, used to call class functions from outside class
	private DroneMasterThread masterThread = null;
	private DroneServerThread serverThread = null;
	private DroneMasterStatThread masterStatThread = null;
	private DroneDeliveryThread deliveryThread = null;
	private DroneSafeQuitThread safeQuitThread = null;
	private DroneCheckThread checkThread = null;
	private DronePrintThread printThread = null;
	private PM10Simulator sensorThread = null;

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
		this.chargingQueue = new ArrayList<>();
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
		this.chargingQueue = new ArrayList<>();
	}
	//endregion

	//region Utility functions
	public DroneInfo getNextInRing() {
		DroneInfo thisDrone = findDroneInfoByID(this.getDroneID());
		synchronized (dronesInNetwork) {
			if (dronesInNetwork.size() > 1) {
				int idx = dronesInNetwork.indexOf(thisDrone);
				if (idx == dronesInNetwork.size()-1) {
					return dronesInNetwork.get(0);
				} else {
					return dronesInNetwork.get(idx + 1);
				}
			} else {
				return thisDrone;
			}
		}
	}

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
					.setDelivering(delivering);
		}
	}

	public double distance(int[] dronePosition, int[] pickUpPoint) {
		double x = Math.pow((pickUpPoint[0] - dronePosition[0]), 2);
		double y = Math.pow((pickUpPoint[1] - dronePosition[1]), 2);
		return Math.sqrt(x + y);
	}

	public void printStat() {
		System.out.println("Drone " + droneID + " deliveries stat:\n" +
				"Total deliveries: " + getDeliveryCount() + ", Distance traveled: " +
				getTraveledKM() + " Km, Battery left: " + getBatteryLevel() + "%");
	}

	public void notifyDroneForDelivery() {
		synchronized (droneForDeliveryMux) {
			droneForDeliveryMux.notifyAll();
		}
	}

	public void notifyPendingElectionMux() {
		synchronized (pendingElectionMux) {
			pendingElectionMux.notifyAll();
		}
	}

	public void notifyPendingStatQuittingMux() {
		synchronized (pendingStatQuittingMux) {
			pendingStatQuittingMux.notifyAll();
		}
	}

	public void notifyPostPendingStatMux() {
		synchronized (postPendingStatMux) {
			postPendingStatMux.notifyAll();
		}
	}
	//endregion

	//region Drone network functions
	public void addToNetwork(DroneInfo droneInfo) {
		DroneInfo droneCheck = findDroneInfoByID(droneInfo.getDroneID());
		/*
		//This may be useless
		if (dronesInNetwork.contains(droneInfo)) {
			System.out.println("Drone " + droneInfo.getDroneID() + " already in network");
			return;
		}

		 */

		// If a drone with the same ID is already inside the network, it means the S.A. let it join because
		// the duplicate ID has already left and my network is not updated. So we remove the duplicate before
		// adding the new one
		if (droneCheck != null) {
			System.out.println("Drone with same ID in network, removing it before letting same ID join");
			removeFromNetwork(droneCheck);
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
		// We check if a master thread is already active, this avoids duplicate master threads
		if (this.masterThread != null) {
			return;
		}

		// Set the master as itself, it will be useful when new drones want to know who's master
		setMaster(true);
		setMasterDroneByID(this.droneID);

		// All of the new deliveries produced by publisher meanwhile will just be ignored, so no need to get them
		// BUT we still need to get the stats produced by the drones while there was no master
		// If the new master had pending stats to send, we just save them to the list, the other stats will come
		// with grpc messages
		if (getPendingDroneStat() != null) {
			addDroneStat(getPendingDroneStat());
			setPendingDroneStat(null);
			notifyPendingStatQuittingMux();
		}

		masterThread = new DroneMasterThread(this);
		masterThread.start();
		masterStatThread = new DroneMasterStatThread(this);
		masterStatThread.start();
	}


	// Check if some locks are needed
	public void quit() {
		if (getSafeQuitThread() == null) {
			this.setSafeQuitThread(new DroneSafeQuitThread(this));
			safeQuitThread.start();
		}
	}

	// This function returns a list of drones currently not delivering nor charging
	public List<DroneInfo> getAvailableDronesInNetwork() {
		List<DroneInfo> dronesInNetworkCopy = getDronesInNetwork();
		return dronesInNetworkCopy.stream()
				.filter(droneInfo -> !droneInfo.isDelivering() && !droneInfo.isCharging() && droneInfo.getBatteryLevel() > 0)
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

	// Function for master drone, this produces global stats to send to S.A.
	public GlobalStat produceGlobalStat() {
		List<DroneStat> dronesStatisticsCopy = getDronesStatistics();
		int deliveriesCount = dronesStatisticsCopy.size();
		int numberOfDrones =  getDronesInNetwork().size();

		if (deliveriesCount == 0) {
			System.out.println("No deliveries made");
			return new GlobalStat(System.currentTimeMillis(), 0.0,0.0,0.0,0.0);
		} else if (numberOfDrones == 0) {
			System.out.println("No more drones inside the network! Error!");
			return new GlobalStat(System.currentTimeMillis(), 0.0,0.0,0.0,0.0);
		}

		double averageDeliveriesNumber = (double) deliveriesCount / numberOfDrones;

		// These need to get values from each of the stat
		double averageTraveledKM = 0.0;
		double averagePM = 0.0;
		int pmCount = 0;
		double averageBatteryLevel = 0.0;

		for (DroneStat droneStat : dronesStatisticsCopy) {
			averageTraveledKM += droneStat.getKmTraveled();
			averagePM += droneStat.getAverageBufferPM().stream().mapToDouble(f -> f).sum();
			pmCount += droneStat.getAverageBufferPM().size();
			averageBatteryLevel += droneStat.getBatteryLeft();
		}

		averageTraveledKM = averageTraveledKM / numberOfDrones;
		if (pmCount != 0) {
			averagePM = averagePM / pmCount;
		} else {
			System.out.println("No PM data found in stats");
			averagePM = 0.0;
		}
		averageBatteryLevel = averageBatteryLevel / deliveriesCount;

		long timestamp = System.currentTimeMillis();
		GlobalStat globalStat = new GlobalStat(timestamp, averageDeliveriesNumber, averageTraveledKM, averagePM, averageBatteryLevel);

		System.out.println(globalStat);

		return globalStat;
	}

	/*
	public void  clearPendingStat() {
		synchronized (pendingStatMux) {
			this.pendingDroneStat = null;
		}
	}

	 */
	//endregion

	//region Drone Charge
	// This JUST ASKS to charge, permission to charge will be granted later
	public void charge() {
		if(getChargeThread() == null && !isCharging() && !isWaitingCharge()) {
			//setCharging(true);
			//setDroneIsCharging(this.droneID, true);
			setWaitingCharge(true);

			ChargeInfo chargeInfo = makeChargeInfo();

			setChargeThread(new DroneChargeThread(this, chargeInfo));
			getChargeThread().start();
		}
	}

	private ChargeInfo makeChargeInfo() {
		synchronized (chargeInfoMux) {
			return new ChargeInfo(this.droneID, System.currentTimeMillis());
		}
	}

	public void setDroneIsCharging(int droneID, boolean isCharging) {
		DroneInfo droneDelivery = findDroneInfoByID(droneID);
		if (droneDelivery == null) {
			System.out.println("Couldn't find the drone inside the list, don't know if it's charging");
			return;
		}
		synchronized (dronesInNetwork) {
			droneDelivery.setCharging(isCharging);
		}
	}

	public void addToChargingQueue(ChargeInfo chargeInfo) {
		synchronized (chargingQueue) {
			if (isCharging() || isWaitingCharge()) {
				if (chargingQueue.stream().filter(charge -> charge.getDroneID() == chargeInfo.getDroneID()).findFirst().orElse(null) != null) {
					return;
				}
				chargingQueue.add(chargeInfo);
				chargingQueue.sort(Comparator.comparingLong(ChargeInfo::getTimestamp));
			}
		}
	}

	public void notifyChargeQueueMux() {
		synchronized (chargeQueueMux) {
			chargeQueueMux.notifyAll();
		}
	}
	//endregion

	//region Getters & Setters
	public int getBatteryLevel() {
		synchronized (batteryMux) {
			return batteryLevel;
		}
	}

	public void setBatteryLevel(int batteryLevel) {
		synchronized (batteryMux) {
			this.batteryLevel = batteryLevel;
		}
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
		synchronized (masterMux) {
			return isMaster;
		}
	}

	public void setMaster(boolean master) {
		synchronized (masterMux) {
			isMaster = master;
		}
	}

	public DroneInfo getMasterDrone() {
		return masterDrone;
	}

	public void setMasterDrone(DroneInfo masterDrone) {
		this.setElectionInProgress(false);
		if (dronesInNetwork.contains(masterDrone)) {
			synchronized (masterMux) {
				this.masterDrone = masterDrone;
			}
		}
	}

	// Custom setter
	public void setMasterDroneByID(int masterDroneID) {
		this.setElectionInProgress(false);
		DroneInfo masterDrone = findDroneInfoByID(masterDroneID);
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

	public boolean isParticipant() {
		synchronized (participantMux) {
			return isParticipant;
		}
	}

	public void setParticipant(boolean participant) {
		synchronized (participantMux) {
			isParticipant = participant;
		}
	}

	public boolean isElectionInProgress() {
		synchronized (ongoingElectionMux) {
			return electionInProgress;
		}
	}

	public void setElectionInProgress(boolean electionInProgress) {
		synchronized (ongoingElectionMux) {
			this.electionInProgress = electionInProgress;
		}
	}

	public boolean isDelivering() {
		synchronized (deliveringMux) {
			return isDelivering;
		}
	}

	public void setDelivering(boolean delivering) {
		synchronized (deliveringMux) {
			isDelivering = delivering;
		}
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

	public DroneMasterStatThread getMasterStatThread() {
		return masterStatThread;
	}

	public void setMasterStatThread(DroneMasterStatThread masterStatThread) {
		this.masterStatThread = masterStatThread;
	}

	public DroneDeliveryThread getDeliveryThread() {
		synchronized (deliveringMux) {
			return deliveryThread;
		}
	}

	public void setDeliveryThread(DroneDeliveryThread deliveryThread) {
		synchronized (deliveringMux) {
			this.deliveryThread = deliveryThread;
		}
	}

	public DroneSafeQuitThread getSafeQuitThread() {
		synchronized (quittingMux) {
			return safeQuitThread;
		}
	}

	public void setSafeQuitThread(DroneSafeQuitThread safeQuitThread) {
		synchronized (quittingMux) {
			this.safeQuitThread = safeQuitThread;
		}
	}

	public boolean isQuitting() {
		if (this.getSafeQuitThread() != null) {
			return true;
		} else {
			return false;
		}
	}

	public List<OrderData> getOrdersQueue() {
		synchronized (ordersQueue) {
			return ordersQueue;
		}
	}

	public void setOrdersQueue(List<OrderData> ordersQueue) {
		synchronized (ordersQueue) {
			this.ordersQueue = ordersQueue;
		}
	}

	// Whenever we need to retrieve the buffer, we also want to clear it (every completed delivery)
	public List<Double> getAverageBufferPM() {
		synchronized (averageBufferPM) {
			List<Double> averageBufferPMCopy = new ArrayList<>(averageBufferPM);
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

	// We get the stats list when we want to send them to S.A., when it happens we also want to clear the list
	public List<DroneStat> getDronesStatistics() {
		synchronized (dronesStatistics) {
			List<DroneStat> dronesStatisticsCopy = new ArrayList<>(dronesStatistics);
			dronesStatistics.clear();
			return dronesStatisticsCopy;
		}
	}

	// This is for the quitting master drone
	public List<DroneStat> getPendingDronesStatistics() {
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

	public boolean isCharging() {
		return isCharging;
	}

	public void setCharging(boolean charging) {
		isCharging = charging;
	}

	public Object getDroneForDeliveryMux() {
		return droneForDeliveryMux;
	}

	public void setDroneForDeliveryMux(Object droneForDeliveryMux) {
		this.droneForDeliveryMux = droneForDeliveryMux;
	}

	public Object getPendingElectionMux() {
		return pendingElectionMux;
	}

	public void setPendingElectionMux(Object pendingElectionMux) {
		this.pendingElectionMux = pendingElectionMux;
	}

	public Object getPendingStatQuittingMux() {
		return pendingStatQuittingMux;
	}

	public void setPendingStatQuittingMux(Object pendingStatQuittingMux) {
		this.pendingStatQuittingMux = pendingStatQuittingMux;
	}

	public Object getPostPendingStatMux() {
		return postPendingStatMux;
	}

	public void setPostPendingStatMux(Object postPendingStatMux) {
		this.postPendingStatMux = postPendingStatMux;
	}

	public DroneCheckThread getCheckThread() {
		return checkThread;
	}

	public void setCheckThread(DroneCheckThread checkThread) {
		this.checkThread = checkThread;
	}

	public DronePrintThread getPrintThread() {
		return printThread;
	}

	public void setPrintThread(DronePrintThread printThread) {
		this.printThread = printThread;
	}

	public PM10Simulator getSensorThread() {
		return sensorThread;
	}

	public void setSensorThread(PM10Simulator sensorThread) {
		this.sensorThread = sensorThread;
	}

	public DroneChargeThread getChargeThread() {
		return chargeThread;
	}

	public void setChargeThread(DroneChargeThread chargeThread) {
		this.chargeThread = chargeThread;
	}

	public boolean isWaitingCharge() {
		return isWaitingCharge;
	}

	public void setWaitingCharge(boolean waitingCharge) {
		isWaitingCharge = waitingCharge;
	}

	public List<ChargeInfo> getChargingQueue() {
		synchronized (chargingQueue) {
			return chargingQueue;
		}
	}

	public void setChargingQueue(List<ChargeInfo> chargingQueue) {
		synchronized (chargingQueue) {
			this.chargingQueue = chargingQueue;
		}
	}

	public Object getChargeQueueMux() {
		return chargeQueueMux;
	}

	public void setChargeQueueMux(Object chargeQueueMux) {
		this.chargeQueueMux = chargeQueueMux;
	}

	//endregion
}
