package ab.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseController {
	Connection connection;
	DataGateway dataGateway;
	TableManager tableManager;

	public DatabaseController() throws SQLException {
		this.connection = ConnectionManager.connect();
		this.dataGateway = new DataGateway(connection);
		this.tableManager = new TableManager(dataGateway.load());
	}

	public void print() {
		this.tableManager.printTables();
	}

	public void createTable(String tableName, String[] columns) {
		String sql = SQLBuilder.createTableSQL(tableName, columns);
		this.dataGateway.runSQL(sql);
		this.tableManager.createTable(tableName, columns);
	}

	public void deleteTable(String tableName) {
		String sql = SQLBuilder.deleteTable(tableName);
		this.dataGateway.runSQL(sql);
		this.tableManager.deleteTable(tableName);
	}

	public String[] getTablesNames() {
		return this.tableManager.getTablesNames();
	}

	public String[] getTableColumns(String tableName) {
		return this.tableManager.getTableColumns(tableName);
	}

	public String[][] getValuesMatrix(String tableName) {
		return this.tableManager.getValuesMatrix(tableName);
	}

	public String[] getRowValues(String tableName, int rowIndex) {
		return this.tableManager.getRowValues(tableName, rowIndex);
	}

	public void insertRow(String tableName, String[] columns, String[] values) {
		String sql = SQLBuilder.insertRow(tableName, columns);
		this.dataGateway.runSQL(sql, values);
		this.tableManager.insertRow(tableName, columns, values);
	}

	public void updateRow(String tableName, String[] columns, String[] values, String[] tableColumns,
			String[] oldValues, int rowIndex) {
		String sql = SQLBuilder.updateRow(tableName, columns, tableColumns);
		this.dataGateway.runSQL(sql, values, oldValues);
		this.tableManager.updateRow(tableName, rowIndex, columns, values);
	}

	public void deleteRow(String tableName, int rowIndex) {
		String[] columns = this.tableManager.getTableColumns(tableName);
		String[] values = this.tableManager.getRowValues(tableName, rowIndex);
		String sql = SQLBuilder.deleteRow(tableName, columns);
		this.dataGateway.runSQL(sql, values);
		this.tableManager.deleteRow(tableName, rowIndex);
	}

}
