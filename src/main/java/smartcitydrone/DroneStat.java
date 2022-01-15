package smartcitydrone;

import java.sql.Timestamp;
import java.util.List;

public class DroneStat {
	String timestamp;
	private int[] dronePosition;
	double kmTraveled;
	List<Double> averageBufferPM;
	int batteryLeft;

	public DroneStat(String timestamp, int[] dronePosition, double kmTraveled, List<Double> averageBufferPM, int batteryLeft) {
		this.timestamp = timestamp;
		this.dronePosition = dronePosition;
		this.kmTraveled = kmTraveled;
		this.averageBufferPM = averageBufferPM;
		this.batteryLeft = batteryLeft;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int[] getDronePosition() {
		return dronePosition;
	}

	public void setDronePosition(int[] dronePosition) {
		this.dronePosition = dronePosition;
	}

	public double getKmTraveled() {
		return kmTraveled;
	}

	public void setKmTraveled(double kmTraveled) {
		this.kmTraveled = kmTraveled;
	}

	public List<Double> getAverageBufferPM() {
		return averageBufferPM;
	}

	public void setAverageBufferPM(List<Double> averageBufferPM) {
		this.averageBufferPM = averageBufferPM;
	}

	public int getBatteryLeft() {
		return batteryLeft;
	}

	public void setBatteryLeft(int batteryLeft) {
		this.batteryLeft = batteryLeft;
	}
}
