package es.uvigo.esei.dai.hybridserver;

//import java.util.Scanner;

public class Launcher {

	public static void main(String[] args) {

		HybridServer server = new HybridServer();
		server.start();

		//Para parar el servidor
		// System.out.println("Press Q to exit");
		// try (Scanner inputScanner = new Scanner(System.in)) {
		// 	while (inputScanner.nextLine().toUpperCase().charAt(0) != 'Q');
		// }

		// server.stop();

	}
}
