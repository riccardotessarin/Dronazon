package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DroneInfo {
	private int droneID;
	private String ipAddress;
	private int port;

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

	@Override
	public String toString() {
		return "\nDrone " + droneID + ".\nAvailable for connection on " + ipAddress + " with port " + port + "\n";
	}
}
