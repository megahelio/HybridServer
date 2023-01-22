package es.uvigo.esei.dai.hybridserver.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private final String url;
    private final String user;
    private final String password;
    private final int poolSize;
    private final List<Connection> availableConnections;
    private final List<Connection> usedConnections;

    public ConnectionPool(String url, String user, String password, int poolSize) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.poolSize = poolSize;
        this.availableConnections = new ArrayList<>(poolSize);
        this.usedConnections = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                this.availableConnections.add(connection);
            } catch (SQLException e) {
                throw new RuntimeException("Error al crear el pool de conexiones", e);
            }
        }
    }

    public synchronized Connection getConnection() {
        if (this.availableConnections.isEmpty()) {
            throw new RuntimeException("No hay conexiones disponibles en el momento");
        }
        Connection connection = this.availableConnections.remove(0);
        this.usedConnections.add(connection);
        return connection;
    }

    public synchronized void returnConnection(Connection connection) {
        if (!this.usedConnections.remove(connection)) {
            throw new IllegalArgumentException("La conexión no pertenece a este pool");
        }
        this.availableConnections.add(connection);
    }

    public void stop() {
        for (Connection connection : this.availableConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (Connection connection : this.usedConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
