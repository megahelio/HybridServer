package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class Launcher {

	public static void main(String[] args) {
		if (args.length == 0) {

			HybridServer server = new HybridServer();
			server.start();

			// cargar fichero .xml
		} else if (args.length == 1 && args[0].substring(args[0].length() - 3).equals("xml")) {
			Configuration configuration;
			try {
				configuration = XMLConfigurationLoader.load(new File(args[0]));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			HybridServer server = new HybridServer(configuration);
			server.start();

			// Cargar fichero .prop
		} else if (args.length == 1 && args[0].substring(args[0].length() - 4).equals("prop")) {

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

			System.err.println("No se aceptan más de 1 argumento .xml o .prop");

		}

	}
}
