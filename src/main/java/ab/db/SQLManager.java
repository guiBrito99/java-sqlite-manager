package ab.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class SQLManager {
	private Scanner scanner = new Scanner(System.in);
	private ArrayList<Table> tables = new ArrayList<>();
	private Connection connection;

	public SQLManager(Connection connection) throws SQLException {
		this.connection = connection;
		load();
	}

	private void load() throws SQLException {
		ArrayList<String> tableNames = new ArrayList<>();
		ArrayList<String[]> columnsArrays = new ArrayList<>();

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
		ArrayList<ArrayList<String[]>> rowsArrays = new ArrayList<>();
		for (int i = 0; i < tableNames.size(); i++) {
			sql = "SELECT * FROM " + tableNames.get(i);
			ResultSet rowsFetch = this.runSQL(sql);
			rowsArrays.add(new ArrayList<>());
			String[] values = new String[columnsArrays.get(i).length];
			while (rowsFetch.next()) {
				for (int j = 0; j < columnsArrays.get(i).length; i++) {
					values[j] = rowsFetch.getString(j);
				}
				rowsArrays.get(i).add(values);
			}
		}

		// Adding the tables to the internal structure
		for (int i = 0; i < tableNames.size(); i++) {
			this.tables.add(new Table(tableNames.get(i), columnsArrays.get(i), rowsArrays.get(i)));
		}

	}

	public void createTable() {
		String tableName = null;
		String[] columns = null;

		// Table name creator
		do {
			System.out.println("Table name:");

			tableName = this.scanner.nextLine();

			System.out.println("Table name is " + tableName);

			/*
			 * To avoid confusion(table name empty, returning directly to the beginning of
			 * the loop), only prints the confirm prompt if tableName is valid
			 */
			System.out.print(!tableName.isBlank() ? "Confirm y/n\n" : "");
		} while (tableName.isBlank() || this.scanner.nextLine().charAt(0) != 'y');

		// Table columns creator

		String command;
		do {
			System.out.println("Table columns, separated by comma:");

			command = this.scanner.nextLine();

			if (command != "") {
				columns = command.split(",");
				System.out.println("Columns: " + Arrays.toString(columns));
				System.out.println("Confirm y/n");
			}

		} while (command == "" || this.scanner.nextLine().charAt(0) != 'y');

		// Adding the table to the data base
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName;

		sql += " (id INTEGER PRIMARY KEY AUTOINCREMENT";

		if (columns != null)
			for (int i = 0; i < columns.length; i++)
				sql += ", " + columns[i];

		sql += ")";

		this.runSQL(sql);

		String[] allColumns = new String[columns.length + 1];

		allColumns[0] = "id";
		for (int i = 0; i < columns.length; i++)
			allColumns[i + 1] = columns[i];
		// Adding the desired table to the ArrayList representation of tables
		this.tables.add(new Table(tableName, allColumns));
	}

	public void deleteTable() {
		if (!this.tables.isEmpty()) {
			Table table = this.selectTable(this.tables, "Select table for deletion:");

			// Deleting table from the database and on the controller
			String sql = "DROP TABLE IF EXISTS " + table.getName();

			this.tables.remove(table);

			this.runSQL(sql);
		} else
			System.out.println("No table to delete");
	}

	public void insert() {
		if (!this.tables.isEmpty()) {
			Table table = this.selectTable(this.tables, "Select table to insert");
			String[] columns = this.selectColumns(table, "Select the column(s) for the insertion, separating by coma");
			String[] values = this.selectValues(columns, "Select the value(s) for each column(s)");

			// Updating internal table variable with values
			table.getRows().add(values);

			// Inserting the value to the data base
			String sql = "INSERT INTO " + table.getName() + "(";

			for (String column : columns)
				sql += column + ", ";

			sql = sql.substring(0, sql.length() - 2) + ") VALUES (";

			for (String value : values)
				sql += value + ", ";

			sql = sql.substring(0, sql.length() - 2) + ")";

			this.runSQL(sql);
		} else
			System.out.println("No table available for insertion");
	}

	public void update() {
		if (!this.tables.isEmpty()) {
			Table table = this.selectTable(this.tables, "Select table to update");
			int rowIndex = this.selectRow(table, "Select the row for the update");
			String[] columns = this.selectColumns(table, "Select column(s) to update, separating by coma");
			String[] values = this.selectValues(columns, "Select value(s) for each column(s)");
			int rowId = Integer.valueOf(values[0]);

			// Updating internal table structure
			table.getRows().set(rowIndex, values);

			// Updating the values in the data base
			String sql = "UPDATE " + table.getName() + " SET";

			for (int i = 0; i < columns.length; i++) {
				sql += " " + columns[i] + " = " + values[i] + ",";
			}

			sql = sql.substring(0, sql.length() - 2);

			sql += " WHERE id = " + rowId;

			this.runSQL(sql);
		} else
			System.out.println("No table available to update");
	}

	public void delete() {
		if (!this.tables.isEmpty()) {
			Table table = this.selectTable(this.tables, "Select table to remove one row");
			int rowIndex = this.selectRow(table, "Select the row to delete");
			int rowId = Integer.valueOf(table.getRows().get(rowIndex)[0]);
			// Updating internal table structure
			table.getRows().remove(rowIndex);

			// Updating the data structure in the data base
			String sql = "DELETE FROM " + table.getName() + " WHERE id = " + rowId;

			this.runSQL(sql);
		} else
			System.out.println("No table available to delete row");
	}

	public void print() {
		if (!this.tables.isEmpty()) {
			for (Table table : this.tables) {
				System.out.println("### " + table.getName() + "\n");

				String[] columns = table.getColumns();
				ArrayList<String[]> rows = table.getRows();

				// Calculate column widths (minimum = column header length)
				int[] columnWidths = new int[columns.length];
				for (int i = 0; i < columns.length; i++) {
					columnWidths[i] = columns[i].length();
				}
				for (String[] values : rows) {
					for (int i = 0; i < values.length; i++) {
						if (values[i] != null && (values[i].length() > columnWidths[i])) {
							columnWidths[i] = values[i].length();
						}
					}
				}

				// Build header row
				String header = "|";
				String separator = "|";
				for (int i = 0; i < columns.length; i++) {
					header += String.format(" %-" + columnWidths[i] + "s |", columns[i]);
					separator += " " + "-".repeat(columnWidths[i]) + " |";
				}
				System.out.println(header);
				System.out.println(separator);

				// Build data rows
				for (String[] row : rows) {
					String line = "|";
					for (int i = 0; i < columns.length; i++) {
						String cell = (i < row.length && row[i] != null) ? row[i] : "";
						line += String.format(" %-" + columnWidths[i] + "s |", cell);
					}
					System.out.println(line);
				}

				System.out.println();
			}
		} else
			System.out.println("No table to print");
	}

	private Table selectTable(ArrayList<Table> tables, String message) {

		int selection = -1;
		boolean valid = false;

		do {
			System.out.println(message);

			for (int i = 0; i < tables.size(); i++)
				System.out.println(i + " - " + tables.get(i).getName());

			try {
				selection = this.scanner.nextInt();
				valid = selection >= 0 && selection < this.tables.size();
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				valid = false;
			}

			if (valid) {
				System.out.println("Selected table: " + tables.get(selection).getName());
				System.out.println("Confirm y/n");
			}

			this.scanner.nextLine();

		} while (!valid || this.scanner.nextLine().charAt(0) != 'y');

		return tables.get(selection);
	}

	private String[] selectColumns(Table table, String message) {
		String[] availableColumns = table.getColumns(), selection = null, selectedColumns = null;
		ArrayList<Integer> selectedIndexes = new ArrayList<>();

		do {
			System.out.println(message);

			for (int i = 1; i < availableColumns.length; i++)
				System.out.println(i + " - " + availableColumns[i]);

			System.out.println(availableColumns.length + " - Select all columns");

			selection = this.scanner.nextLine().split(",");

			for (String string : selection)
				try {
					selectedIndexes.add(Integer.valueOf(string));
				} catch (Exception e) {
					System.out.println("Invalid integer, rejecting");
				}

			selectedIndexes.sort(Comparator.reverseOrder());

			while (selectedIndexes.getFirst() > availableColumns.length) {
				selectedIndexes.removeFirst();
			}

			if (selectedIndexes.getFirst() == availableColumns.length)
				selectedIndexes.subList(1, selectedIndexes.size()).clear();

			selectedIndexes.sort(Comparator.naturalOrder());

			String selectionOutput = "Selection: ";

			for (int index : selectedIndexes)
				selectionOutput += index + " ";

			selectionOutput = selectionOutput.substring(0, selectionOutput.length() - 2);

			System.out.println(selectionOutput);

			System.out.println("Confirm y/n");

		} while (selectedIndexes.isEmpty() || this.scanner.nextLine().charAt(0) != 'y');

		if (selectedIndexes.getFirst() == availableColumns.length)
			selectedColumns = availableColumns;
		else {
			selectedColumns = new String[selectedIndexes.size()];
			for (int i = 0; i < selectedIndexes.size(); i++)
				selectedColumns[i] = availableColumns[selectedIndexes.get(i)];
		}

		return selectedColumns;
	}

	private int selectRow(Table table, String message) {
		int row = -1;
		String[] columns = table.getColumns();
		ArrayList<String[]> rows = table.getRows();
		String rowString = null;
		boolean valid = false;

		do {
			System.out.println(message);
			for (int i = 0; i < rows.size(); i++) {
				rowString = i + " - ";
				for (int j = 0; j < rows.size(); j++)
					rowString += "(" + columns[j] + ")" + rows.get(i)[j] + " ";

				System.out.println(rowString.substring(0, rowString.length() - 2));
			}

			try {
				row = this.scanner.nextInt();
				valid = row < 0 || row >= rows.size();
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				valid = false;
			}

			if (valid) {
				System.out.println("Selected row:");

				for (int i = 0; i < rows.size(); i++)
					rowString += "(" + columns[i] + ")" + rows.get(row)[i] + " ";

				System.out.println(rowString.substring(0, rowString.length() - 2));
			}

		} while (!valid || this.scanner.nextLine().charAt(0) != 'y');

		return row;
	}

	private String[] selectValues(String[] columns, String message) {
		String[] values = new String[columns.length];
		do {
			System.out.println(message);
			for (int i = 0; i < columns.length; i++) {
				System.out.print(columns[i] + ": ");
				values[i] = this.scanner.nextLine();
			}

			System.out.println("Selected values:");
			for (int i = 0; i < columns.length; i++)
				System.out.println(columns[i] + ": " + values[i]);

			System.out.println("Confirm y/n");

		} while (this.scanner.nextLine().charAt(0) != 'y');

		return values;
	}

	private ResultSet runSQL(String sql) {
		ResultSet resultSet = null;
		try (Statement statement = this.connection.createStatement()) {
			statement.execute(sql);
			resultSet = statement.getResultSet();
			System.out.println("SQL complete");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return resultSet;
	}

}
