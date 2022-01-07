package smartcitydrone;

import beans.DroneInfo;
import com.sun.jersey.api.client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

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
	}


	// Constructor with ID and socket port defined by the user
	public DroneProperty(int droneID, int port, Client clientHTTP) {
		this.droneID = droneID;
		this.ipAddress = "localhost";
		this.port = port;
		this.clientHTTP = clientHTTP;
	}
	//endregion

	//region Drone functions
	public void makeMaster() {
		// Set the master as itself so it will know not to send message to self
		isMaster = true;
		this.masterDrone = new DroneInfo(this.droneID, this.ipAddress, this.port);
	}

	public void quit() {
		//TODO: Start thread that handles the safe drone removal from network
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
		return dronesInNetwork;
	}

	public void setDronesInNetwork(List<DroneInfo> dronesInNetwork) {
		this.dronesInNetwork = dronesInNetwork;
	}
	//endregion
}
