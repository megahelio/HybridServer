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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoMapper;

public class HybridServer {
	private static final int SERVICE_PORT = 80;

	private static int count=0;

	private Thread serverThread;
	private boolean stop;
	private DaoInterface dao;
	private Properties prop;
	private ExecutorService threadPool;

	public HybridServer() {
		System.out.println("Creando HybridServer");
		this.prop = new Properties();
		this.prop.put("numClients", 50);
		this.prop.put("port", SERVICE_PORT);
		this.prop.put("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		this.prop.put("db.user", "hsdb");
		this.prop.put("db.password", "hsdbpass");

		this.dao = new DaoMapper();
	}

	public HybridServer(Map<String, String> pages) {
		this.prop = new Properties();
		this.prop.put("numClients", 50);
		this.prop.put("port", SERVICE_PORT);
		this.prop.put("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		this.prop.put("db.user", "hsdb");
		this.prop.put("db.password", "hsdbpass");

		this.dao = new DaoMapper(pages);
	}

	public HybridServer(Properties properties) {
		this.prop = properties;

		this.dao = new DaoMapper();
	}

	public int getPort() {
		return Integer.parseInt(prop.getProperty("port"));
	}

	public void start() {
		System.out.println("HybridServer.Start");
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					// try (final ServerSocket serverSocket = new
					// ServerSocket(Integer.parseInt(prop.getProperty("port")))) {
					threadPool = Executors.newFixedThreadPool(50);
					// threadPool =
					// Executors.newFixedThreadPool(Integer.parseInt(prop.getProperty("numClients")));
					while (true) {
						System.out.println("HybridServer.WaitingConnection "+(++count)+": "+serverSocket.toString());
						Socket socket = serverSocket.accept();
							System.out.println("HybridServer.SocketAccept: "+ socket.toString());
							if (stop)
								break;
							ServiceThread thread = new ServiceThread(socket, dao);
							System.out.println("HybridServer.SocketAccept.Execute");
							//threadPool.execute(thread);
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

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
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
