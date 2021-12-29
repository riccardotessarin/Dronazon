package smartcity;

import org.eclipse.paho.client.mqttv3.*;

// Dronazon is an MQTT publisher that simulates an e-commerce website
// It publishes an order every 5 seconds on topic dronazon/smartcity/orders
public class Dronazon {
	public static void main(String[] args) {
		MqttClient client;
		String broker = "tcp://localhost:1883";
		String clientId = MqttClient.generateClientId();
		String topic = "dronazon/smartcity/orders";
		int qos = 2;
		int x1, x2, y1, y2;
		int orderID = 0;

		try {
			client = new MqttClient(broker, clientId);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			//connOpts.setWill("this/is/a/topic","will message".getBytes(),1,false);  // optional

			// Connect the client
			System.out.println(clientId + " Connecting Broker " + broker);
			client.connect(connOpts);
			System.out.println(clientId + " Connected");

			while(true) {
				// Set random coordinates for pick up and delivery points between 0 and 9
				x1 = (int) (Math.random() * 10);
				x2 = (int) (Math.random() * 10);
				y2 = (int) (Math.random() * 10);
				y1 = (int) (Math.random() * 10);

				// Building the order message
				String order = "" + orderID + ": " + x1 +"," + y1 + " " + x2 + "," + y2;
				MqttMessage message = new MqttMessage(order.getBytes());
				message.setQos(qos);

				// Publishing message
				System.out.println(clientId + " Publishing message: " + order + " ...");
				client.publish(topic, message);
				System.out.println(clientId + " Message published");

				// Dronazon publishes an order every 5 seconds
				Thread.sleep(5000);

				orderID++;
			}



		} catch (MqttException | InterruptedException me ) {
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
	}
}
