package dev.andrepontde.retailmanager;
import java.sql.Connection;
import java.util.*;

//Start server ->
//net start postgresql-x64-17
//or go to services and start the PostgreSQL service if set to manual

public class Main {
    public static void main(String[] args) {
        DbConnection dbConn = new DbConnection();
        // Get the password from the environment variable 'postpsw'
        String password = System.getenv("postpsw");
        if (password == null) {
            System.err.println("Environment variable 'postpsw' not set!");
            return;
        }
        String tableName = "productTrial2";
        Connection conn = dbConn.connectToDB("retailassistant", "postgres", password);

        // Create a map with your column definitions
        Map<String, String> tcolumns = new HashMap<>();
        tcolumns.put("name", "VARCHAR(100)");
        tcolumns.put("type", "VARCHAR(50)");
        tcolumns.put("price", "VARCHAR(100)");
        tcolumns.put("description", "TEXT");

        dbConn.createTable(conn, tableName, tcolumns);
        String[] icolumns = tcolumns.keySet().toArray(new String[0]);
        Object[] values = new Object[icolumns.length];
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < icolumns.length; i++) {
            System.out.print("Enter value for " + icolumns[i] + ": ");
            values[i] = scanner.nextLine();
        }
        scanner.close();

        Map<String, Object> dataMap = DbConnection.mapColumnsToValues(icolumns, values);
        dbConn.insert_data(conn, tableName, dataMap);

        dbConn.readData(conn, tableName);
    }
}