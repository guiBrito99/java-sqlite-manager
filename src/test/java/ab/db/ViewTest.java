package ab.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ViewTest {

	// Save the real keyboard input so we can restore it later
	private final InputStream originalSystemIn = System.in;
	private StringBuilder testString = new StringBuilder();

	private void addTest(String input) throws SQLException {
		this.testString.append(input);
	}

	private void execute() throws Exception {
		System.out.println(this.testString);
		ByteArrayInputStream fakeKeyboard = new ByteArrayInputStream(this.testString.toString().getBytes());
		System.setIn(fakeKeyboard);
		View.scanner = new Scanner(System.in);

		View.main();
	}

	@AfterEach
	public void tearDown() {
		// This runs AFTER every test to clean up.
		// We put the real keyboard back so we don't break the rest of the application!
		System.setIn(originalSystemIn);
		View.scanner = new Scanner(System.in);
	}

	@Test
	public void fullTest() throws Exception {
		//Testing the menu loop logic
		this.testMenuInvalidInput();
		
		// Tests done while the database is empty
		this.testMenuPrint();
		this.testMenuDeleteTable(null, null);
		this.testMenuInsertRow(null, null, null, null);
		this.testMenuDeleteRow(null, null, null);
		this.testMenuUpdateRow(null, null, null, null, null);

		// Now properly creating a table and testing the program
		ArrayList<String> tablesNames = new ArrayList<>();
		ArrayList<String[]> tablesColumns = new ArrayList<>();
		ArrayList<ArrayList<String[]>> tableValidInputs = new ArrayList<>();
		ArrayList<String[]> testInputs = new ArrayList<>();

		testInputs.add(new String[] { "Testing1", "Testing2" });
		testInputs.add(new String[] { "Updated1", "Updated2" });

		tablesNames.add("Testing");
		tablesColumns.add(new String[] { "Testing1", "Testing2" });
		tableValidInputs.add(new ArrayList<>());
		tableValidInputs.get(0).add(testInputs.get(0));
		
		this.testMenuCreateTable(tablesNames.get(0), tablesColumns.get(0));
		this.testMenuPrint();
		this.testMenuDeleteTable(tablesNames, tablesNames.get(0));
		this.testMenuPrint();
		
		this.testMenuCreateTable(tablesNames.get(0), tablesColumns.get(0));
		this.testMenuPrint();
		
		this.testMenuInsertRow(tablesNames, tablesNames.get(0), tablesColumns.get(0), tableValidInputs.get(0).get(0));
		this.testMenuPrint();
		this.testMenuDeleteRow(tablesNames, tablesNames.get(0), tableValidInputs.get(0));
		this.testMenuPrint();

		
		this.testMenuInsertRow(tablesNames, tablesNames.get(0), tablesColumns.get(0), tableValidInputs.get(0).get(0));
		this.testMenuPrint();
		this.testMenuUpdateRow(tablesNames, tablesNames.get(0), tablesColumns.get(0), tableValidInputs.get(0),
				testInputs.getLast());
		this.testMenuPrint();
		
		this.testMenuDeleteTable(tablesNames, tablesNames.get(0));
		this.testMenuPrint();
		
		this.testMenuExit();

		
		this.execute();
	}

	private void testMenuPrint() throws Exception {
		this.addTest("1\n");
	}

	private void testMenuCreateTable(String tableName, String[] tableColumns) throws Exception {
		this.addTest("2\n");
		if(tableName != null && tableColumns != null)
			this.testCreateTable(tableName, tableColumns);
	}

	private void testMenuDeleteTable(ArrayList<String> tablesNames, String tableName) throws Exception {
		this.addTest("3\n");
		if(tablesNames != null && tableName != null)
			this.testDeleteTable(tablesNames, tableName);
	}

	private void testMenuInsertRow(ArrayList<String> tablesNames, String tableName, String[] tableColumns,
			String[] validInputs) throws Exception {
		this.addTest("4\n");
		if(tablesNames != null && tableName != null && tableColumns != null && validInputs != null)
			this.testInsertRow(tablesNames, tableName, tableColumns, validInputs);

	}

	private void testMenuDeleteRow(ArrayList<String> tablesNames, String tableName,
			ArrayList<String[]> tableValidInputs) throws Exception {
		this.addTest("5\n");
		if(tablesNames != null && tableName != null && tableValidInputs != null)
			this.testDeleteRow(tablesNames, tableName, tableValidInputs);
	}

	private void testMenuUpdateRow(ArrayList<String> tablesNames, String tableName, String[] tableColumns,
			ArrayList<String[]> tableRows, String[] validInputs) throws Exception {
		this.addTest("6\n");
		if(tablesNames != null && tableName != null && tableColumns != null && tableRows != null && validInputs != null)
			this.testUpdateRow(tablesNames, tableName, tableRows, tableColumns, validInputs);
	}

	private void testMenuExit() throws Exception {
		this.addTest("0\n");
	}

	private void testCreateTable(String tableName, String[] tableColumns) throws Exception {

		this.testNegatedInput();

		this.testInvalidStrings();

		this.addTest(tableName + "\ny\n");

		StringBuilder testColumns = new StringBuilder();

		for (String column : tableColumns)
			testColumns.append(column).append(",");

		testColumns.deleteCharAt(testColumns.length() - 1);

		this.addTest(testColumns.toString());

		this.addTest("\ny\n");
	}

	private void testDeleteTable(ArrayList<String> tablesNames, String tableName) throws Exception {
		this.testSelectTable(tablesNames.indexOf(tableName));
	}

	private void testInsertRow(ArrayList<String> tablesNames, String tableName, String[] tableColumns,
			String[] validInputs) throws Exception {
		this.testSelectTable(tablesNames.indexOf(tableName));
		this.testSelectColumns(tableColumns);
		this.testSelectValues(tableColumns, validInputs);
	}

	private void testDeleteRow(ArrayList<String> tablesNames, String tableName, ArrayList<String[]> tableRows)
			throws Exception {
		this.testSelectTable(tablesNames.indexOf(tableName));
		this.testSelectRow(tableRows);
	}

	private void testUpdateRow(ArrayList<String> tablesNames, String tableName, ArrayList<String[]> tableRows,
			String[] tableColumns, String[] validInputs) throws Exception {
		this.testSelectTable(tablesNames.indexOf(tableName));
		this.testSelectRow(tableRows);
		this.testSelectColumns(tableColumns);
		this.testSelectValues(tableColumns, validInputs);
	}

	private void testSelectTable(int correctInput) throws Exception {
		// Testing the invalid input
		this.testInvalidNumberSelection(correctInput);

		// Selecting the table
		this.addTest(correctInput + "\n");

		// Testing confirmation
		this.testInvalidCharactersConfirmation();

		// Confirming the table to deletion
		this.addTest("y\n");
	}

	private void testSelectColumns(String[] tableColumns) throws Exception {
		// Testing all but the correct value
		this.testInvalidNumberSelection((tableColumns.length) - 1);

		// Selecting column to negate afterwards
		this.addTest("0\nn\n");

		// Selecting the right option
		this.addTest(tableColumns.length + "\n"); 

		// Testing confirmation
		this.testInvalidCharactersConfirmation();

		// Confirming column selection
		this.addTest("y\n");
	}

	private void testSelectValues(String[] tableColumns, String[] validInputs) throws Exception {
		// Selecting values to negate
		int columnsSize = tableColumns.length;
		for (int i = 0; i < columnsSize; i++)
			this.addTest("Invalid " + i + "\n");
		this.addTest("n\n");

		// Adding valid inputs
		for (int i = 0; i < columnsSize; i++)
			this.addTest(validInputs[i] + "\n");

		this.testInvalidCharactersConfirmation();

		this.addTest("y\n");
	}

	private void testSelectRow(ArrayList<String[]> rows) throws Exception {
		int validOption = rows.size() - 1;

		// Testing invalid options
		this.testInvalidNumberSelection(validOption);

		// Selecting the valid option
		this.addTest(validOption + "\n");

		// Testing confirmation
		this.testInvalidCharactersConfirmation();

		this.addTest("y\n");
	}

	private void testNegatedInput() throws Exception {
		this.addTest("NegatedInput\n");
		this.testInvalidCharactersConfirmation();
		this.addTest("n\n");
	}

	private void testInvalidStrings() throws Exception {
		this.addTest("\n");
		this.addTest(",,,\n");
		this.addTest("	\n");
		this.addTest(",InvalidString\n");
		this.addTest(",InvalidString,\n");
		this.addTest(",,InvalidString\n");
		this.addTest(",InvalidString,,\n");
		this.addTest("InvalidString,,\n");
	}

	private void testInvalidCharactersConfirmation() throws Exception {
		StringBuilder simulatedInput = new StringBuilder();

		for (int i = 32; i < 127; i++)
			if (i != ((char) 'y')  && i != ((char) 'n') && i != ((char) 'Y') && i != ((char) 'N'))
				simulatedInput.append((char) i).append("\n");

		this.addTest(simulatedInput.toString());
	}

	private void testInvalidNumberSelection(int columnsSize) throws Exception {
		for (int i = -10; i <= 10; i++)
			if (!(i >= 0 && i <= (columnsSize+1)))
				this.addTest(i + "\n");
	}

	private void testMenuInvalidInput() throws Exception {
		StringBuilder simulatedInput = new StringBuilder();

		// Testing chars
		for (int i = 32; i < 127; i++)
			simulatedInput.append((char) i).append("\n");

		// Testing numbers
		for (int i = 7; i < 100; i++)
			simulatedInput.append(i).append("\n");
	}
}