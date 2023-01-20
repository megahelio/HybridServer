package es.uvigo.esei.dai.hybridserver;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import es.uvigo.esei.dai.hybridserver.core.HybridServer;

public class Launcher {

	public static void main(String[] args) {
		if (args.length == 0) {

			HybridServer server = new HybridServer();
			server.start();

		} else if (args.length == 1) {

			Properties properties = new Properties();

			try (Reader inputStream = new FileReader(args[0])) {

				properties.load(inputStream);

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (properties.getProperty("db.url") == null || properties.getProperty("db.user") == null || properties
					.getProperty("db.password") == null) {

				System.err.println("El fichero de configuración no incluye información de conexión.");
			} else {
				HybridServer server = new HybridServer(properties);
				server.start();
			}

		} else {

			System.err.println("No se aceptan más de 1 argumento.");

		}

		// Para parar el servidor
		// System.out.println("Press Q to exit");
		// try (Scanner inputScanner = new Scanner(System.in)) {
		// while (inputScanner.nextLine().toUpperCase().charAt(0) != 'Q');
		// }

		// server.stop();

	}
}
