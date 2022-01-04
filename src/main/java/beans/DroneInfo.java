package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DroneInfo {
	private String droneID;
	private String ipAddress;
	private int port;

	public DroneInfo(){}

	public DroneInfo(String droneID, String ipAddress, int port) {
		this.droneID = droneID;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getDroneID() {
		return droneID;
	}

	public void setDroneID(String droneID) {
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
}
