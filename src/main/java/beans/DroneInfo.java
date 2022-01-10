package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DroneInfo {
	private int droneID;
	private String ipAddress;
	private int port;

	// Stats useless for init but used for the network list that each drone keeps
	private int batteryLevel = 100;
	private int[] dronePosition = new int[]{-1,-1};

	// Useful variables for message exchange
	private boolean isCharging;
	private boolean isDelivering;

	public DroneInfo(){}

	public DroneInfo(int droneID, String ipAddress, int port) {
		this.droneID = droneID;
		this.ipAddress = ipAddress;
		this.port = port;
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
	}

	@Override
	public String toString() {
		return "\nDrone " + droneID + ".\nAvailable for connection on " + ipAddress + " with port " + port + "\n";
	}
}
