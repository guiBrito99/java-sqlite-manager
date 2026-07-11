package ab.db;

public class SQLBuilder {

	public static String createTableSQL(String tableName, String[] columns) {

		StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName);

		sql.append(" (").append(columns[0]);

		for (int i = 1; i < columns.length; i++)
			sql.append(",").append(columns[i]);

		sql.append(")");

		return sql.toString();
	}

	public static String deleteTable(String tableName) {
		return "DROP TABLE IF EXISTS " + tableName;
	}

	public static String insertRow(String tableName, String[] columns) {
		StringBuilder sql = new StringBuilder();

		// Inserting the value to the data base
		sql.append("INSERT INTO ").append(tableName).append("(");

		for (String column : columns)
			sql.append(column).append(",");

		sql.replace(sql.length() - 1, sql.length(), ") VALUES(");

		for (int i = 0; i < columns.length; i++)
			sql.append("?,");

		sql.replace(sql.length() - 1, sql.length(), ")");

		return sql.toString();
	}

	public static String updateRow(String tableName, String[] columns, String[] tableColumns) {
		StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");

		for (int i = 0; i < columns.length; i++) {
			sql.append(columns[i]).append(" = ?,");
		}

		sql.replace(sql.length() - 1, sql.length(), " WHERE ");

		for (int i = 0; i < tableColumns.length; i++)
			sql.append(tableColumns[i]).append(" = ? AND ");

		sql.replace(sql.length() - 5, sql.length(), "");

		return sql.toString();
	}

	public static String deleteRow(String tableName, String[] columns) {
		StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");

		for (int i = 0; i < columns.length; i++)
			sql.append(columns[i]).append(" = ? AND ");

		sql.replace(sql.length() - 5, sql.length(), "");

		return sql.toString();
	}

}
