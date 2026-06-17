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

	public void createTable() {
		Object[] result = this.tableManager.createTable();
		String tableName = (String) result[0];
		String[] columns = (String[]) result[1];

		String sql = SQLBuilder.createTableSQL(tableName, columns);
		this.dataGateway.runSQL(sql);
	}

	public void deleteTable() {
		String tableName = this.tableManager.deleteTable();
		if (tableName != null) {
			String sql = SQLBuilder.deleteTable(tableName);
			this.dataGateway.runSQL(sql);
		}
	}

	public void insertRow() {
		Object[] result = this.tableManager.insertRow();
		if (result != null) {
			String tableName = (String) result[0];
			String[] columns = (String[]) result[1];
			String[] values = (String[]) result[2];

			String sql = SQLBuilder.insertRow(tableName, columns, values);
			this.dataGateway.runSQL(sql);
		}
	}

	public void updateRow() {
		Object[] result = this.tableManager.updateRow();
		if (result != null) {
			String tableName = (String) result[0];
			String[] columns = (String[]) result[1];
			String[] values = (String[]) result[2];
			String[] oldValues = (String[]) result[3];

			String sql = SQLBuilder.updateRow(tableName, columns, values, oldValues);
			this.dataGateway.runSQL(sql);
		}
	}

	public void deleteRow() {
		Object[] result = this.tableManager.deleteRow();
		if (result != null) {
			Table table = (Table) result[0];
			int rowIndex = (int) result[1];

			String sql = SQLBuilder.deleteRow(table, rowIndex);
			this.dataGateway.runSQL(sql);
		}
	}

}
