package ab.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataGateway {

	private Connection connection;

	public DataGateway(Connection connection) throws SQLException {
		this.connection = connection;
	}

	public ArrayList<Table> load() throws SQLException {
		ArrayList<String> tableNames = new ArrayList<>();
		ArrayList<ArrayList<String>> columnsArrays = new ArrayList<>();
		ArrayList<ArrayList<String[]>> rowsArrays = new ArrayList<>();

		ArrayList<Table> tables = new ArrayList<>();

		// Retrieve database metadata
		DatabaseMetaData metaData = this.connection.getMetaData();

		ResultSet connectionTables = metaData.getTables(null, null, null, new String[] { "TABLE" });

		// Retrieving all table names in the connection
		while (connectionTables.next()) {
			tableNames.add(connectionTables.getString("TABLE_NAME"));
		}

		// Retrieving all columns names for each table
		for (int i = 0; i < tableNames.size(); i++) {
			ArrayList<String> fetchedColumns = new ArrayList<>();

			// Fetching columns
			ResultSet columnsFetch = metaData.getColumns(null, null, tableNames.get(i), null);

			// Adding each column in the structure
			while (columnsFetch.next()) {
				fetchedColumns.add(columnsFetch.getString("COLUMN_NAME"));
			}

			columnsArrays.add(fetchedColumns);

		}

		// Retrieving all rows in each table
		String sql = null;
		for (int i = 0; i < tableNames.size(); i++) {
			sql = "SELECT * FROM " + tableNames.get(i);
			ResultSet rowsFetch = this.runSQL(sql);
			rowsArrays.add(new ArrayList<>());
			while (rowsFetch.next()) {
				String[] values = new String[columnsArrays.get(i).size()];
				for (int j = 0; j < columnsArrays.get(i).size(); j++) {
					values[j] = rowsFetch.getString(j + 1);
				}
				rowsArrays.get(i).add(values);
			}
		}

		// Adding the tables to the internal structure
		for (int i = 0; i < tableNames.size(); i++) {
			tables.add(new Table(tableNames.get(i), columnsArrays.get(i), rowsArrays.get(i)));
		}

		return tables;
	}

	public ResultSet runSQL(String sql, String[]... parameters) {

		ResultSet resultSet = null;
		try {

			String id = sql.substring(0, 2).toLowerCase();

			if (id.equals("cr") || id.equals("dr")) {
				Statement statement = this.connection.createStatement();
				statement.executeUpdate(sql);
			} else if (id.equals("in") || id.equals("up") || id.equals("de")) {

				PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

				int index = 0;
				for (int i = 0; i < parameters.length; i++) {
					for (int j = 0; j < parameters[i].length; j++) {
						index++;
						preparedStatement.setObject(index, parameters[i][j]);
					}
				}

				preparedStatement.executeUpdate();
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
