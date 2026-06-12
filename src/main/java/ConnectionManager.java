package ab.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	public static Connection connect() {
		Connection connection = null;
		try {
			// Default path is the project's root directory
			String url = "jdbc:sqlite:src.database";
			connection = DriverManager.getConnection(url);
			System.out.println("SQLite connection made.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return connection;
	}

}
