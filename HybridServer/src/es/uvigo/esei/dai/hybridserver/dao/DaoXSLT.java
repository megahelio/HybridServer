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

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(this.url, this.user, this.password);

	}

	public String addPage(String content, String xsd) {
		String uuid = UUIDgenerator.generate();
		try (PreparedStatement statement = getConnection()
				.prepareStatement("INSERT INTO XSLT (uuid, content, xsd) VALUES (?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, uuid);
			statement.setString(2, content);
			statement.setString(3, xsd);

			if (statement.executeUpdate() != 1) {
				throw new SQLException("Error al insertar");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return uuid;
	}

	public void deletePage(String id) {

		try (PreparedStatement statement = getConnection().prepareStatement("DELETE FROM xslt WHERE uuid=?")) {
			statement.setString(1, id);

			if (statement.executeUpdate() != 1)
				throw new SQLException("Error al eliminar");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public String listPages() {
		try (Statement statement = getConnection().createStatement()) {
			try (ResultSet result = statement.executeQuery("SELECT * FROM xslt")) {
				final List<String> xslt = new ArrayList<>();

				while (result.next()) {
					xslt.add(result.getString("uuid"));
				}

				StringBuilder toRet = new StringBuilder();

				for (String i : xslt) {
					toRet.append(i + "\n");
				}

				return toRet.toString();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getContent(String id) {
		// no tiene mucho sentido que te traigas toda la tabla (*) para solo necesitar
		// la columna content
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM xslt WHERE uuid=?")) {
			statement.setString(1, id);

			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return result.getString("content");
				} else
					return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getXSD(String id) {
		//no tiene mucho sentido que te traigas toda la tabla (*) para solo necesitar la columna xsd
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM xslt WHERE uuid=?")) {
			statement.setString(1, id);

			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return result.getString("xsd");
				} else
					return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean exist(String id) {
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM xslt WHERE uuid=?")) {
			statement.setString(1, id);
			try (ResultSet result = statement.executeQuery()) {
				return result.next();

			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}