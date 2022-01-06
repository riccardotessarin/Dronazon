package beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class DroneInfos {

	@XmlElement(name = "drones_info")
	private List<DroneInfo> dronesInfo;

	private static DroneInfos instance;

	private DroneInfos() {
		dronesInfo = new ArrayList<DroneInfo>();
	}

	//With a singleton we ensure that only one list of drones
	//will be active, it calls the constructor once
	public synchronized static DroneInfos getInstance() {
		if (instance == null) {
			instance = new DroneInfos();
		}
		return instance;
	}

	public List<DroneInfo> getDronesInfo() {
		return new ArrayList<>(dronesInfo);
	}

	public void setDronesInfo(List<DroneInfo> dronesInfo) {
		this.dronesInfo = dronesInfo;
	}

	// If the insert succeeds it returns true
	public synchronized boolean addDroneInfo(DroneInfo droneInfo) {
		if (findDroneInfo(droneInfo.getDroneID()) == null) {
			dronesInfo.add(droneInfo);
			return true;
		}
		return false;
	}

	// If it finds a drone with the same ID it returns the drone info
	public DroneInfo findDroneInfo(int droneID) {
		List<DroneInfo> dronesInfoCopy = getDronesInfo();
		for (DroneInfo dInfo: dronesInfoCopy) {
			//if (dInfo.getDroneID().equalsIgnoreCase(droneID.toLowerCase())) {
			if (dInfo.getDroneID() == droneID) {
				return dInfo;
			}
		}
		return null;
	}
}
