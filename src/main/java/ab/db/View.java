package ab.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

public class View {
	static Scanner scanner = new Scanner(System.in);

	public static void main(String... args) throws SQLException {
		DatabaseController databaseController = new DatabaseController();
		if (args.length == 0) {
			databaseMenu(databaseController);
		} else {
			ArrayList<String> arguments = new ArrayList<>(List.of(args));

			for (String argument : arguments) {
				switch (argument.toLowerCase()) {
				case "--print":
					databaseController.print();
					break;
				default:
				}
			}
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
				command = View.scanner.nextInt();
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				command = -1;
			}
			scanner.nextLine();

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
		do {
			System.out.println("Table name:");

			tableName = scanner.nextLine();

			System.out.println("Table name is " + tableName);

			/*
			 * To avoid confusion(table name empty, returning directly to the beginning of
			 * the loop), only prints the confirm prompt if tableName is valid
			 */
		} while (tableName.isBlank() || !inputValidation("Confirm y/n"));

		// Table columns creator
		String command;
		do {
			System.out.println("Table columns, separated by comma:");

			command = scanner.nextLine();

			if (command != "") {
				columns = command.split(",");

				System.out.println("Columns: " + Arrays.toString(columns));
			}

		} while (command == "" || !inputValidation("Confirm y/n"));

		databaseController.createTable(tableName, columns);

	}

	private static void deleteTable(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		String tableName = selectTable("Select table for deletion:", tablesNames);
		databaseController.deleteTable(tableName);
	}

	private static void insertRow(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		String tableName = selectTable("Select table to insert", tablesNames);

		String[] tableColumns = databaseController.getTableColumns(tableName);
		String[] columns = selectColumns("Select the column(s) for the insertion, separating by coma", tableColumns);

		String[] values = selectValues("Select the value(s) for each column(s)", columns);

		databaseController.insertRow(tableName, columns, values);
	}

	private static void deleteRow(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		String tableName = selectTable("Select table to remove one row", tablesNames);

		String[] columns = databaseController.getTableColumns(tableName);
		String[][] valuesMatrix = databaseController.getValuesMatrix(tableName);
		int rowIndex = selectRow("Select the row to delete", columns, valuesMatrix);

		databaseController.deleteRow(tableName, rowIndex);
	}
	
	private static void updateRow(DatabaseController databaseController) {
		String[] tablesNames = databaseController.getTablesNames();
		String tableName = selectTable("Select table to update", tablesNames);
		
		String[] tableColumns = databaseController.getTableColumns(tableName);
		String[][] valuesMatrix = databaseController.getValuesMatrix(tableName);
		int rowIndex = selectRow("Select the row for the update", tableColumns, valuesMatrix);
		String[] oldValues = databaseController.getRowValues(tableName, rowIndex);
		
		String[] columns = selectColumns("Select column(s) to update, separating by coma", tableColumns);
		String[] values = selectValues("Select value(s) for each column(s)", columns);

		databaseController.updateRow(tableName, columns, values, tableColumns, oldValues, rowIndex);
	}

	private static String selectTable(String message, String[] tablesNames) {

		int selection = -1;
		boolean valid = false;

		do {
			System.out.println(message);

			for (int i = 0; i < tablesNames.length; i++)
				System.out.println(i + " - " + tablesNames[i]);

			try {
				selection = scanner.nextInt();
				scanner.nextLine();
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

			selection = scanner.nextLine().split(",");

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
				values[i] = scanner.nextLine();
			}

			System.out.println("Selected values:");
			for (int i = 0; i < columns.length; i++)
				System.out.println(columns[i] + ": " + values[i]);

		} while (!inputValidation("Confirm y/n"));

		return values;
	}

	private static int selectRow(String message, String[] columns ,String[][] valuesMatrix) {
		int row = -1;
		boolean valid = false;

		do {
			System.out.println(message);
			String rowString = null;
			for (int i = 0; i < valuesMatrix.length; i++) {
				rowString = i + " - ";
				for (int j = 0; j < columns.length; j++)
					rowString += "(" + columns[j] + ")" + valuesMatrix[i][j] + " ";

				System.out.println(rowString.substring(0, rowString.length() - 1));
			}

			try {
				row = scanner.nextInt();
				scanner.nextLine();
				valid = row >= 0 && row < valuesMatrix.length;
			} catch (Exception e) {
				System.out.println("Type in valid integer");
				valid = false;
			}

			if (valid) {
				System.out.println("Selected row:");

				rowString = row + " - ";
				for (int j = 0; j < columns.length; j++)
					rowString += "(" + columns[j] + ")" + valuesMatrix[row][j] + " ";

				System.out.println(rowString.substring(0, rowString.length() - 1));
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
			command = View.scanner.nextLine().toLowerCase();

			if (command == "")
				System.out.println("Type in valid character");
			else
				firstChar = command.charAt(0);

		} while (command == "" || (firstChar != 'y' && firstChar != 'n'));

		confirm = firstChar == 'y';

		return confirm;
	}
}
