import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class StartServer {

	private static final String HOST = "localhost";
	private static final int PORT = 1337;


	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServerFactory.create("http://"+HOST+":"+PORT+"/");
		server.start();

		System.out.println("Server running!");
		System.out.println("Server started on: http://"+HOST+":"+PORT);
		/*
		ServerSocket serverSocket = new ServerSocket(6789);

		while(true) {
			Socket connectionSocket = serverSocket.accept();
			Useless.ServerThread thread = new Useless.ServerThread(connectionSocket);
			thread.start();
			System.out.println("Thread started");
		}
		*/
		System.out.println("Hit return to stop...");
		System.in.read();
		System.out.println("Stopping server");
		server.stop(0);
		System.out.println("Server stopped");

	}
}