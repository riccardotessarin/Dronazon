package beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InitDroneInfo {

	@XmlElement(name = "drones_in_network")
	private List<DroneInfo> dronesInNetwork;
	private int x;
	private int y;

	// This is necessary for the un/marshalling
	public InitDroneInfo() {}

	public InitDroneInfo(List<DroneInfo> dronesInNetwork) {
		this.dronesInNetwork = dronesInNetwork;
		this.x = (int) (Math.random() * 10);;
		this.y = (int) (Math.random() * 10);;
	}

	public List<DroneInfo> getDronesInNetwork() {
		return dronesInNetwork;
	}

	public void setDronesInNetwork(List<DroneInfo> dronesInNetwork) {
		this.dronesInNetwork = dronesInNetwork;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
