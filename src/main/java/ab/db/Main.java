package ab.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
	static Scanner scanner = new Scanner(System.in);

	public static void main(String... args) throws SQLException {

		Connection connection = ConnectionManager.connect();

		SQLManager sqlManager = new SQLManager(connection);

		if (args.length == 0) {
			int command;

			do {
				System.out.println("Select the command:");
				System.out.println("1 - Print");
				System.out.println("2 - Add table");
				System.out.println("3 - Remove table");
				System.out.println("4 - Insert row");
				System.out.println("5 - Remove row");
				System.out.println("6 - Update row");
				System.out.println("0 - Exit");

				try {
					command = Main.scanner.nextInt();
				} catch (Exception e) {
					System.out.println("Type in valid integer");
					Main.scanner.next();
					command = -1;
				}

				switch (command) {
				case 1:
					sqlManager.print();
					break;
				case 2:
					sqlManager.createTable();
					break;
				case 3:
					sqlManager.deleteTable();
					break;
				case 4:
					sqlManager.insert();
					break;
				case 5:
					sqlManager.delete();
					break;
				case 6:
					sqlManager.update();
					break;
				default:
				}

			} while (command != 0);

		}

		System.out.println("Exiting...");
	}
}
