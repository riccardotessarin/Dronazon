package smartcity;

public class OrderData {
	// Order data
	private int orderID;
	private int[] pickUpPoint;
	private int[] deliveryPoint;

	public OrderData() {
		this.orderID = 0;
		this.pickUpPoint = new int[2];
		this.deliveryPoint = new int[2];
	}

	public OrderData(int orderID, int[] pickUpPoint, int[] deliveryPoint) {
		this.orderID = orderID;
		this.pickUpPoint = pickUpPoint;
		this.deliveryPoint = deliveryPoint;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public int[] getPickUpPoint() {
		return pickUpPoint;
	}

	public void setPickUpPoint(int[] pickUpPoint) {
		this.pickUpPoint = pickUpPoint;
	}

	public int[] getDeliveryPoint() {
		return deliveryPoint;
	}

	public void setDeliveryPoint(int[] deliveryPoint) {
		this.deliveryPoint = deliveryPoint;
	}

	@Override
	public String toString() {
		return "Order ID: " + orderID + "\n" + "Pick up coordinates: " + pickUpPoint[0] + "," + pickUpPoint[1] + "\n" +
				"Delivery coordinates: " + deliveryPoint[0] + "," + deliveryPoint[1] + "\n";
	}

}
