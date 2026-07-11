package ab.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The ConnectionManager is a utility class responsible for establishing the
 * physical connection between the Java application and the SQLite database
 * file. It abstracts away the JDBC driver initialization and connection
 * strings.
 */
public class ConnectionManager {

	/**
	 * Establishes and returns a connection to the SQLite database.
	 * 
	 * @return A valid java.sql.Connection object, or null if the connection fails.
	 */
	public static Connection connect() {
		Connection connection = null;
		try {
			// Default path is the project's root directory.
			// SQLite creates the file automatically if it does not already exist.
			String url = "jdbc:sqlite:database.sqlite";

			// DriverManager automatically locates the sqlite-jdbc driver in the
			// dependencies
			// and establishes the connection to the specified file.
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			// Catch and display any connection errors (e.g., file permission issues)
			System.out.println(e.getMessage());
		}
		return connection;
	}

}