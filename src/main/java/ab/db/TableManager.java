package ab.db;

import java.util.ArrayList;
import java.util.List;

public class TableManager {
	private ArrayList<Table> tables = new ArrayList<>();

	public TableManager(ArrayList<Table> tables) {
		this.tables = tables;
	}

	public void createTable(String tableName, String[] columns) {
		ArrayList<String> columnsArrayList = new ArrayList<>(List.of(columns));

		Table newTable = new Table(tableName, columnsArrayList);

		// Adding table to internal data structure
		this.tables.add(newTable);
	}

	public void deleteTable(String tableName) {
		Table table = this.findTable(tableName);
		if (table != null)
			this.tables.remove(table);
		else
			System.out.println(tableName + " do not exists");
	}

	public void insertRow(String tableName, String[] columns, String[] values) {
		Table table = this.findTable(tableName);

		if (table != null) {
			// Updating internal table variable with values
			ArrayList<String> tableColumns = table.getColumns();
			ArrayList<String[]> tableRows = table.getRows();
			tableRows.add(new String[tableColumns.size()]);
			for (int i = 0; i < columns.length; i++) {
				int internalIndex = tableColumns.indexOf(columns[i]);
				tableRows.getLast()[internalIndex] = values[i];
			}
		} else
			System.out.println(tableName + " do not exists");
	}

	public void updateRow(String tableName, int rowIndex, String[] columns, String[] values) {
		Table table = this.findTable(tableName);

		if (table != null) {
			ArrayList<String> tableColumns = table.getColumns();
			String[] tableValues = table.getRows().get(rowIndex);
			for (int i = 0; i < columns.length; i++) {
				int internalIndex = tableColumns.indexOf(columns[i]);
				tableValues[internalIndex] = values[i];
			}
		} else
			System.out.println(tableName + " do not exists");
	}

	public void deleteRow(String tableName, int rowIndex) {
		Table table = this.findTable(tableName);

		if (table != null)
			table.getRows().remove(rowIndex);
		else
			System.out.println(tableName + " do not exists");
	}

	public void printTables() {
		if (!this.tables.isEmpty())
			for (Table table : this.tables) {
				System.out.println("### " + table.getName() + "\n");

				ArrayList<String> columns = table.getColumns();
				ArrayList<String[]> rows = table.getRows();

				// Calculate column widths (minimum = column header length)
				int[] columnWidths = new int[columns.size()];
				for (int i = 0; i < columns.size(); i++) {
					columnWidths[i] = columns.get(i).length();
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
				for (int i = 0; i < columns.size(); i++) {
					header += String.format(" %-" + columnWidths[i] + "s |", columns.get(i));
					separator += " " + "-".repeat(columnWidths[i]) + " |";
				}
				System.out.println(header);
				System.out.println(separator);

				// Build data rows
				for (String[] row : rows) {
					String line = "|";
					for (int i = 0; i < columns.size(); i++) {
						String cell = (i < row.length && row[i] != null) ? row[i] : "";
						line += String.format(" %-" + columnWidths[i] + "s |", cell);
					}
					System.out.println(line);
				}

				System.out.println();
			}
		else
			System.out.println("No table to print");
	}

	public String[] getTablesNames() {
		String[] tableNames = null;

		if (!this.tables.isEmpty()) {
			tableNames = new String[this.tables.size()];
			for (int i = 0; i < this.tables.size(); i++)
				tableNames[i] = this.tables.get(i).getName();
		} else
			System.out.println("No table in the database");

		return tableNames;
	}

	public String[] getTableColumns(String tableName) {
		String[] tableColumns = null;

		Table table = this.findTable(tableName);

		if (table != null)
			tableColumns = table.getColumns().toArray(new String[table.getColumns().size()]);

		return tableColumns;
	}

	public String[][] getValuesMatrix(String tableName) {
		Table table = this.findTable(tableName);
		String[][] valuesMatrix = null;

		if (table != null) {
			int rowsSize = table.getRows().size();
			int columnsSize = table.getColumns().size();

			valuesMatrix = new String[rowsSize][columnsSize];

			for (int i = 0; i < rowsSize; i++) {
				String[] row = table.getRows().get(i);
				for (int j = 0; j < columnsSize; j++) {
					valuesMatrix[i][j] = row[j];
				}
			}
		}

		return valuesMatrix;
	}

	public String[] getRowValues(String tableName, int rowIndex) {
		String[] rowValues = null;
		Table table = this.findTable(tableName);

		if (table != null)
			rowValues = table.getRows().get(rowIndex);

		return rowValues;
	}

	private Table findTable(String tableName) {
		Table table = null;
		if (!this.tables.isEmpty()) {
			int index = -1;

			do {
				index++;
			} while (index < this.tables.size() && !this.tables.get(index).getName().equals(tableName));

			table = index < this.tables.size() ? this.tables.get(index) : null;
		}

		return table;
	}

}
