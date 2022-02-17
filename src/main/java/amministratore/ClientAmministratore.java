package amministratore;

import beans.DroneInfo;
import beans.DroneInfos;
import beans.GlobalStat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class ClientAmministratore {
	public static void main(String[] args) throws IOException {
		String serverAddress = "http://localhost:1337";
		Client client = Client.create();
		ClientResponse clientResponse = null;
		boolean quit = false;
		String getPath = "";

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (!quit) {
			System.out.println("\nAvailable commands:\ndrones (shows the list of active drones)\nstatistics [n]" +
					" (shows the last n global statistics)\ndeliveries [t1] [t2] (gets the average deliveries between t1 and t2)" +
					"\ntraveled [t1] [t2] (gets the average km traveled between t1 and t2)\nquit (to close the admin interface)");

			String consoleInput = br.readLine().trim();
			consoleInput = consoleInput.toLowerCase(Locale.ENGLISH);
			String[] token = consoleInput.split(" ");

			switch (token[0]) {
				case "drones" :
					System.out.println("Requesting the drones network...\n");
					getPath = "/amministratore";
					clientResponse = getRequest(client, serverAddress + getPath);
					assert clientResponse != null;
					List<DroneInfo> dronesInfoCopy = clientResponse.getEntity(DroneInfos.class).getDronesInfo();
					System.out.println("Drones currently available in network:");
					for (DroneInfo droneInfo : dronesInfoCopy) {
						System.out.println(droneInfo.toString());
					}
					break;
				case "statistics" :
					try {
						if (token.length != 2) {
							System.out.println("Type the command again");
							break;
						}
						int n = Integer.parseInt(token[1]);
						if (n < 1) {
							System.out.println("Negative numbers and 0 are not allowed here");
							break;
						}
						System.out.println("Requesting last " + n + " smart-city global statistics...\n");
						getPath = "/amministratore/stats/" + n;
						clientResponse = getRequest(client, serverAddress + getPath);
						assert clientResponse != null;
						if (clientResponse.getStatus() != Response.Status.OK.getStatusCode()) {
							break;
						}
						String lastStats  = clientResponse.getEntity(String.class);
						List<GlobalStat> lastNGlobalStats = new Gson().fromJson(lastStats, new TypeToken<List<GlobalStat>>() {}.getType());
						System.out.println("Last " + n + " global statistics:");
						for (GlobalStat globalStat : lastNGlobalStats) {
							System.out.println(globalStat.toString());
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				case "deliveries" :
					try {
						if (token.length != 3) {
							System.out.println("Type the command again");
							break;
						}
						long t1 = Long.parseLong(token[1]);
						long t2 = Long.parseLong(token[2]);
						if (t1 < 0 || t2 < 0) {
							System.out.println("Negative numbers are not allowed");
							break;
						} else if (t2 < t1) {
							System.out.println("Start time is bigger than end time");
							break;
						}
						System.out.println("Requesting average deliveries number between " + t1 + " and " + t2);
						getPath = "/amministratore/deliveries/" + t1 + "/" + t2;
						clientResponse = getRequest(client, serverAddress + getPath);
						assert clientResponse != null;
						if (clientResponse.getStatus() != Response.Status.OK.getStatusCode()) {
							break;
						}
						String averageDeliveries = clientResponse.getEntity(String.class);
						System.out.println("Smart city had an average number of deliveries of " + averageDeliveries
								+ " between " + t1 + " and " + t2);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				case "traveled" :
					try {
						if (token.length != 3) {
							System.out.println("Type the command again");
							break;
						}
						long t1 = Long.parseLong(token[1]);
						long t2 = Long.parseLong(token[2]);
						if (t1 < 0 || t2 < 0) {
							System.out.println("Negative numbers are not allowed");
							break;
						} else if (t2 < t1) {
							System.out.println("Start time is bigger than end time");
							break;
						}
						System.out.println("Requesting average kilometers traveled between " + t1 + " and " + t2);
						getPath = "/amministratore/traveled/" + t1 + "/" + t2;
						clientResponse = getRequest(client, serverAddress + getPath);
						assert clientResponse != null;
						if (clientResponse.getStatus() != Response.Status.OK.getStatusCode()) {
							break;
						}
						String averageTraveledKM = clientResponse.getEntity(String.class);
						System.out.println("Drones in smart city traveled for an average of " + averageTraveledKM
								+ " km between " + t1 + " and " + t2);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				case "quit" :
					quit = true;
					System.out.println("Closing admin interface...");
					break;
				default :
					System.out.println("Unknown command, please try again");
					break;
			}
		}
	}

	public static ClientResponse getRequest(Client client, String url){
		WebResource webResource = client.resource(url);
		try {
			return webResource.type("application/json").get(ClientResponse.class);
		} catch (ClientHandlerException e) {
			System.out.println("Server unavailable");
			return null;
		}
	}
}
