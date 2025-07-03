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

    public void createTable(Connection conn, String table_name){
        Statement stmt;
        try{
            String query = "CREATE TABLE IF NOT EXISTS " + table_name + " (id SERIAL PRIMARY KEY, name VARCHAR(100), type VARCHAR(50))";
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Table " + table_name + " created successfully.");
        }catch(Exception e){
            System.out.println("Error creating table " + table_name);
            e.printStackTrace();
        }
    }

    //  Inserts a row into any table with dynamic columns and values using a Map.
    //  Uses PreparedStatement for safety and flexibility.
    //  the parameter data is A map of column names to values

    public void insert_data(Connection conn, String tableName, Map<String, Object> data) {
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
            System.out.println("Row inserted into table " + tableName + " successfully.");
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
}
