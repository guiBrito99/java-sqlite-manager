package ab.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Scanner;

/**
 * The View class serves as the primary user interface for the database
 * application. It supports two modes of operation: 1. Automated CLI mode (when
 * arguments are passed during execution). 2. Interactive Menu mode (when no
 * arguments are passed).
 */
public class View {
	// Scanner used to read user input from the console
	static Scanner scanner = new Scanner(System.in);

	/**
	 * The main entry point of the application. Evaluates the execution arguments to
	 * determine whether to launch the CLI or the interactive menu, and ensures the
	 * database connection is closed safely.
	 *
	 * @param args Command line arguments passed to the program.
	 * @throws SQLException If a database access error occurs.
	 */
	public static void main(String... args) throws SQLException {
		DatabaseController databaseController = new DatabaseController();

		try {
			// If no arguments are provided, start the interactive loop
			if (args.length == 0) {
				databaseMenu(databaseController);
			} else {
				// Automated CLI mode using the provided arguments
				switch (args[0]) {
				case "print":
					databaseController.print();
					break;

				case "create":
					// Expected syntax: create <tableName> <column1,column2,...>
					if (args.length == 3) {
						String tableName = args[1];
						String[] columns = args[2].split(",");

						databaseController.createTable(tableName, columns);
					} else
						System.out.println("Invalid arguments");

					break;

				case "drop":
					// Expected syntax: drop <tableName>
					if (args.length == 2) {
						String tableName = args[1];

						databaseController.deleteTable(tableName);
					} else
						System.out.println("Invalid arguments");

					break;

				case "insert":
					// Expected syntax: insert <tableName> <col1,col2> <val1,val2>
					if (args.length == 4) {
						String tableName = args[1];
						String[] columns = args[2].split(",");
						String[] values = args[3].split(",");

						databaseController.insertRow(tableName, columns, values);
					} else
						System.out.println("Invalid arguments");

					break;

				case "delete":
					// Expected syntax: delete <tableName> <rowIndex>
					if (args.length == 3) {
						String tableName = args[1];
						int rowIndex = Integer.parseInt(args[2]);

						databaseController.deleteRow(tableName, rowIndex);
					} else
						System.out.println("Invalid arguments");

					break;

				case "update":
					// Expected syntax: update <tableName> <rowIndex> <col1,col2> <val1,val2>
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
			// Ensures the database connection is cleanly closed regardless of execution
			// outcome
			databaseController.close();
		}

	}

	/**
	 * Runs the interactive console menu for manual database management. Loops
	 * continuously until the user selects the exit option (0).
	 *
	 * @param databaseController The controller managing database operations.
	 * @throws SQLException If a database access error occurs.
	 */
	private static void databaseMenu(DatabaseController databaseController) throws SQLException {

		int command;

		do {
			// Display main menu options
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

			// Route the user's selection to the corresponding method
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

	/**
	 * Triggers the database print operation.
	 * 
	 * @param databaseController The controller managing database operations.
	 */
	private static void print(DatabaseController databaseController) {
		databaseController.print();
	}

	/**
	 * Handles the interactive flow for creating a new database table. Prompts for
	 * the table name and column definitions, validating alphanumeric formatting.
	 *
	 * @param databaseController The controller managing database operations.
	 */
	private static void createTable(DatabaseController databaseController) {
		String tableName = null;
		String[] columns = null;
		// Table name creator
		System.out.println("Table creation");
		do {
			System.out.println("Table name:");

			tableName = getInput();

			// Validate that the table name contains only alphanumeric characters
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
					// Validate that every column name contains only alphanumeric characters
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

		// Execute table creation
		databaseController.createTable(tableName, columns);

	}

	/**
	 * Handles the interactive flow for deleting a database table.
	 *
	 * @param databaseController The controller managing database operations.
	 */
	private static void deleteTable(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		if (tablesNames != null) {
			String tableName = selectTable("Select table for deletion:", tablesNames);
			databaseController.deleteTable(tableName);
		}
	}

	/**
	 * Handles the interactive flow for inserting a new row into a specific table.
	 *
	 * @param databaseController The controller managing database operations.
	 */
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

	/**
	 * Handles the interactive flow for deleting a specific row from a table.
	 *
	 * @param databaseController The controller managing database operations.
	 */
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

	/**
	 * Handles the interactive flow for updating an existing row in a table.
	 *
	 * @param databaseController The controller managing database operations.
	 */
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

	/**
	 * Utility method to display available tables and allow the user to select one.
	 *
	 * @param message     Prompt to display to the user.
	 * @param tablesNames Array of available table names.
	 * @return The name of the selected table.
	 */
	private static String selectTable(String message, String[] tablesNames) {

		int selection = -1;
		boolean valid = false;

		do {
			System.out.println(message);

			// List available tables with their index
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

	/**
	 * Prompts the user to select one, multiple, or all columns from a table.
	 * Ensures no duplicates are selected and handles "Select all" functionality.
	 *
	 * @param message          Prompt to display to the user.
	 * @param availableColumns Array of all columns available in the table.
	 * @return An array of the selected column names.
	 */
	private static String[] selectColumns(String message, String[] availableColumns) {
		String[] selection = null, selectedColumns = null;
		ArrayList<Integer> selectedIndexes = new ArrayList<>();
		LinkedHashSet<Integer> indexSelection = new LinkedHashSet<>();

		do {
			System.out.println(message);

			// Display individual column options
			for (int i = 0; i < availableColumns.length; i++)
				System.out.println(i + " - " + availableColumns[i]);

			// Add the dynamic "Select all columns" option
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

			// Filter out any indexes that are out of bounds
			selectedIndexes.removeIf(index -> index > availableColumns.length || index < 0);

			if (!selectedIndexes.isEmpty()) {
				// If "Select all" is chosen, remove any individual column selections
				if (selectedIndexes.getFirst() == availableColumns.length)
					selectedIndexes.subList(1, selectedIndexes.size()).clear();

				selectedIndexes.sort(Comparator.naturalOrder());

				String selectionOutput = "Selection: ";

				// Build human-readable confirmation string
				for (int index : selectedIndexes)
					selectionOutput += availableColumns.length == index ? "All columns "
							: availableColumns[index] + " ";

				System.out.println(selectionOutput.substring(0, selectionOutput.length() - 1));
			}
		} while (selectedIndexes.isEmpty() || !inputValidation("Confirm y/n"));

		// Process final return array: either all columns, or the specific subset chosen
		if (selectedIndexes.getFirst() == availableColumns.length)
			selectedColumns = availableColumns;
		else {
			selectedColumns = new String[selectedIndexes.size()];
			for (int i = 0; i < selectedIndexes.size(); i++)
				selectedColumns[i] = availableColumns[selectedIndexes.get(i)];
		}

		return selectedColumns;
	}

	/**
	 * Interactively prompts the user to input a value for each specified column.
	 *
	 * @param message Prompt to display to the user.
	 * @param columns Array of columns that need values assigned.
	 * @return An array of the input values matching the order of the columns.
	 */
	private static String[] selectValues(String message, String[] columns) {
		String[] values = new String[columns.length];
		do {
			System.out.println(message);
			// Collect user input for each column individually
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

	/**
	 * Renders a formatted ASCII table of the data and prompts the user to select a
	 * row. Dynamically calculates column widths for clean visual alignment.
	 *
	 * @param message      Prompt to display to the user.
	 * @param columns      Array of column headers.
	 * @param valuesMatrix The actual row data to render.
	 * @return The integer index of the selected row.
	 */
	private static int selectRow(String message, String[] columns, String[][] valuesMatrix) {
		int row = -1;
		boolean valid = false;

		do {
			System.out.println(message);

			// Dynamically calculate the maximum width for each column to align the table
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

			// Build table header and separator lines
			StringBuilder header = new StringBuilder("    |");
			StringBuilder separator = new StringBuilder("    |");
			for (int i = 0; i < columns.length; i++) {
				header.append(String.format(" %-" + columnWidths[i] + "s |", columns[i]));
				separator.append(" ").append("-".repeat(columnWidths[i])).append(" |");
			}

			System.out.println(header.toString());
			System.out.println(separator.toString());

			StringBuilder[] lines = new StringBuilder[valuesMatrix.length];

			// Render table rows line by line
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

			// Await row selection
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

	/**
	 * Prompts the user for a simple yes/no (y/n) validation.
	 *
	 * @param message Prompt asking for confirmation.
	 * @return True if the user inputs 'y', false if 'n'.
	 */
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

	/**
	 * Centralized input handler that reads lines from the standard input. Safely
	 * closes the application if the input stream is terminated.
	 *
	 * @return The raw string inputed by the user.
	 */
	private static String getInput() {

		if (!scanner.hasNextLine()) {
			System.out.println("\nTerminal stream closed. Exiting safely...");
			System.exit(0);
		}

		return scanner.nextLine();
	}

}