package ab.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class TableManager {
	private Scanner scanner = new Scanner(System.in);
	private ArrayList<Table> tables = new ArrayList<>();

	public TableManager(ArrayList<Table> tables) {
		this.tables = tables;
	}

	public Object[] createTable() {
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
		} while (tableName.isBlank() || !this.inputValidation("Confirm y/n"));

		// Table columns creator
		String command;
		do {
			System.out.println("Table columns, separated by comma:");

			command = this.scanner.nextLine();

			if (command != "") {
				columns = command.split(",");
				System.out.println("Columns: " + Arrays.toString(columns));
			}

		} while (command == "" || !this.inputValidation("Confirm y/n"));

		Table newTable = new Table(tableName, columns);

		// Adding table to internal data structure
		this.tables.add(newTable);

		return new Object[] { newTable.getName(), newTable.getColumns() };
	}

	public String deleteTable() {
		String tableName = null;

		if (!this.tables.isEmpty()) {
			Table table = this.selectTable("Select table for deletion:");
			tableName = table.getName();
			// Deleting selected table from the internal data structure
			this.tables.remove(table);
		} else
			System.out.println("No table to delete");

		return tableName;
	}

	public Object[] insertRow() {

		Object[] result = null;

		if (!this.tables.isEmpty()) {
			Table table = this.selectTable("Select table to insert");
			String[] columns = this.selectColumns(table, "Select the column(s) for the insertion, separating by coma");
			String[] values = this.selectValues(columns, "Select the value(s) for each column(s)");

			// Updating internal table variable with values
			table.getRows().add(values);

			result = new Object[] { table.getName(), columns, values };

		} else
			System.out.println("No table available for insertion");

		return result;
	}

	public Object[] updateRow() {
		Object[] result = null;

		if (!this.tables.isEmpty()) {
			Table table = this.selectTable("Select table to update");
			int rowIndex = this.selectRow(table, "Select the row for the update");
			String[] columns = this.selectColumns(table, "Select column(s) to update, separating by coma");
			String[] values = this.selectValues(columns, "Select value(s) for each column(s)");

			String[] oldValues = table.getRows().get(rowIndex);

			// Updating internal table structure
			table.getRows().set(rowIndex, values);

			result = new Object[] { table.getName(), columns, values, oldValues };

		} else
			System.out.println("No table available to update");

		return result;
	}

	public Object[] deleteRow() {
		Object[] result = null;

		if (!tables.isEmpty()) {
			Table table = this.selectTable("Select table to remove one row");
			int rowIndex = this.selectRow(table, "Select the row to delete");

			// Updating internal table structure
			table.getRows().remove(rowIndex);

			result = new Object[] { table, rowIndex };
		} else
			System.out.println("No table available to delete row");

		return result;
	}

	public void printTables() {
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

	private Table selectTable(String message) {

		int selection = -1;
		boolean valid = false;

		do {
			System.out.println(message);

			for (int i = 0; i < this.tables.size(); i++)
				System.out.println(i + " - " + this.tables.get(i).getName());

			try {
				selection = this.scanner.nextInt();
				this.scanner.nextLine();
				valid = selection >= 0 && selection < this.tables.size();
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				valid = false;
			}

			if (valid) {
				System.out.println("Selected table: " + this.tables.get(selection).getName());
			}

		} while (!valid || !this.inputValidation("Confirm y/n"));

		return this.tables.get(selection);
	}

	private String[] selectColumns(Table table, String message) {
		String[] availableColumns = table.getColumns(), selection = null, selectedColumns = null;
		ArrayList<Integer> selectedIndexes = new ArrayList<>();
		LinkedHashSet<Integer> indexSelection = new LinkedHashSet<>();

		do {
			System.out.println(message);

			for (int i = 0; i < availableColumns.length; i++)
				System.out.println(i + " - " + availableColumns[i]);

			System.out.println(availableColumns.length + " - Select all columns");

			selection = this.scanner.nextLine().split(",");

			selectedIndexes.clear();
			indexSelection.clear();

			// Using LinkedHashSet so no duplicate values are stored
			for (String string : selection)
				try {
					indexSelection.add(Integer.valueOf(string));
				} catch (Exception e) {
					System.out.println("Invalid integer, rejecting");
				}

			// Now adding all values to the ArrayList to perform modifications
			selectedIndexes.addAll(indexSelection);
			selectedIndexes.sort(Comparator.reverseOrder());

			while (selectedIndexes.getFirst() > availableColumns.length) {
				selectedIndexes.removeFirst();
			}

			if (selectedIndexes.getFirst() == availableColumns.length)
				selectedIndexes.subList(1, selectedIndexes.size()).clear();

			selectedIndexes.sort(Comparator.naturalOrder());

			String selectionOutput = "Selection: ";

			for (int index : selectedIndexes)
				selectionOutput += availableColumns.length == index ? "All columns " : availableColumns[index] + " ";

			System.out.println(selectionOutput.substring(0, selectionOutput.length() - 1));

		} while (selectedIndexes.isEmpty() || !this.inputValidation("Confirm y/n"));

		if (selectedIndexes.getFirst() == availableColumns.length)
			selectedColumns = availableColumns;
		else {
			selectedColumns = new String[selectedIndexes.size()];
			for (int i = 0; i < selectedIndexes.size(); i++)
				selectedColumns[i] = availableColumns[selectedIndexes.get(i)];
		}

		return selectedColumns;
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

		} while (!this.inputValidation("Confirm y/n"));

		return values;
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
				for (int j = 0; j < columns.length; j++)
					rowString += "(" + columns[j] + ")" + rows.get(i)[j] + " ";

				System.out.println(rowString.substring(0, rowString.length() - 1));
			}

			try {
				row = this.scanner.nextInt();
				this.scanner.nextLine();
				valid = row >= 0 && row < rows.size();
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				valid = false;
			}

			if (valid) {
				System.out.println("Selected row:");

				System.out.println(rowString.substring(0, rowString.length() - 1));
			}

		} while (!valid || !this.inputValidation("Confirm y/n"));

		return row;
	}

	private boolean inputValidation(String message) {
		boolean confirm = false;
		String command = null;
		char firstChar = 'a';

		do {
			System.out.println(message);
			command = this.scanner.nextLine().toLowerCase();

			if (command == "")
				System.out.println("Type in valid character");
			else
				firstChar = command.charAt(0);

		} while (command == "" || (firstChar != 'y' && firstChar != 'n'));

		confirm = firstChar == 'y';

		return confirm;
	}
}
