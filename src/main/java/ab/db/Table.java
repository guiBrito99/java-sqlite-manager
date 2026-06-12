package ab.db;

import java.util.ArrayList;

public class Table {
	private String name;
	private String[] columns;
	private ArrayList<String[]> rows;

	public Table(String name, String[] columns, ArrayList<String[]> rows) {
		this.name = name;
		this.columns = columns;
		this.rows = rows;
	}
	
	public Table(String name, String[] columns) {
		this(name, columns, new ArrayList<>());
	}

	public String getName() {
		return this.name;
	}

	public String[] getColumns() {
		return this.columns;
	}

	public ArrayList<String[]> getRows() {
		return this.rows;
	}

	public void setValues(ArrayList<String[]> values) {
		this.rows = values;
	}
}
