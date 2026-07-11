package ab.db;

import java.util.ArrayList;
import java.util.List;

/**
 * The TableManager acts as the in-memory cache and state manager for the
 * database. It holds the structural and data representation of the database in
 * RAM, allowing the application to read and format data instantly without
 * querying the hard drive.
 */
public class TableManager {
	// The internal data structure storing all tables currently loaded into memory
	private ArrayList<Table> tables = new ArrayList<>();

	/**
	 * Initializes the TableManager with a pre-existing list of tables, typically
	 * loaded by the DataGateway during application startup.
	 * 
	 * @param tables The populated list of Table objects.
	 */
	public TableManager(ArrayList<Table> tables) {
		this.tables = tables;
	}

	/**
	 * Creates a new in-memory table.
	 * 
	 * @param tableName The name of the new table.
	 * @param columns   An array of column names.
	 */
	public void createTable(String tableName, String[] columns) {
		// Convert the standard array into an ArrayList for the Table constructor
		ArrayList<String> columnsArrayList = new ArrayList<>(List.of(columns));

		Table newTable = new Table(tableName, columnsArrayList);

		// Adding table to internal data structure
		this.tables.add(newTable);
	}

	/**
	 * Removes a table completely from the in-memory representation.
	 * 
	 * @param tableName The name of the table to delete.
	 */
	public void deleteTable(String tableName) {
		Table table = this.findTable(tableName);
		if (table != null)
			this.tables.remove(table);
		else
			System.out.println(tableName + " do not exists");
	}

	/**
	 * Inserts a new row of data into an existing table in memory.
	 * 
	 * @param tableName The target table.
	 * @param columns   The specific columns receiving the values.
	 * @param values    The values to insert.
	 */
	public void insertRow(String tableName, String[] columns, String[] values) {
		Table table = this.findTable(tableName);

		if (table != null) {
			// Updating internal table variable with values
			ArrayList<String> tableColumns = table.getColumns();
			ArrayList<String[]> tableRows = table.getRows();

			// Create a new empty row sized to the total number of columns in the table
			tableRows.add(new String[tableColumns.size()]);

			// Map the provided values to their correct column indexes.
			// This ensures data goes to the right place even if the user didn't
			// provide values for every column, or provided them out of order.
			for (int i = 0; i < columns.length; i++) {
				int internalIndex = tableColumns.indexOf(columns[i]);
				tableRows.getLast()[internalIndex] = values[i];
			}
		} else
			System.out.println(tableName + " do not exists");
	}

	/**
	 * Updates an existing row in memory.
	 * 
	 * @param tableName The target table.
	 * @param rowIndex  The zero-based index of the row to update.
	 * @param columns   The columns being updated.
	 * @param values    The new values.
	 */
	public void updateRow(String tableName, int rowIndex, String[] columns, String[] values) {
		Table table = this.findTable(tableName);

		if (table != null) {
			ArrayList<String> tableColumns = table.getColumns();
			// Retrieve the existing row reference directly from the list
			String[] tableValues = table.getRows().get(rowIndex);

			// Overwrite the specific column indexes with the new values
			for (int i = 0; i < columns.length; i++) {
				int internalIndex = tableColumns.indexOf(columns[i]);
				tableValues[internalIndex] = values[i];
			}
		} else
			System.out.println(tableName + " do not exists");
	}

	/**
	 * Removes a row from a table in memory.
	 * 
	 * @param tableName The target table.
	 * @param rowIndex  The zero-based index of the row to delete.
	 */
	public void deleteRow(String tableName, int rowIndex) {
		Table table = this.findTable(tableName);

		if (table != null)
			table.getRows().remove(rowIndex);
		else
			System.out.println(tableName + " do not exists");
	}

	/**
	 * Renders a visually aligned, Markdown-style representation of all tables and
	 * their data to the standard output.
	 */
	public void printTables() {
		if (!this.tables.isEmpty())
			for (Table table : this.tables) {
				System.out.println("### " + table.getName() + "\n");

				ArrayList<String> columns = table.getColumns();
				ArrayList<String[]> rows = table.getRows();

				// Calculate column widths (minimum = column header length)
				// This ensures the printed table remains perfectly aligned regardless of data
				// size.
				int[] columnWidths = new int[columns.size()];
				for (int i = 0; i < columns.size(); i++) {
					columnWidths[i] = columns.get(i).length();
				}
				for (String[] values : rows) {
					for (int i = 0; i < values.length; i++) {
						if (values[i] != null && (values[i].length() > columnWidths[i])) {
							columnWidths[i] = values[i].length();
						}
					}
				}

				// Build header row with dynamic spacing
				StringBuilder header = new StringBuilder("|");
				StringBuilder separator = new StringBuilder("|");
				for (int i = 0; i < columns.size(); i++) {
					header.append(String.format(" %-" + columnWidths[i] + "s |", columns.get(i)));
					separator.append(" ").append("-".repeat(columnWidths[i])).append(" |");
				}

				System.out.println(header.toString());
				System.out.println(separator.toString());

				// Build and print data rows
				for (String[] row : rows) {
					StringBuilder line = new StringBuilder("|");
					StringBuilder cell = new StringBuilder();
					for (int i = 0; i < columns.size(); i++) {
						cell.setLength(0);
						// Safely handle null values to prevent formatting crashes
						cell.append((row[i] != null) ? row[i] : "");
						line.append(String.format(" %-" + columnWidths[i] + "s |", cell));
					}
					System.out.println(line.toString());
				}

				System.out.println();
			}
		else
			System.out.println("No table to print");
	}

	/**
	 * Retrieves an array of all table names currently in memory.
	 * 
	 * @return An array of table name strings.
	 */
	public String[] getTablesNames() {
		String[] tableNames = null;

		if (!this.tables.isEmpty()) {
			tableNames = new String[this.tables.size()];
			for (int i = 0; i < this.tables.size(); i++)
				tableNames[i] = this.tables.get(i).getName();
		} else
			System.out.println("No table in the database");

		return tableNames;
	}

	/**
	 * Retrieves the column names for a specific table.
	 * 
	 * @param tableName The name of the target table.
	 * @return An array of column name strings.
	 */
	public String[] getTableColumns(String tableName) {
		String[] tableColumns = null;

		Table table = this.findTable(tableName);

		if (table != null)
			tableColumns = table.getColumns().toArray(new String[table.getColumns().size()]);

		return tableColumns;
	}

	/**
	 * Generates a standard 2D array representation of a table's data.
	 * 
	 * @param tableName The target table.
	 * @return A 2D array matrix of the table's rows and columns.
	 */
	public String[][] getValuesMatrix(String tableName) {
		Table table = this.findTable(tableName);
		String[][] valuesMatrix = null;

		if (table != null) {
			int rowsSize = table.getRows().size();
			int columnsSize = table.getColumns().size();

			valuesMatrix = new String[rowsSize][columnsSize];

			// Translate the ArrayList<String[]> into a strict String[][] matrix
			for (int i = 0; i < rowsSize; i++) {
				String[] row = table.getRows().get(i);
				for (int j = 0; j < columnsSize; j++) {
					valuesMatrix[i][j] = row[j];
				}
			}
		}

		return valuesMatrix;
	}

	/**
	 * Fetches the data array for a single, specific row.
	 * 
	 * @param tableName The target table.
	 * @param rowIndex  The index of the row.
	 * @return Array of string values for that row.
	 */
	public String[] getRowValues(String tableName, int rowIndex) {
		String[] rowValues = null;
		Table table = this.findTable(tableName);

		if (table != null)
			rowValues = table.getRows().get(rowIndex);

		return rowValues;
	}

	/**
	 * Helper method to locate a Table object by its string name. Performs a linear
	 * search through the internal tables list.
	 * 
	 * @param tableName The name of the table to find.
	 * @return The Table object if found, or null.
	 */
	private Table findTable(String tableName) {
		Table table = null;
		if (!this.tables.isEmpty()) {
			int index = -1;

			// Iterate through the list until the name matches or the end is reached
			do {
				index++;
			} while (index < this.tables.size() && !this.tables.get(index).getName().equals(tableName));

			// If the index didn't overflow the list size, we found a match
			table = index < this.tables.size() ? this.tables.get(index) : null;
		}

		return table;
	}

}