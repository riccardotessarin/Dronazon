package smartcity;

import beans.DroneInfo;
import beans.InitDroneInfo;
import com.google.gson.Gson;
import com.sun.jersey.api.client.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.eclipse.paho.client.mqttv3.*;

import javax.ws.rs.core.GenericEntity;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

// The drone is a subscriber (for now). It will get the orders from Dronazon.
public class Drone {
	public static void main(String[] args) {
		MqttClient client;
		String broker = "tcp://localhost:1883";
		String clientId = MqttClient.generateClientId();
		String topic = "dronazon/smartcity/orders";
		int qos = 2;

		String ipAddress = "ciao";
		int port = 6798;

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

			// Like an HTTP client
			Client clientHTTP = Client.create();
			String serverAddress = "http://localhost:1337";
			ClientResponse clientResponse = null;

			// Posting drone info
			String postPath = "/amministratore/insert";
			DroneInfo dInfo = new DroneInfo(clientId, ipAddress, port);
			clientResponse = insertRequest(clientHTTP, serverAddress + postPath, dInfo);
			//List<DroneInfo> dronesInNetwork = clientResponse.getEntity(new GenericType<List<DroneInfo>>(){});
			InitDroneInfo initDroneInfo = clientResponse.getEntity(InitDroneInfo.class);
			System.out.println(initDroneInfo);



			// With Socket connection

			/*
			Socket clientSocket = new Socket("localhost", 6789);
			DroneInfo droneInfo = DroneInfo.newBuilder()
					.setDroneID(clientId).setIpAddress(ipAddress).setPort(port)
					.build();

			droneInfo.writeTo(clientSocket.getOutputStream());
			System.out.println("Sent drone info to server");
			clientSocket.close();
			*/

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
}
