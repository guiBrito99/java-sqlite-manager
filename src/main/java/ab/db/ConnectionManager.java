package ab.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	public static Connection connect() {
		Connection connection = null;
		try {
			// Default path is the project's root directory
			String url = "jdbc:sqlite:database.sqlite";
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return connection;
	}

}
