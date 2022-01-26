package smartcitydrone;

public class ChargeInfo {
	private int droneID;
	private long timestamp;

	public ChargeInfo(int droneID, long timestamp) {
		this.droneID = droneID;
		this.timestamp = timestamp;
	}

	public int getDroneID() {
		return droneID;
	}

	public void setDroneID(int droneID) {
		this.droneID = droneID;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != ChargeInfo.class) return false;
		ChargeInfo myChargeInfo = (ChargeInfo) obj;
		return this.droneID == myChargeInfo.droneID;
	}
}
