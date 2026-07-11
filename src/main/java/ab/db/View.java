package ab.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class View {
	static Scanner scanner = new Scanner(System.in);

	public static void main(String... args) throws SQLException {
		DatabaseController databaseController = new DatabaseController();

		try {
			if (args.length == 0) {
				databaseMenu(databaseController);
			} else {

				switch (args[0]) {
				case "print":
					databaseController.print();
					break;

				case "create":

					if (args.length == 3) {
						String tableName = args[1];
						String[] columns = args[2].split(",");

						databaseController.createTable(tableName, columns);
					} else
						System.out.println("Invalid arguments");

					break;

				case "drop":

					if (args.length == 2) {
						String tableName = args[1];

						databaseController.deleteTable(tableName);
					} else
						System.out.println("Invalid arguments");

					break;

				case "insert":

					if (args.length == 4) {
						String tableName = args[1];
						String[] columns = args[2].split(",");
						String[] values = args[3].split(",");

						databaseController.insertRow(tableName, columns, values);
					} else
						System.out.println("Invalid arguments");

					break;

				case "delete":

					if (args.length == 3) {
						String tableName = args[1];
						int rowIndex = Integer.parseInt(args[2]);

						databaseController.deleteRow(tableName, rowIndex);
					} else
						System.out.println("Invalid arguments");

					break;

				case "update":

					if (args.length == 5) {
						String tableName = args[1];
						int rowIndex = Integer.parseInt(args[2]);
						String[] columns = args[3].split(",");
						String[] values = args[4].split(",");
						String[] tableColumns = databaseController.getTableColumns(tableName);
						String[] oldValues = databaseController.getRowValues(tableName, rowIndex);

						databaseController.updateRow(tableName, columns, values, tableColumns, oldValues, rowIndex);

					} else
						System.out.println("Invalid arguments");

					break;

				default:
					System.out.println("Unknow command");

				}

			}
		} finally {
			databaseController.close();
		}

	}

	private static void databaseMenu(DatabaseController databaseController) throws SQLException {

		int command;

		do {
			System.out.println("Select the command:");
			System.out.println("1 - Print");
			System.out.println("2 - Create table");
			System.out.println("3 - Delete table");
			System.out.println("4 - Insert row");
			System.out.println("5 - Delete row");
			System.out.println("6 - Update row");
			System.out.println("0 - Exit");

			try {
				command = Integer.parseInt(getInput());
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				command = -1;
			}

			switch (command) {
			case 1:
				print(databaseController);
				break;
			case 2:
				createTable(databaseController);
				break;
			case 3:
				deleteTable(databaseController);
				break;
			case 4:
				insertRow(databaseController);
				break;
			case 5:
				deleteRow(databaseController);
				break;
			case 6:
				updateRow(databaseController);
				break;
			default:
			}

		} while (command != 0);

		System.out.println("Exiting...");
	}

	private static void print(DatabaseController databaseController) {
		databaseController.print();
	}

	private static void createTable(DatabaseController databaseController) {
		String tableName = null;
		String[] columns = null;
		// Table name creator
		System.out.println("Table creation");
		do {
			System.out.println("Table name:");

			tableName = getInput();

			if (!tableName.isBlank() && tableName.matches("^[a-zA-Z0-9]+$"))
				System.out.println("Table name is " + tableName);

		} while (tableName.isBlank() || !tableName.matches("^[a-zA-Z0-9]+$") || !inputValidation("Confirm y/n"));

		// Table columns creator
		String command;
		boolean valid = true;
		do {

			System.out.println("Table columns, separated by comma:");

			command = getInput();

			if (!command.isBlank()) {
				columns = command.split(",");
				if (columns.length != 0) {
					int index = 0;
					valid = true;
					while (index < columns.length && valid) {
						String column = columns[index];
						valid = column.matches("^[a-zA-Z0-9]+$");
						index++;
					}

					if (valid)
						System.out.println("Columns: " + Arrays.toString(columns));
				}
			}

		} while (command.isBlank() || columns.length == 0 || !valid || !inputValidation("Confirm y/n"));

		databaseController.createTable(tableName, columns);

	}

	private static void deleteTable(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		if (tablesNames != null) {
			String tableName = selectTable("Select table for deletion:", tablesNames);
			databaseController.deleteTable(tableName);
		}
	}

	private static void insertRow(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		if (tablesNames != null) {
			String tableName = selectTable("Select table to insert", tablesNames);

			String[] tableColumns = databaseController.getTableColumns(tableName);
			String[] columns = selectColumns("Select the column(s) for the insertion, separating by coma",
					tableColumns);

			String[] values = selectValues("Select the value(s) for each column(s)", columns);

			databaseController.insertRow(tableName, columns, values);
		} else
			System.out.println("Option unavailable");
	}

	private static void deleteRow(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		if (tablesNames != null) {
			String tableName = selectTable("Select table to remove one row", tablesNames);
			String[][] valuesMatrix = databaseController.getValuesMatrix(tableName);
			if (valuesMatrix.length != 0) {
				String[] columns = databaseController.getTableColumns(tableName);
				int rowIndex = selectRow("Select the row to delete", columns, valuesMatrix);

				databaseController.deleteRow(tableName, rowIndex);
			} else
				System.out.println("Option unavailable");
		} else
			System.out.println("Option unavailable");
	}

	private static void updateRow(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		if (tablesNames != null) {
			String tableName = selectTable("Select table to update", tablesNames);
			String[][] valuesMatrix = databaseController.getValuesMatrix(tableName);
			if (valuesMatrix.length != 0) {
				String[] tableColumns = databaseController.getTableColumns(tableName);
				int rowIndex = selectRow("Select the row for the update", tableColumns, valuesMatrix);
				String[] oldValues = databaseController.getRowValues(tableName, rowIndex);

				String[] columns = selectColumns("Select column(s) to update, separating by coma", tableColumns);
				String[] values = selectValues("Select value(s) for each column(s)", columns);

				databaseController.updateRow(tableName, columns, values, tableColumns, oldValues, rowIndex);
			} else
				System.out.println("Option unavailable");
		} else
			System.out.println("Option unavailable");
	}

	private static String selectTable(String message, String[] tablesNames) {

		int selection = -1;
		boolean valid = false;

		do {
			System.out.println(message);

			for (int i = 0; i < tablesNames.length; i++)
				System.out.println(i + " - " + tablesNames[i]);

			try {
				selection = Integer.parseInt(getInput());
				valid = selection >= 0 && selection < tablesNames.length;
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				valid = false;
			}

			if (valid) {
				System.out.println("Selected table: " + tablesNames[selection]);
			}

		} while (!valid || !inputValidation("Confirm y/n"));

		return tablesNames[selection];
	}

	private static String[] selectColumns(String message, String[] availableColumns) {
		String[] selection = null, selectedColumns = null;
		ArrayList<Integer> selectedIndexes = new ArrayList<>();
		LinkedHashSet<Integer> indexSelection = new LinkedHashSet<>();

		do {
			System.out.println(message);

			for (int i = 0; i < availableColumns.length; i++)
				System.out.println(i + " - " + availableColumns[i]);

			System.out.println(availableColumns.length + " - Select all columns");

			selection = getInput().split(",");

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

			selectedIndexes.removeIf(index -> index > availableColumns.length || index < 0);

			if (!selectedIndexes.isEmpty()) {
				if (selectedIndexes.getFirst() == availableColumns.length)
					selectedIndexes.subList(1, selectedIndexes.size()).clear();

				selectedIndexes.sort(Comparator.naturalOrder());

				String selectionOutput = "Selection: ";

				for (int index : selectedIndexes)
					selectionOutput += availableColumns.length == index ? "All columns "
							: availableColumns[index] + " ";

				System.out.println(selectionOutput.substring(0, selectionOutput.length() - 1));
			}
		} while (selectedIndexes.isEmpty() || !inputValidation("Confirm y/n"));

		if (selectedIndexes.getFirst() == availableColumns.length)
			selectedColumns = availableColumns;
		else {
			selectedColumns = new String[selectedIndexes.size()];
			for (int i = 0; i < selectedIndexes.size(); i++)
				selectedColumns[i] = availableColumns[selectedIndexes.get(i)];
		}

		return selectedColumns;
	}

	private static String[] selectValues(String message, String[] columns) {
		String[] values = new String[columns.length];
		do {
			System.out.println(message);
			for (int i = 0; i < columns.length; i++) {
				System.out.print(columns[i] + ": ");
				values[i] = getInput();
			}

			System.out.println("Selected values:");
			for (int i = 0; i < columns.length; i++)
				System.out.println(columns[i] + ": " + values[i]);

		} while (!inputValidation("Confirm y/n"));

		return values;
	}

	private static int selectRow(String message, String[] columns, String[][] valuesMatrix) {
		int row = -1;
		boolean valid = false;

		do {
			System.out.println(message);

			int[] columnWidths = new int[columns.length];
			for (int i = 0; i < columns.length; i++) {
				columnWidths[i] = columns[i].length();
			}
			for (String[] values : valuesMatrix) {
				for (int i = 0; i < values.length; i++) {
					if (values[i] != null && (values[i].length() > columnWidths[i])) {
						columnWidths[i] = values[i].length();
					}
				}
			}

			StringBuilder header = new StringBuilder("    |");
			StringBuilder separator = new StringBuilder("    |");
			for (int i = 0; i < columns.length; i++) {
				header.append(String.format(" %-" + columnWidths[i] + "s |", columns[i]));
				separator.append(" ").append("-".repeat(columnWidths[i])).append(" |");
			}

			System.out.println(header.toString());
			System.out.println(separator.toString());

			StringBuilder[] lines = new StringBuilder[valuesMatrix.length];

			for (int i = 0; i < valuesMatrix.length; i++) {
				StringBuilder line = new StringBuilder(i + " - |");
				StringBuilder cell = new StringBuilder();
				for (int j = 0; j < columns.length; j++) {
					cell.setLength(0);
					cell.append((valuesMatrix[i][j] != null) ? valuesMatrix[i][j] : "");
					line.append(String.format(" %-" + columnWidths[j] + "s |", cell));
				}
				System.out.println(line.toString());
				lines[i] = new StringBuilder(line.toString());
			}

			try {
				row = Integer.parseInt(getInput());
				valid = row >= 0 && row < valuesMatrix.length;
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				valid = false;
			}

			if (valid) {
				System.out.println("Selected row:");
				System.out.println(lines[row].toString());
			}

		} while (!valid || !inputValidation("Confirm y/n"));

		return row;
	}

	private static boolean inputValidation(String message) {
		boolean confirm = false;
		String command = null;
		char firstChar = 'a';

		do {
			System.out.println(message);
			command = getInput().toLowerCase();

			if (command.isBlank())
				System.out.println("Type in valid character");
			else
				firstChar = command.charAt(0);

		} while (command.isBlank() || (firstChar != 'y' && firstChar != 'n'));

		confirm = firstChar == 'y';

		return confirm;
	}

	private static String getInput() {

		if (!scanner.hasNextLine()) {
			System.out.println("\nTerminal stream closed. Exiting safely...");
			System.exit(0);
		}

		return scanner.nextLine();
	}

}
