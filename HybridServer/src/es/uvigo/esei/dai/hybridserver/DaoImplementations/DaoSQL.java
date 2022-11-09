package es.uvigo.esei.dai.hybridserver.DaoImplementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.DaoInterface;
import es.uvigo.esei.dai.hybridserver.UUIDgenerator;

public class DaoSQL implements DaoInterface {

	private final Connection connection;

	public DaoSQL(Connection connection) {
		this.connection = connection;
	}

	@Override
	public String addPage(String content) {
		try (PreparedStatement statement = this.connection
				.prepareStatement("INSERT INTO HTML (uuid, content) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, UUIDgenerator.generate());
			statement.setString(2, content);

			if (statement.executeUpdate() != 1)
				throw new SQLException("Error al insertar");

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return "Insertado correctamente";
	}

	@Override
	public void deletePage(String id) {

		try (PreparedStatement statement = this.connection.prepareStatement("DELETE FROM html WHERE uuid=?")) {
			statement.setString(1, id);

			if (statement.executeUpdate() != 1)
				throw new SQLException("Error al eliminar");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String listPages() {
		try (Statement statement = this.connection.createStatement()) {
			try (ResultSet result = statement.executeQuery("SELECT * FROM html")) {
				final List<String> html = new ArrayList<>();

				while (result.next()) {
					html.add(result.getString("content"));
				}

				StringBuilder toRet = new StringBuilder();

				for (String i : html) {
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
		try (PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM html WHERE uuid=?")) {
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
		try (PreparedStatement statement = this.connection.prepareStatement("SELECT FROM html WHERE uuid=?")) {
			statement.setString(1, id);

			if (statement.executeUpdate() != 1)
				return false;
			else
				return true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}