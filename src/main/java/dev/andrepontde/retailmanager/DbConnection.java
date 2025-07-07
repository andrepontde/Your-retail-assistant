package dev.andrepontde.retailmanager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.stream.Collectors;

public class DbConnection {
    public Connection connectToDB(String dbname, String username, String password) {
        // This method should return a connection to the database.
        // For now, we will return null to avoid compilation errors.
        Connection conn = null;

        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+ dbname, username, password);
            if (conn != null) {
                System.out.println("Connected to the database successfully!");
            } else {
                System.out.println("Failed to make connection!");
            }

        }catch(Exception e){
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Creates a table with dynamic column definitions.
     * @param conn The database connection
     * @param table_name The name of the table to create
     * @param columns A map where keys are column names and values are column definitions (e.g., "VARCHAR(100)", "INTEGER", "TEXT")
     */
    public void createTable(Connection conn, String table_name, Map<String, String> columns){
        Statement stmt = null;
        try{
            // Start building the CREATE TABLE query
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("CREATE TABLE IF NOT EXISTS ").append(table_name).append(" (");
            
            // Add id as VARCHAR primary key for hash/string IDs
            queryBuilder.append("id VARCHAR(32) PRIMARY KEY");
            
            // Add user-defined columns
            for (Map.Entry<String, String> column : columns.entrySet()) {
                queryBuilder.append(", ").append(column.getKey()).append(" ").append(column.getValue());
            }
            
            queryBuilder.append(")");
            String query = queryBuilder.toString();
            
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Table " + table_name + " created successfully with columns: " + columns.keySet());
        }catch(Exception e){
            System.out.println("Error creating table " + table_name);
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    //  Inserts a row into any table with dynamic columns and values using a Map.
    //  Uses PreparedStatement for safety and flexibility.
    //  the parameter data is A map of column names to values

    public void insert_data(Connection conn, String tableName, Map<String, Object> data) {
        // If 'id' is not present, generate it using generateSectionedId
        if (!data.containsKey("id")) {
            String generatedId = generateSectionedId(tableName, data);
            data.put("id", generatedId);
        }
        // Build a comma-separated list of column names from the map keys
        String columns = String.join(", ", data.keySet());
        // Build a comma-separated list of '?' placeholders, one for each column
        String placeholders = data.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));
        // Build the SQL statement
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 1;
            // Set each value in the PreparedStatement
            for (Object value : data.values()) {
                pstmt.setObject(i++, value); // Set the value for each placeholder
            }
            pstmt.executeUpdate(); // Execute the insert
            System.out.println("Row inserted into table " + tableName + " successfully. ID: " + data.get("id"));
        } catch (SQLException e) {
            System.out.println("Error inserting row into table " + tableName);
            e.printStackTrace();
        }
    }

    // Utility method to create a Map of column names to values for database insertion.
    //  @return Map mapping each column name to its value
    public static Map<String, Object> mapColumnsToValues(String[] columns, Object[] values) {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values must have the same length");
        }
        Map<String, Object> data = new java.util.HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            data.put(columns[i], values[i]);
        }
        return data;
    }

    /**
     * Reads all rows from the specified table and prints them to the console.
     * This method uses a simple SELECT * query and prints each row as a map of column names to values.
     * 
     * @param conn      The database connection to use.
     * @param tableName The name of the table to read from.
     */
    public void readData(Connection conn, String tableName) {
        // Statement and ResultSet are used for executing and processing the SQL query
        Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            // Build the SQL query to select all columns from the table
            String query = "SELECT * FROM " + tableName;
            // Create a statement object to execute the query
            stmt = conn.createStatement();
            // Execute the query and get the result set
            rs = stmt.executeQuery(query);

            // Get metadata about the result set, such as column names and count
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterate through each row in the result set
            while (rs.next()) {
                // For each row, create a map to store column-value pairs
                java.util.Map<String, Object> row = new java.util.HashMap<>();
                // Loop through all columns in the row
                for (int i = 1; i <= columnCount; i++) {
                    // Get the column name and value for the current column
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    // Put the column-value pair into the map
                    row.put(columnName, value);
                }
                // Print the row map to the console
                System.out.println(row);
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions that occur
            System.out.println("Error reading data from table " + tableName);
            e.printStackTrace();
        } finally {
            // Always close the ResultSet and Statement to free resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Generates a deterministic, human-friendly code for a row based on up to 3 key attributes.
     * Each attribute is hashed separately and a few digits are taken from each hash to form a segment.
     * This ensures similar products have similar codes, and the code is fully automated.
     *
     * @param tableName The table name (for context, not used in code)
     * @param data The map of column names to values (should be insertion order for best results)
     * @return The generated code string (e.g., 123/456/789)
     */
    public static String generateSectionedId(String tableName, Map<String, Object> data) {
        // Choose up to 3 key attributes (e.g., name, type, color) if present, else use whatever is available
        String[] keys = data.keySet().toArray(new String[0]);
        String[] segments = new String[3];
        for (int i = 0; i < 3; i++) {
            if (i < keys.length) {
                Object value = data.get(keys[i]);
                segments[i] = hashSegment(value == null ? "" : value.toString());
            } else {
                segments[i] = "000"; // Default if not enough attributes
            }
        }
        return segments[0] + "/" + segments[1] + "/" + segments[2];
    }

    // Helper: Hash a string and return the first 3 digits of its MD5 hash as a segment
    private static String hashSegment(String value) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(value.getBytes());
            int v = ((hash[0] & 0xFF) << 8 | (hash[1] & 0xFF)); // Use first 2 bytes for more variety
            v = Math.abs(v) % 1000; // Keep it 3 digits
            return String.format("%03d", v);
        } catch (Exception e) {
            return "000";
        }
    }
}
