package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DaoXSD implements DaoInterface {

	private String url;
	private String user;
	private String password;

	/**
	 * @param url
	 * @param user
	 * @param password
	 */
	public DaoXSD(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public String addPage(String content) {
		String uuid = UUIDgenerator.generate();
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection
					.prepareStatement("INSERT INTO XSD (uuid, content) VALUES (?, ?)",
							Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, uuid);
				statement.setString(2, content);

				if (statement.executeUpdate() != 1) {
					throw new SQLException("Error al insertar");
				}
			}
		} catch (SQLException e) {
			uuid = null;
			throw new RuntimeException(e);
		}

		return uuid;
	}

	@Override
	public void deletePage(String id) {
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM xsd WHERE uuid=?")) {
				statement.setString(1, id);

				if (statement.executeUpdate() != 1)
					throw new SQLException("Error al eliminar");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String listPages() {
		StringBuilder toRet = new StringBuilder();
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet result = statement.executeQuery("SELECT * FROM xsd")) {
					final List<String> xsd = new ArrayList<>();

					while (result.next()) {
						xsd.add(result.getString("uuid"));
					}

					for (String i : xsd) {
						toRet.append(i + "\n");
					}

				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return toRet.toString();
	}

	@Override
	public String get(String id) {
		String toret = null;
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM xsd WHERE uuid=?")) {
				statement.setString(1, id);

				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						toret = result.getString("content");
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return toret;
	}

	@Override
	public boolean exist(String id) {
		Boolean toret = false;
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM xsd WHERE uuid=?")) {
				statement.setString(1, id);
				try (ResultSet result = statement.executeQuery()) {
					toret = result.next();

				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return toret;

	}
}