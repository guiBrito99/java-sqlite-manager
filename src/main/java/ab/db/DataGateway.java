package ab.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataGateway {

	private ArrayList<Table> tables = new ArrayList<>();
	private Connection connection;

	public DataGateway(Connection connection) throws SQLException {
		this.connection = connection;
		load();
	}

	private void load() throws SQLException {
		ArrayList<String> tableNames = new ArrayList<>();
		ArrayList<String[]> columnsArrays = new ArrayList<>();
		ArrayList<ArrayList<String[]>> rowsArrays = new ArrayList<>();

		// Clearing all present data
		this.tables.clear();

		// Retrieve database metadata
		DatabaseMetaData metaData = this.connection.getMetaData();

		ResultSet connectionTables = metaData.getTables(null, null, null, new String[] { "TABLE" });

		// Retrieving all table names in the connection
		while (connectionTables.next()) {
			tableNames.add(connectionTables.getString("TABLE_NAME"));
		}

		// Retrieving all columns names for each table
		ArrayList<String> fetchedColumns = new ArrayList<>();
		for (int i = 0; i < tableNames.size(); i++) {
			// Fetching columns
			ResultSet columnsFetch = metaData.getColumns(null, null, tableNames.get(i), null);

			// Adding each column in the structure
			while (columnsFetch.next()) {
				fetchedColumns.add(columnsFetch.getString("COLUMN_NAME"));
			}

			columnsArrays.add(new String[fetchedColumns.size()]);

			// Adding each column to the final data structure
			for (int j = 0; j < fetchedColumns.size(); j++)
				columnsArrays.get(i)[j] = fetchedColumns.get(j);

		}

		// Retrieving all rows in each table
		String sql = null;
		for (int i = 0; i < tableNames.size(); i++) {
			sql = "SELECT * FROM " + tableNames.get(i);
			ResultSet rowsFetch = this.runSQL(sql);
			rowsArrays.add(new ArrayList<>());
			String[] values = new String[columnsArrays.get(i).length];
			while (rowsFetch.next()) {
				for (int j = 0; j < columnsArrays.get(i).length; j++) {
					values[j] = rowsFetch.getString(j + 1);
				}
				rowsArrays.get(i).add(values);
			}
		}

		// Adding the tables to the internal structure
		for (int i = 0; i < tableNames.size(); i++) {
			this.tables.add(new Table(tableNames.get(i), columnsArrays.get(i), rowsArrays.get(i)));
		}

	}

	public ResultSet runSQL(String sql) {
		ResultSet resultSet = null;
		try {
			Statement statement = this.connection.createStatement();
			statement.execute(sql);
			resultSet = statement.getResultSet();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return resultSet;
	}
}
