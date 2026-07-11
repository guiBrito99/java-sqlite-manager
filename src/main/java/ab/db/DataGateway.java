package ab.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * The DataGateway class is responsible for all direct interactions with the
 * SQLite database. It encapsulates the JDBC API, ensuring that upper layers
 * (like DatabaseController) do not need to deal with ResultSets, Statements, or
 * SQL connections directly.
 */
public class DataGateway {

	// The active database connection provided by ConnectionManager
	private Connection connection;

	/**
	 * Constructs the DataGateway with a given database connection.
	 * 
	 * @param connection The active JDBC Connection to the SQLite file.
	 * @throws SQLException If the connection is invalid.
	 */
	public DataGateway(Connection connection) throws SQLException {
		this.connection = connection;
	}

	/**
	 * Reads the entire database schema and data from the SQLite file and translates
	 * it into a list of in-memory Table objects. This acts as a custom
	 * Object-Relational Mapper (ORM).
	 * 
	 * @return An ArrayList of populated Table objects representing the current
	 *         database state.
	 * @throws SQLException If database metadata or data cannot be read.
	 */
	public ArrayList<Table> load() throws SQLException {
		ArrayList<String> tableNames = new ArrayList<>();
		ArrayList<ArrayList<String>> columnsArrays = new ArrayList<>();
		ArrayList<ArrayList<String[]>> rowsArrays = new ArrayList<>();

		ArrayList<Table> tables = new ArrayList<>();

		// Retrieve database metadata (schema information)
		DatabaseMetaData metaData = this.connection.getMetaData();

		// Fetch all standard tables (ignoring system tables or views)
		ResultSet connectionTables = metaData.getTables(null, null, null, new String[] { "TABLE" });

		// Step 1: Retrieving all table names in the connection
		while (connectionTables.next()) {
			tableNames.add(connectionTables.getString("TABLE_NAME"));
		}

		// Step 2: Retrieving all columns names for each table
		for (int i = 0; i < tableNames.size(); i++) {
			ArrayList<String> fetchedColumns = new ArrayList<>();

			// Fetching columns for the current table in the loop
			ResultSet columnsFetch = metaData.getColumns(null, null, tableNames.get(i), null);

			// Adding each column in the structure
			while (columnsFetch.next()) {
				fetchedColumns.add(columnsFetch.getString("COLUMN_NAME"));
			}

			columnsArrays.add(fetchedColumns);

		}

		// Step 3: Retrieving all rows in each table
		String sql = null;
		for (int i = 0; i < tableNames.size(); i++) {
			sql = "SELECT * FROM " + tableNames.get(i);
			ResultSet rowsFetch = this.runSQL(sql);
			rowsArrays.add(new ArrayList<>());

			// Iterate through every row returned by the SELECT query
			while (rowsFetch.next()) {
				String[] values = new String[columnsArrays.get(i).size()];
				// Map the ResultSet columns to our String array
				// JDBC ResultSet indexes are 1-based, hence (j + 1)
				for (int j = 0; j < columnsArrays.get(i).size(); j++) {
					values[j] = rowsFetch.getString(j + 1);
				}
				rowsArrays.get(i).add(values);
			}
		}

		// Step 4: Adding the tables to the internal structure
		// Combine names, columns, and rows into the final Table objects
		for (int i = 0; i < tableNames.size(); i++) {
			tables.add(new Table(tableNames.get(i), columnsArrays.get(i), rowsArrays.get(i)));
		}

		return tables;
	}

	/**
	 * A dynamic SQL execution engine that automatically routes queries to the
	 * correct JDBC Statement type based on the first two characters of the SQL
	 * string.
	 * 
	 * @param sql        The raw SQL string to execute.
	 * @param parameters Optional varargs matrix of parameters for PreparedStatement
	 *                   insertion.
	 * @return A ResultSet if the query is a SELECT statement, otherwise null.
	 */
	public ResultSet runSQL(String sql, String[]... parameters) {

		ResultSet resultSet = null;
		try {

			// Extract the first two letters of the SQL command (e.g., "cr" for CREATE)
			String id = sql.substring(0, 2).toLowerCase();

			// Routing for DDL (Data Definition Language) - CREATE (cr) or DROP (dr)
			if (id.equals("cr") || id.equals("dr")) {
				Statement statement = this.connection.createStatement();
				statement.executeUpdate(sql);

				// Routing for DML (Data Manipulation Language) - INSERT (in), UPDATE (up),
				// DELETE (de)
			} else if (id.equals("in") || id.equals("up") || id.equals("de")) {

				// Use PreparedStatement to protect against SQL Injection and handle formatting
				PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

				int index = 0;
				// Flatten the 2D varargs array into linear PreparedStatement parameters
				for (int i = 0; i < parameters.length; i++) {
					for (int j = 0; j < parameters[i].length; j++) {
						index++;
						preparedStatement.setObject(index, parameters[i][j]);
					}
				}

				preparedStatement.executeUpdate();

				// Routing for DQL (Data Query Language) - SELECT (se)
			} else if (id.equals("se")) {
				Statement statement = this.connection.createStatement();
				resultSet = statement.executeQuery(sql);
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return resultSet;
	}

}