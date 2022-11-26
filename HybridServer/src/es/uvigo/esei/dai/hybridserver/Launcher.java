package es.uvigo.esei.dai.hybridserver;

import java.util.Properties;


public class Launcher {

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put("numClients", "50");
		properties.put("port", "8888");
		properties.put("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		properties.put("db.user", "hsdb");
		properties.put("db.password", "hsdbpass");

		HybridServer server = new HybridServer(properties);
		server.start();

		// Para parar el servidor
		// System.out.println("Press Q to exit");
		// try (Scanner inputScanner = new Scanner(System.in)) {
		// while (inputScanner.nextLine().toUpperCase().charAt(0) != 'Q');
		// }

		// server.stop();

	}
}
