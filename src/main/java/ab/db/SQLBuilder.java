package ab.db;

public class SQLBuilder {

	public static String createTableSQL(String tableName, String[] columns) {

		String sql = "CREATE TABLE IF NOT EXISTS " + tableName;

		sql += " (" + columns[0];

		for (int i = 1; i < columns.length; i++)
			sql += "," + columns[i];

		sql += ")";

		return sql;
	}

	public static String deleteTable(String tableName) {
		return "DROP TABLE IF EXISTS " + tableName;
	}

	public static String insertRow(String tableName, String[] columns, String[] values) {
		String sql = null;

		// Inserting the value to the data base
		sql = "INSERT INTO " + tableName + "(";

		for (String column : columns)
			sql += column + ", ";

		sql = sql.substring(0, sql.length() - 2) + ") VALUES(";

		for (String value : values)
			sql += "'" + value + "', ";

		sql = sql.substring(0, sql.length() - 2) + ")";

		return sql;
	}

	public static String updateRow(String tableName, String[] columns, String[] values, String[] tableColumns,
			String[] oldValues) {
		String sql = null;

		// Updating the values in the data base
		String stringBuilding = "UPDATE " + tableName + " SET";

		for (int i = 0; i < columns.length; i++) {
			stringBuilding += " " + columns[i] + " = '" + values[i] + "',";
		}

		stringBuilding = stringBuilding.substring(0, stringBuilding.length() - 1) + " WHERE ";

		for (int i = 0; i < tableColumns.length; i++) {
			if (oldValues[i] == null)
				stringBuilding += tableColumns[i] + " IS NULL AND ";
			else
				stringBuilding += tableColumns[i] + " = '" + oldValues[i] + "' AND ";
		}

		sql = stringBuilding.substring(0, stringBuilding.length() - 5);

		System.out.println(sql);
		
		return sql;
	}

	public static String deleteRow(String tableName, String[] columns, String[] values) {
		String sql = null;

		// Updating the data structure in the data base
		String sqlBuilding = "DELETE FROM " + tableName + " WHERE ";

		for (int i = 0; i < columns.length; i++)
			if(values[i] == null)
				sqlBuilding += columns[i] + " IS NULL AND ";
			else
				sqlBuilding += columns[i] + " = '" + values[i] + "' AND ";

		sql = sqlBuilding.substring(0, sqlBuilding.length() - 5);

		return sql;
	}

}
