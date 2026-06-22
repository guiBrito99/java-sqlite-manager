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
		this.tableManager.createTable(tableName, columns);
		String sql = SQLBuilder.createTableSQL(tableName, columns);
		this.dataGateway.runSQL(sql);
	}

	public void deleteTable(String tableName) {
		this.tableManager.deleteTable(tableName);
		String sql = SQLBuilder.deleteTable(tableName);
		this.dataGateway.runSQL(sql);
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
		this.tableManager.insertRow(tableName, columns, values);
		String sql = SQLBuilder.insertRow(tableName, columns, values);
		this.dataGateway.runSQL(sql);
	}

	public void updateRow(String tableName, String[] columns, String[] values, String[] tableColumns,
			String[] oldValues, int rowIndex) {
		this.tableManager.updateRow(tableName, rowIndex, columns, values);
		String sql = SQLBuilder.updateRow(tableName, columns, values, tableColumns, oldValues);
		this.dataGateway.runSQL(sql);
	}

	public void deleteRow(String tableName, int rowIndex) {
		String[] columns = this.tableManager.getTableColumns(tableName);
		String[] values = this.tableManager.getRowValues(tableName, rowIndex);
		this.tableManager.deleteRow(tableName, rowIndex);
		String sql = SQLBuilder.deleteRow(tableName, columns, values);
		this.dataGateway.runSQL(sql);
	}

}
