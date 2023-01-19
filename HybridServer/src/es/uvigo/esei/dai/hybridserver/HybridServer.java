/**
 *  HybridServer
 *  Copyright (C) 2022 Miguel Reboiro-Jato
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoHTML;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXML;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXSD;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXSLT;

public class HybridServer {
	private static int count = 0;

	private Thread serverThread;
	private boolean stop;
	private Properties prop;
	private ExecutorService threadPool;
	private DaoXML daoXML;
	private DaoHTML daoHTML;
	private DaoXSD daoXSD;
	private DaoXSLT daoXSLT;

	public HybridServer() {
		System.out.println("constructor vacío");
		Configuration configuration = new Configuration();
		this.prop = new Properties();
		this.prop.put("port", Integer.toString(configuration.getHttpPort()));
		this.prop.put("numClients", Integer.toString(configuration.getNumClients()));
		// this.prop.put("webServiceURL", configuration.getWebServiceURL());
		this.prop.put("db.user", configuration.getDbUser());
		this.prop.put("db.password", configuration.getDbPassword());
		this.prop.put("db.url", configuration.getDbURL());
		System.out.println("Propertis: " + this.prop.toString());

		this.daoHTML = new DaoHTML(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXML = new DaoXML(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXSD = new DaoXSD(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXSLT = new DaoXSLT(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
	}

	public HybridServer(Properties properties) {
		System.out.println("constructor properties");
		Configuration configuration = new Configuration();
		this.prop = properties;
		if (!this.prop.containsKey("port")) {
			this.prop.put("port", configuration.getHttpPort());
		}
		if (!this.prop.containsKey("numClients")) {
			this.prop.put("numClients", configuration.getNumClients());
		}
		System.out.println("Propertis: " + this.prop.toString());

		// this.dao = new DaoMapper();
		this.daoHTML = new DaoHTML(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXML = new DaoXML(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXSD = new DaoXSD(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXSLT = new DaoXSLT(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
	}

	public HybridServer(Configuration configuration) {
		System.out.println("constructor configuration");
		this.prop = new Properties();
		this.prop.put("port", Integer.toString(configuration.getHttpPort()));
		this.prop.put("numClients", Integer.toString(configuration.getNumClients()));
		// this.prop.put("webServiceURL", configuration.getWebServiceURL());
		System.out.println("flag");
		this.prop.put("db.user", configuration.getDbUser());
		this.prop.put("db.password", configuration.getDbPassword());
		this.prop.put("db.url", configuration.getDbURL());
		System.out.println("Propertis: " + this.prop.toString());
		this.daoHTML = new DaoHTML(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXML = new DaoXML(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXSD = new DaoXSD(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));
		this.daoXSLT = new DaoXSLT(this.prop.getProperty("db.url"), this.prop.getProperty("db.user"),
				this.prop.getProperty("db.password"));

	}

	public int getPort() {
		System.out.println(this.prop.getProperty("port"));
		return Integer.parseInt(this.prop.getProperty("port"));
	}

	public void start() {
		System.out.println("HybridServer.Start");
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(getPort())) {
					// try (final ServerSocket serverSocket = new
					// ServerSocket(Integer.parseInt(prop.getProperty("port")))) {
					threadPool = Executors.newFixedThreadPool(Integer.parseInt(prop.getProperty("numClients")));
					// threadPool =
					// Executors.newFixedThreadPool(Integer.parseInt(prop.getProperty("numClients")));
					while (true) {

						System.out.println(
								"HybridServer.WaitingConnection " + (++count) + ": " + serverSocket.toString());

						Socket socket = serverSocket.accept();

						System.out.println("HybridServer.SocketAccept: " + socket.toString());

						if (stop)
							break;
						ServiceThread thread = new ServiceThread(socket, daoHTML, daoXML, daoXSD, daoXSLT);
						// ServiceThreadTester thread = new ServiceThreadTester(socket, dao);

						System.out.println("HybridServer.SocketAccept.Execute");

						// threadPool.execute(thread);
						threadPool.submit(thread);

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		System.out.println("HybridServer Stop");
		this.stop = true;

		try (Socket socket = new Socket("localhost", getPort())) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;

		threadPool.shutdownNow();

		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
