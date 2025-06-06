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

import javax.xml.ws.Endpoint;

import es.uvigo.esei.dai.hybridserver.dao.DaoHTML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSLT;

public class HybridServer {
	private static int count = 0;

	private Thread serverThread;
	private boolean stop;
	private Configuration configuration;
	private ExecutorService threadPool;

	private DaoXML daoXML;
	private DaoHTML daoHTML;
	private DaoXSD daoXSD;
	private DaoXSLT daoXSLT;

	private Endpoint endpoint;
	private HybridServerServiceImpl hybridServerServiceImpl;

	public HybridServer() {
		System.out.println("constructor vacío");
		this.configuration = new Configuration();
		System.out.println(this.configuration.toString());

		this.daoHTML = new DaoHTML(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXML = new DaoXML(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXSD = new DaoXSD(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXSLT = new DaoXSLT(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.endpoint = null;

		this.hybridServerServiceImpl = new HybridServerServiceImpl(this.daoHTML, this.daoXML, this.daoXSD,
				this.daoXSLT);

	}

	public HybridServer(Properties properties) {
		System.out.println("constructor properties");

		System.out.println(properties.toString());
		this.configuration = new Configuration(properties);
		System.out.println(this.configuration.toString());

		this.daoHTML = new DaoHTML(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXML = new DaoXML(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXSD = new DaoXSD(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXSLT = new DaoXSLT(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());
		this.endpoint = null;
		this.hybridServerServiceImpl = new HybridServerServiceImpl(this.daoHTML, this.daoXML, this.daoXSD,
				this.daoXSLT);
	}

	public HybridServer(Configuration configuration) {
		System.out.println("constructor configuration");
		this.configuration = configuration;
		System.out.println(this.configuration.toString());

		this.daoHTML = new DaoHTML(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXML = new DaoXML(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXSD = new DaoXSD(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());

		this.daoXSLT = new DaoXSLT(this.configuration.getDbURL(), this.configuration.getDbUser(),
				this.configuration.getDbPassword());
		this.endpoint = null;
		this.hybridServerServiceImpl = new HybridServerServiceImpl(this.daoHTML, this.daoXML, this.daoXSD,
				this.daoXSLT);

	}

	public int getPort() {
		System.out.println(this.configuration.getHttpPort());
		return this.configuration.getHttpPort();
	}

	public void start() {
		System.out.println("HybridServer.Start: ");
		this.serverThread = new Thread() {
			@Override
			public void run() {

				try (final ServerSocket serverSocket = new ServerSocket(configuration.getHttpPort())) {
					if (configuration.getWebServiceURL() != null && hybridServerServiceImpl != null) {
						endpoint = Endpoint.publish(configuration.getWebServiceURL(), hybridServerServiceImpl);
					}
					// try (final ServerSocket serverSocket = new
					// ServerSocket(Integer.parseInt(prop.getProperty("port")))) {
					threadPool = Executors.newFixedThreadPool(configuration.getNumClients());
					// threadPool =
					// Executors.newFixedThreadPool(Integer.parseInt(prop.getProperty("numClients")));
					while (true) {

						System.out.println(
								"HybridServer.WaitingConnection " + (++count) + ": " + serverSocket.toString());

						Socket socket = serverSocket.accept();

						System.out.println("HybridServer.SocketAccept: " + socket.toString());

						if (stop)
							break;
						ServiceThread thread = new ServiceThread(socket, daoHTML, daoXML, daoXSD, daoXSLT,
								configuration);

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
		System.out.println("HybridServer.Stop");
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
		if (endpoint != null) {
			endpoint.stop();
		}
		hybridServerServiceImpl.close();

	}
}
