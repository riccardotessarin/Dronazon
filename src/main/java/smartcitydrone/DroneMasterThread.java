package smartcitydrone;

import org.eclipse.paho.client.mqttv3.*;
import smartcity.OrderData;

import java.sql.Timestamp;
import java.util.Scanner;
import java.util.StringTokenizer;

// This will be active ONLY for the master drone
// The master drone is an MQTT subscriber. It will get the orders from Dronazon and dispatch them to drones.
public class DroneMasterThread extends Thread {
	private DroneProperty droneProperty;
	MqttClient client;

	public DroneMasterThread(DroneProperty droneProperty) {
		this.droneProperty = droneProperty;
	}

	@Override
	public void run() {
		// Data for the MQTT subscriber
		String broker = "tcp://localhost:1883";
		String clientId = MqttClient.generateClientId();
		String topic = "dronazon/smartcity/orders";
		int qos = 2;

		try {
			client = new MqttClient(broker, clientId);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

			// Connect the client
			System.out.println(clientId + " Connecting Broker " + broker);
			client.connect(connOpts);
			System.out.println(clientId + " Connected - Thread PID: " + Thread.currentThread().getId());

			client.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable cause) {
					System.out.println(clientId + " Connectionlost! cause:" + cause.getMessage()+ "-  Thread PID: " + Thread.currentThread().getId());
				}

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					// Called when a message arrives from the server that matches any subscription made by the client
					String time = new Timestamp(System.currentTimeMillis()).toString();
					String receivedMessage = new String(message.getPayload());
					System.out.println(clientId +" Received a Message! - Callback - Thread PID: " + Thread.currentThread().getId() +
							"\n\tTime:    " + time +
							"\n\tTopic:   " + topic +
							"\n\tMessage: " + receivedMessage +
							"\n\tQoS:     " + message.getQos() + "\n");

					// Get the order variables from the received message
					StringTokenizer st = new StringTokenizer(receivedMessage, " :,");
					int orderID = Integer.parseInt(st.nextToken());
					int x1 = Integer.parseInt(st.nextToken());
					int y1 = Integer.parseInt(st.nextToken());
					int x2 = Integer.parseInt(st.nextToken());
					int y2 = Integer.parseInt(st.nextToken());
					OrderData order = new OrderData(orderID, new int[]{x1,y1}, new int[]{x2,y2});
					System.out.println(order);

					//TODO: Dispatch order to nearest drone and start thread for GRPC
				}

				// Not useful for now
				@Override
				public void deliveryComplete(IMqttDeliveryToken token) { }
			});

			System.out.println(clientId + " Subscribing ... - Thread PID: " + Thread.currentThread().getId());
			client.subscribe(topic,qos);
			System.out.println(clientId + " Subscribed to topics : " + topic);

		} catch (MqttException me ) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
	}

	public void disconnectMQTT() {
		try {
			client.disconnect();
		} catch (MqttException me ) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
	}
}
