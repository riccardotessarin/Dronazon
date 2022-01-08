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
		// It also starts the master drone thread with the MQTT subscriber
		if (droneProperty.getDronesInNetwork().size() == 1) {
			droneProperty.makeMaster();
		}

		DroneServerThread serverThread = new DroneServerThread(droneProperty);
		serverThread.start();

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