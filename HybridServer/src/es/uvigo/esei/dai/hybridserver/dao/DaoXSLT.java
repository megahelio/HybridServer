package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DaoXSLT {

	private String url;
	private String user;
	private String password;

	/**
	 * @param url
	 * @param user
	 * @param password
	 */
	public DaoXSLT(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}


	public String addPage(String content, String xsd) {
		String uuid = UUIDgenerator.generate();
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection
					.prepareStatement("INSERT INTO XSLT (uuid, content, xsd) VALUES (?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, uuid);
				statement.setString(2, content);
				statement.setString(3, xsd);

				if (statement.executeUpdate() != 1) {
					throw new SQLException("Error al insertar");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return uuid;
	}

	public void deletePage(String id) {

		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM XSLT WHERE uuid=?")) {
				statement.setString(1, id);

				if (statement.executeUpdate() != 1)
					throw new SQLException("Error al eliminar");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public List<String> listPages() {
		final List<String> xslt = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet result = statement.executeQuery("SELECT * FROM XSLT")) {

					while (result.next()) {
						xslt.add(result.getString("uuid"));
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return xslt;
	}

	public String getContent(String id) {
		// no tiene mucho sentido que te traigas toda la tabla (*) para solo necesitar
		// la columna content
		String toret = null;

		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XSLT WHERE uuid=?")) {
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

	public String getXSD(String id) {
		// no tiene mucho sentido que te traigas toda la tabla (*) para solo necesitar
		// la columna xsd
		String toret = null;
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XSLT WHERE uuid=?")) {
				statement.setString(1, id);

				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						toret = result.getString("xsd");
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return toret;
	}

	public boolean exist(String id) {
		boolean toret = false;
		try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XSLT WHERE uuid=?")) {
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