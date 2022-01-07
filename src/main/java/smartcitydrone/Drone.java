package smartcitydrone;

import beans.DroneInfo;
import beans.InitDroneInfo;
import com.google.gson.Gson;
import com.sun.jersey.api.client.*;
import org.eclipse.paho.client.mqttv3.*;
import smartcity.OrderData;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.StringTokenizer;

import static java.lang.System.exit;

// The drone is a subscriber (for now). It will get the orders from Dronazon.
public class Drone {
	public static void main(String[] args) throws IOException {
		// Drone properties
		DroneProperty droneProperty;

		// Connection to the HTTP server
		Client clientHTTP = Client.create();
		String serverAddress = "http://localhost:1337";
		ClientResponse clientResponse = null;

		droneProperty = new DroneProperty(clientHTTP);

		//region ID & port set by user
		/*
		// For duplicate drones exception testing
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Insert the drone ID:");
		int droneID = Integer.parseInt(bufferedReader.readLine());

		// For explicit port assignment
		System.out.println("Insert the drone ID:");
		int port = Integer.parseInt(bufferedReader.readLine());

		droneProperty = new DroneProperty(droneID, port, clientHTTP);
		 */
		//endregion

		// Posting drone info
		String postPath = "/amministratore/insert";
		DroneInfo dInfo = new DroneInfo(droneProperty.getDroneID(), droneProperty.getIpAddress(), droneProperty.getPort());
		clientResponse = insertRequest(clientHTTP, serverAddress + postPath, dInfo);
		if (clientResponse == null){
			exit(0);
		}
		if (clientResponse.getStatus() == 409) {
			System.out.println("A drone with the same ID is already in the network.");
			exit(0);
		}

		InitDroneInfo initDroneInfo = clientResponse.getEntity(InitDroneInfo.class);
		// Assign drone starting point
		droneProperty.setDronePosition(initDroneInfo.getX(), initDroneInfo.getY());
		// Set the list of drones in the smart city received by the server
		droneProperty.setDronesInNetwork(initDroneInfo.getDronesInNetwork());

		// If it's the only drone in the network it automatically becomes master
		if (droneProperty.getDronesInNetwork().size() == 1) {
			droneProperty.makeMaster();
		}

		DroneServerThread serverThread = new DroneServerThread(droneProperty);
		serverThread.start();



		// **** Start of MQTT stuff ****
		// This will be active ONLY for the master drone

		// Data for the MQTT subscriber
		MqttClient client;
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

					System.out.println("\n ***  Press a random key to exit *** \n");
				}

				// Not useful for now
				@Override
				public void deliveryComplete(IMqttDeliveryToken token) { }
			});

			// **** End of MQTT stuff ****

			System.out.println(clientId + " Subscribing ... - Thread PID: " + Thread.currentThread().getId());
			client.subscribe(topic,qos);
			System.out.println(clientId + " Subscribed to topics : " + topic);


			System.out.println("\n ***  Press a random key to exit *** \n");
			Scanner command = new Scanner(System.in);
			command.nextLine();
			client.disconnect();

		} catch (MqttException me ) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		} /*catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	public static ClientResponse insertRequest (Client client, String url, DroneInfo dInfo) {
		WebResource webResource = client.resource(url);
		String input = new Gson().toJson(dInfo);
		try {
			return webResource.type("application/json").post(ClientResponse.class, input);
		} catch (ClientHandlerException e) {
			System.out.println("Server unavailable");
			return null;
		}
	}

	public static ClientResponse removeRequest (Client client, String url, DroneInfo dInfo) {
		WebResource webResource = client.resource(url);
		String input = new Gson().toJson(dInfo);
		try {
			return webResource.type("application/json").delete(ClientResponse.class, input);
		} catch (ClientHandlerException e) {
			System.out.println("Server unavailable");
			return null;
		}
	}
}
