package es.uvigo.esei.dai.hybridserver;

import java.util.LinkedHashMap;
import java.util.Map;

//import java.util.Scanner;

public class Launcher {

	public static void main(String[] args) {
		Map<String, String> pages = new LinkedHashMap<>();
		pages.put("1", "Página número 1");
		pages.put("2", "Página número 2");
		pages.put("3", "Página número 3");
		HybridServer server = new HybridServer(pages);
		server.start();

		// Para parar el servidor
		// System.out.println("Press Q to exit");
		// try (Scanner inputScanner = new Scanner(System.in)) {
		// while (inputScanner.nextLine().toUpperCase().charAt(0) != 'Q');
		// }

		// server.stop();

	}
}
