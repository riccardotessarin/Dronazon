package Useless;

import smartcitydrone.Drone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Drones {

	@XmlElement(name = "smart_city")
	private List<Drone> smartCity;

	private static Drones instance;

	private Drones() {
		smartCity = new ArrayList<Drone>();
	}

	//With a singleton we ensure that only one list of drones
	//will be active, it calls the constructor once
	public synchronized static Drones getInstance() {
		if (instance == null) {
			instance = new Drones();
		}
		return instance;
	}

	public List<Drone> getSmartCity() {
		return new ArrayList<>(smartCity);
	}

	public void setSmartCity(List<Drone> smartCity) {
		this.smartCity = smartCity;
	}

	public synchronized void addDrone(Drone d) {

		smartCity.add(d);
	}
/*
	public Drone findDroneByID(String droneID) {
		List<Drone> smartCityCopy = getSmartCity();
		for (Drone d: smartCityCopy) {
			if (d.getDroneID().equalsIgnoreCase(droneID.toLowerCase())) {
				return d;
			}
		}
		return null;
	}
*/

}
