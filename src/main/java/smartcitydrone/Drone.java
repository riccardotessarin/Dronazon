package smartcitydrone;

import beans.DroneInfo;
import beans.GlobalStat;
import beans.InitDroneInfo;
import com.google.gson.Gson;
import com.sun.jersey.api.client.*;
import org.eclipse.paho.client.mqttv3.*;
import sensors.DroneSensorBuffer;
import sensors.PM10Simulator;
import smartcity.OrderData;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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

		// By sorting the drones we will have a base for the ring network
		droneProperty.sortDronesInNetwork();
		// We need to keep the position updated also inside the drone list (anyone could become master)
		droneProperty.updatePositionInNetwork();

		// If it's the only drone in the network it automatically becomes master
		// It also starts the master drone thread with the MQTT subscriber
		if (droneProperty.getDronesInNetwork().size() == 1) {
			droneProperty.makeMaster();
		}

		DroneServerThread serverThread = new DroneServerThread(droneProperty);
		droneProperty.setServerThread(serverThread);
		serverThread.start();

		DroneConsoleThread consoleThread = new DroneConsoleThread(droneProperty);
		consoleThread.start();

		// Sending to the drones GRPC network the join request message in broadcast
		List<DroneServiceThread> threads = new ArrayList<>();
		List<DroneInfo> dronesInNetworkCopy = droneProperty.getDronesInNetwork();

		for (DroneInfo droneInfo : dronesInNetworkCopy) {
			if (droneInfo.getDroneID() != droneProperty.getDroneID()) {
				DroneServiceThread serviceThread = new DroneServiceThread(droneProperty, droneInfo);
				serviceThread.start();
				threads.add(serviceThread);
			}
		}

		// Joining all threads
		for (DroneServiceThread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// If the new drone doesn't get any info about the master or about an ongoing election, it starts an election
		if (droneProperty.getMasterDrone() == null && !droneProperty.isElectionInProgress() && !droneProperty.isParticipant()) {
			droneProperty.startElection();
		}
		/*
		// Now useless but let's keep it
		else if (droneProperty.getMasterDrone() == null && droneProperty.isElectionInProgress()) {
			// Here we need to check for the election message past the drone edge case
			// If the drone joins the network while the elected message is still going in the ring
			// BUT the drone places itself before the drone who's now sending the elected message, it will
			// never know about the end of election and about who's master so it looks for it on its own
			DroneLookForMasterThread lookForMasterThread = new DroneLookForMasterThread(droneProperty);
			lookForMasterThread.start();
		}

		 */

		droneProperty.setSensorThread(new PM10Simulator(new DroneSensorBuffer(droneProperty)));
		droneProperty.getSensorThread().start();

		if (!droneProperty.isMaster()) {
			droneProperty.setCheckThread(new DroneCheckThread(droneProperty));
			droneProperty.getCheckThread().start();
		}

		droneProperty.setPrintThread(new DronePrintThread(droneProperty));
		droneProperty.getPrintThread().start();

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

	public static ClientResponse postRequest (Client client, String url, GlobalStat globalStat) {
		WebResource webResource = client.resource(url);
		String input = new Gson().toJson(globalStat);
		try {
			return webResource.type("application/json").post(ClientResponse.class, input);
		} catch (ClientHandlerException e) {
			System.out.println("Server unavailable");
			return null;
		}
	}

	public static ClientResponse putRequest (Client client, String url, int droneID) {
		WebResource webResource = client.resource(url);
		String input = new Gson().toJson(droneID);
		try {
			return webResource.type("application/json").put(ClientResponse.class, input);
		} catch (ClientHandlerException e) {
			System.out.println("Server unavailable");
			return null;
		}
	}

	public static ClientResponse removeRequest (Client client, String url, int droneID) {
		WebResource webResource = client.resource(url);
		String input = new Gson().toJson(droneID);
		try {
			return webResource.type("application/json").delete(ClientResponse.class, input);
		} catch (ClientHandlerException e) {
			System.out.println("Server unavailable");
			return null;
		}
	}
}
