package Useless;

import java.io.*;
import java.net.*;
import com.smartcity.droneinfo.DroneInfoOuterClass.DroneInfo;

public class ServerThread extends Thread {
	private Socket connectionSocket = null;

	public ServerThread(Socket s) {
		connectionSocket = s;
	}

	public void run() {
		try {
			DroneInfo droneInfo = DroneInfo.parseFrom(connectionSocket.getInputStream());
			System.out.println(droneInfo.toString());
			connectionSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
