# java-sqlite-manager

A modular Java application for SQLite database management. Designed to operate in two modes: an **Interactive Menu** (for manual data entry) and an **Automated CLI** (for script integration).

## CLI Argument Syntax

When running the application via command line, commands must follow the specified order.

| Command | Syntax | Description |  
| `create` | `create [table] [cols]` | Creates a table. |  
| `insert` | `insert [table] [cols] [vals]` | Inserts a row. |  
| `print` | `print` | Displays all tables and their contents. |  
| `update` | `update [table] [idx] [cols] [vals]` | Updates a row by index. |  
| `delete` | `delete [table] [row_idx]` | Removes a row by index. |  
| `drop` | `drop [table]` | Deletes a table. |  

**CRITICAL FORMATTING RULE:**
When providing multiple items (columns or values), you must use a single string separated by commas, **with no spaces**.

*   **Incorrect:** `id, name, role`
*   **Correct:** `id,name,role`

## Usage Examples

### 0. Interactive Menu
Starting the application if no parameters initiates the Interactive Menu  
`java -jar sqlite-manager-complete.jar`

### 1. Print Database
Prints the current contents of the database  
`java -jar sqlite-manager-complete.jar print`

### 2. Create a Table
Creates the table with name foo and columns foo1, foo2 and foo3  
`java -jar sqlite-manager-complete.jar create foo foo1,foo2,foo3`

### 4. Delete a table
Deletes the table with the name foo  
`java -jar sqlite-manager-complete.jar drop foo`

### 5. Insert Data
Inserts a data roll in the table with name foo. The values for the columns foo1, foo2 and foo3  
are bar1, bar2 and bar3  
`java -jar sqlite-manager-complete.jar insert foo foo1,foo2,foo3 bar1,bar2,bar3`

### 6. Delete Data
Deletes the row of index 0 of the table named foo  
`java -jar sqlite-manager-complete.jar delete foo 0`

### 7. Update Data
Updates the row of index 0 of the table named foo. The values of the columns foo1 and foo2  
will be updated to bar1 and bar2  
`java -jar sqlite-manager-complete.jar update foo 0 foo1,foo2 bar1,bar2`