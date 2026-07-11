package ab.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The DatabaseController acts as the central orchestrator (Facade pattern) for
 * the application. It bridges the View (user interface) with the underlying
 * database logic and memory state. For every modifying operation, it ensures
 * that both the persistent database (via DataGateway) and the in-memory
 * representation (via TableManager) are kept perfectly in sync.
 */
public class DatabaseController {
	// Holds the active connection to the SQLite database
	Connection connection;
	// Handles all direct SQL execution and database communication
	DataGateway dataGateway;
	// Manages the in-memory representation of tables and data for fast retrieval
	TableManager tableManager;

	/**
	 * Initializes the DatabaseController by establishing a connection, setting up
	 * the DataGateway, and pre-loading existing database data into memory.
	 * 
	 * @throws SQLException If the connection fails or data cannot be loaded.
	 */
	public DatabaseController() throws SQLException {
		this.connection = ConnectionManager.connect();
		this.dataGateway = new DataGateway(connection);
		this.tableManager = new TableManager(dataGateway.load());
	}

	/**
	 * Safely closes the active database connection to prevent memory leaks and file
	 * locks.
	 */
	public void close() {
		try {
			if (this.connection != null && !this.connection.isClosed()) {
				this.connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Error closing database: " + e.getMessage());
		}
	}

	/**
	 * Prints the current state of all tables currently stored in the in-memory
	 * TableManager.
	 */
	public void print() {
		this.tableManager.printTables();
	}

	/**
	 * Creates a new table both in the persistent database and the in-memory
	 * manager.
	 * 
	 * @param tableName The name of the table to create.
	 * @param columns   An array of column names for the new table.
	 */
	public void createTable(String tableName, String[] columns) {
		// 1. Generate the raw SQL command
		String sql = SQLBuilder.createTableSQL(tableName, columns);
		// 2. Execute against the SQLite file
		this.dataGateway.runSQL(sql);
		// 3. Update the in-memory representation
		this.tableManager.createTable(tableName, columns);
	}

	/**
	 * Deletes a table from both the persistent database and the in-memory manager.
	 * 
	 * @param tableName The name of the table to drop.
	 */
	public void deleteTable(String tableName) {
		String sql = SQLBuilder.deleteTable(tableName);
		this.dataGateway.runSQL(sql);
		this.tableManager.deleteTable(tableName);
	}

	/**
	 * Retrieves an array of all current table names.
	 * 
	 * @return Array of table name strings.
	 */
	public String[] getTablesNames() {
		return this.tableManager.getTablesNames();
	}

	/**
	 * Retrieves the column names for a specific table.
	 * 
	 * @param tableName The name of the target table.
	 * @return Array of column name strings.
	 */
	public String[] getTableColumns(String tableName) {
		return this.tableManager.getTableColumns(tableName);
	}

	/**
	 * Retrieves the entire data grid for a specific table as a 2D array.
	 * 
	 * @param tableName The name of the target table.
	 * @return A 2D array representing the table's rows and columns.
	 */
	public String[][] getValuesMatrix(String tableName) {
		return this.tableManager.getValuesMatrix(tableName);
	}

	/**
	 * Retrieves the values of a specific row within a table.
	 * 
	 * @param tableName The name of the target table.
	 * @param rowIndex  The zero-based index of the row.
	 * @return Array of string values for the specified row.
	 */
	public String[] getRowValues(String tableName, int rowIndex) {
		return this.tableManager.getRowValues(tableName, rowIndex);
	}

	/**
	 * Inserts a new row of data into the database and the in-memory manager.
	 * 
	 * @param tableName The name of the target table.
	 * @param columns   The specific columns being populated.
	 * @param values    The values corresponding to the selected columns.
	 */
	public void insertRow(String tableName, String[] columns, String[] values) {
		String sql = SQLBuilder.insertRow(tableName, columns);
		this.dataGateway.runSQL(sql, values);
		this.tableManager.insertRow(tableName, columns, values);
	}

	/**
	 * Updates an existing row in the database and in-memory manager.
	 * 
	 * @param tableName    The name of the target table.
	 * @param columns      The specific columns being updated.
	 * @param values       The new values for the updated columns.
	 * @param tableColumns All columns that currently exist in the table.
	 * @param oldValues    The previous values of the row (used for SQL WHERE clause
	 *                     matching).
	 * @param rowIndex     The zero-based index of the row to update in memory.
	 */
	public void updateRow(String tableName, String[] columns, String[] values, String[] tableColumns,
			String[] oldValues, int rowIndex) {
		String sql = SQLBuilder.updateRow(tableName, columns, tableColumns);
		this.dataGateway.runSQL(sql, values, oldValues);
		this.tableManager.updateRow(tableName, rowIndex, columns, values);
	}

	/**
	 * Deletes a specific row from the database and the in-memory manager.
	 * 
	 * @param tableName The name of the target table.
	 * @param rowIndex  The zero-based index of the row to delete.
	 */
	public void deleteRow(String tableName, int rowIndex) {
		// Fetch metadata needed to construct the SQL WHERE clause
		String[] columns = this.tableManager.getTableColumns(tableName);
		String[] values = this.tableManager.getRowValues(tableName, rowIndex);

		String sql = SQLBuilder.deleteRow(tableName, columns);
		this.dataGateway.runSQL(sql, values);
		this.tableManager.deleteRow(tableName, rowIndex);
	}

}