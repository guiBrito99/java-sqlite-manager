package ab.db;

import java.util.ArrayList;

/**
 * The Table class serves as the fundamental Data Model (DTO) for the
 * application. It encapsulates the schema (name and columns) and the data
 * (rows) for a single database table in an easy-to-manage memory structure.
 */
public class Table {
	// The table name identifier
	private String name;
	// The list of column header names
	private ArrayList<String> columns;
	// The list of rows, where each row is an array of strings
	private ArrayList<String[]> rows;

	/**
	 * Full constructor for initializing a table with existing data.
	 * 
	 * @param name    The name of the table.
	 * @param columns The column headers.
	 * @param rows    The data rows.
	 */
	public Table(String name, ArrayList<String> columns, ArrayList<String[]> rows) {
		this.name = name;
		this.columns = columns;
		this.rows = rows;
	}

	/**
	 * Convenience constructor for creating a new, empty table structure.
	 * 
	 * @param name    The name of the table.
	 * @param columns The column headers.
	 */
	public Table(String name, ArrayList<String> columns) {
		this(name, columns, new ArrayList<>());
	}

	/**
	 * Returns the table name.
	 * 
	 * @return String representing the table name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the list of column headers.
	 * 
	 * @return ArrayList of column names.
	 */
	public ArrayList<String> getColumns() {
		return this.columns;
	}

	/**
	 * Returns the list of data rows.
	 * 
	 * @return ArrayList where each element is a String array representing a row.
	 */
	public ArrayList<String[]> getRows() {
		return this.rows;
	}

	/**
	 * Sets the data for the table rows.
	 * 
	 * @param values An ArrayList of row data arrays.
	 */
	public void setValues(ArrayList<String[]> values) {
		this.rows = values;
	}
}