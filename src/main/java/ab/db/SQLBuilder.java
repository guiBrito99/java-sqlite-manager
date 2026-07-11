package ab.db;

/**
 * The SQLBuilder is a utility class responsible for dynamically generating SQL
 * query strings. It abstracts the complex string manipulation required to build
 * valid SQL syntax, keeping the rest of the application clean and
 * database-agnostic.
 */
public class SQLBuilder {

	/**
	 * Generates a SQL query to create a new table if it does not already exist.
	 * 
	 * @param tableName The name of the table to create.
	 * @param columns   An array of column names for the table.
	 * @return A formatted CREATE TABLE SQL string.
	 */
	public static String createTableSQL(String tableName, String[] columns) {

		// Initialize with the base CREATE statement
		StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName);

		// Append the first column explicitly to handle formatting
		sql.append(" (").append(columns[0]);

		// Loop through the remaining columns and append them with a leading comma
		for (int i = 1; i < columns.length; i++)
			sql.append(",").append(columns[i]);

		// Close the column definition parenthesis
		sql.append(")");

		return sql.toString();
	}

	/**
	 * Generates a SQL query to drop a table if it exists.
	 * 
	 * @param tableName The name of the table to drop.
	 * @return A formatted DROP TABLE SQL string.
	 */
	public static String deleteTable(String tableName) {
		return "DROP TABLE IF EXISTS " + tableName;
	}

	/**
	 * Generates a parameterized SQL query to insert a new row. Uses '?'
	 * placeholders for values to support PreparedStatements.
	 * 
	 * @param tableName The target table.
	 * @param columns   The specific columns to populate.
	 * @return A formatted INSERT INTO SQL string with value placeholders.
	 */
	public static String insertRow(String tableName, String[] columns) {
		StringBuilder sql = new StringBuilder();

		// Inserting the value to the data base
		sql.append("INSERT INTO ").append(tableName).append("(");

		// Append all target columns followed by commas
		for (String column : columns)
			sql.append(column).append(",");

		// Elegantly replace the very last trailing comma with the VALUES keyword setup
		sql.replace(sql.length() - 1, sql.length(), ") VALUES(");

		// Generate a '?' placeholder for every column to protect against SQL Injection
		for (int i = 0; i < columns.length; i++)
			sql.append("?,");

		// Replace the final trailing comma with the closing parenthesis
		sql.replace(sql.length() - 1, sql.length(), ")");

		return sql.toString();
	}

	/**
	 * Generates a parameterized SQL query to update an existing row. Uses '?'
	 * placeholders for both the SET and WHERE clauses.
	 * 
	 * @param tableName    The target table.
	 * @param columns      The columns being updated (for the SET clause).
	 * @param tableColumns All columns in the table (for the strict WHERE matching
	 *                     clause).
	 * @return A formatted UPDATE SQL string with placeholders.
	 */
	public static String updateRow(String tableName, String[] columns, String[] tableColumns) {
		StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");

		// Build the SET clause for the columns being changed
		for (int i = 0; i < columns.length; i++) {
			sql.append(columns[i]).append(" = ?,");
		}

		// Replace the trailing comma with the WHERE keyword to begin row matching
		sql.replace(sql.length() - 1, sql.length(), " WHERE ");

		// Build a strict matching clause using every column to ensure we update the
		// exact row
		for (int i = 0; i < tableColumns.length; i++)
			sql.append(tableColumns[i]).append(" = ? AND ");

		// Remove the trailing " AND " (which is 5 characters long) from the final query
		sql.replace(sql.length() - 5, sql.length(), "");

		return sql.toString();
	}

	/**
	 * Generates a parameterized SQL query to delete a specific row. Uses strict
	 * matching across all columns in the WHERE clause.
	 * 
	 * @param tableName The target table.
	 * @param columns   All columns in the table (to strictly identify the row).
	 * @return A formatted DELETE SQL string with placeholders.
	 */
	public static String deleteRow(String tableName, String[] columns) {
		StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");

		// Build a strict matching clause using every column to ensure we delete the
		// exact row
		for (int i = 0; i < columns.length; i++)
			sql.append(columns[i]).append(" = ? AND ");

		// Remove the trailing " AND " (which is 5 characters long) from the final query
		sql.replace(sql.length() - 5, sql.length(), "");

		return sql.toString();
	}

}