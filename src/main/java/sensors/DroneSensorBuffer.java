package sensors;

import smartcitydrone.DroneProperty;

import java.util.ArrayList;
import java.util.List;

public class DroneSensorBuffer implements Buffer {
	private DroneProperty droneProperty;
	private List<Measurement> bufferPM;
	private int overlap = 4;    // The window has 8 measurements and we consider it with a 50% overlap

	public DroneSensorBuffer(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
		this.bufferPM = new ArrayList<>();
	}

	@Override
	public void addMeasurement(Measurement m) {
		synchronized (bufferPM) {
			bufferPM.add(m);
			// With sync we should immediately catch when it's at 8, but just to be sure
			if (bufferPM.size() > 7) {
				droneProperty.addSensorMeasurement(readAllAndClean());
			}

		}
	}

	@Override
	public List<Measurement> readAllAndClean() {
		List<Measurement> bufferPMCopy = getBufferPM();
		synchronized (bufferPM) {
			if (bufferPM.size() > overlap) {
				bufferPM = bufferPM.subList(overlap, bufferPM.size()); // We remove the first 4 elements
			} else {
				// If it comes here the average could be wrong
				System.out.println("WARNING: Extracting buffer with less than half-window size!");
				bufferPM.clear();
			}
		}

		// We return 8 elements of the measurements list
		if (bufferPMCopy.size() < 9) {
			return bufferPMCopy;
		} else {
			return bufferPMCopy.subList(0, 8);
		}
	}


	public DroneProperty getDroneProperty() {
		return droneProperty;
	}

	public void setDroneProperty(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	public List<Measurement> getBufferPM() {
		synchronized (bufferPM) {
			return bufferPM;
		}
	}

	public void setBufferPM(List<Measurement> bufferPM) {
		this.bufferPM = bufferPM;
	}
}
