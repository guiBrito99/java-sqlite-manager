package ab.db;

import java.util.ArrayList;

public class Table {
	private String name;
	private ArrayList<String> columns;
	private ArrayList<String[]> rows;

	public Table(String name, ArrayList<String> columns, ArrayList<String[]> rows) {
		this.name = name;
		this.columns = columns;
		this.rows = rows;
	}

	public Table(String name, ArrayList<String> columns) {
		this(name, columns, new ArrayList<>());
	}

	public String getName() {
		return this.name;
	}

	public ArrayList<String> getColumns() {
		return this.columns;
	}

	public ArrayList<String[]> getRows() {
		return this.rows;
	}

	public void setValues(ArrayList<String[]> values) {
		this.rows = values;
	}
}
