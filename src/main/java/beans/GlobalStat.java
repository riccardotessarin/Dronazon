package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlobalStat {
	private String timestamp;
	private double averageDeliveriesNumber;
	private double averageTraveledKM;
	private double averagePM;
	private double averageBatteryLevel;

	public GlobalStat(){}

	public GlobalStat(String timestamp, double averageDeliveriesNumber, double averageTraveledKM, double averagePM, double averageBatteryLevel) {
		this.timestamp = timestamp;
		this.averageDeliveriesNumber = averageDeliveriesNumber;
		this.averageTraveledKM = averageTraveledKM;
		this.averagePM = averagePM;
		this.averageBatteryLevel = averageBatteryLevel;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public double getAverageDeliveriesNumber() {
		return averageDeliveriesNumber;
	}

	public void setAverageDeliveriesNumber(double averageDeliveriesNumber) {
		this.averageDeliveriesNumber = averageDeliveriesNumber;
	}

	public double getAverageTraveledKM() {
		return averageTraveledKM;
	}

	public void setAverageTraveledKM(double averageTraveledKM) {
		this.averageTraveledKM = averageTraveledKM;
	}

	public double getAveragePM() {
		return averagePM;
	}

	public void setAveragePM(double averagePM) {
		this.averagePM = averagePM;
	}

	public double getAverageBatteryLevel() {
		return averageBatteryLevel;
	}

	public void setAverageBatteryLevel(double averageBatteryLevel) {
		this.averageBatteryLevel = averageBatteryLevel;
	}

	@Override
	public String toString() {
		return "\nTime: " + timestamp + "\nAvg. deliveries " + averageDeliveriesNumber + ", avg. km " + averageTraveledKM
				+ " avg. PM " + averagePM + " avg. battery " + averageBatteryLevel;
	}
}
