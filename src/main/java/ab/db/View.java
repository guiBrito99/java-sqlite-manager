package ab.db;

import java.sql.SQLException;
import java.util.ArrayList;
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
				View.scanner.next();
				command = -1;
			}

			switch (command) {
			case 1:
				databaseController.print();
				break;
			case 2:
				databaseController.createTable();
				break;
			case 3:
				databaseController.deleteTable();
				break;
			case 4:
				databaseController.insertRow();
				break;
			case 5:
				databaseController.deleteRow();
				break;
			case 6:
				databaseController.updateRow();
				break;
			default:
			}

		} while (command != 0);

		System.out.println("Exiting...");
	}
}
