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

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(this.url, this.user, this.password);

	}

	@Override
	public String addPage(String content) {
		String uuid = UUIDgenerator.generate();
		try (PreparedStatement statement = getConnection()
				.prepareStatement("INSERT INTO XSD (uuid, content) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, uuid);
			statement.setString(2, content);

			if (statement.executeUpdate() != 1)
				throw new SQLException("Error al insertar");

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return uuid;
	}

	@Override
	public void deletePage(String id) {

		try (PreparedStatement statement = getConnection().prepareStatement("DELETE FROM xsd WHERE uuid=?")) {
			statement.setString(1, id);

			if (statement.executeUpdate() != 1)
				throw new SQLException("Error al eliminar");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String listPages() {
		try (Statement statement = getConnection().createStatement()) {
			try (ResultSet result = statement.executeQuery("SELECT * FROM xsd")) {
				final List<String> xsd = new ArrayList<>();

				while (result.next()) {
					xsd.add(result.getString("uuid"));
				}

				StringBuilder toRet = new StringBuilder();

				for (String i : xsd) {
					toRet.append(i + "\n");
				}

				return toRet.toString();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String get(String id) {
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM xsd WHERE uuid=?")) {
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

	@Override
	public boolean exist(String id) {
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM xsd WHERE uuid=?")) {
			statement.setString(1, id);
			try (ResultSet result = statement.executeQuery()) {
				return result.next();

			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}