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
        String tableName = "productTrial";
        Connection conn = dbConn.connectToDB("retailassistant", "postgres", password);
        dbConn.createTable(conn, tableName);
        String[] columns = {"name", "type"};
        Object[] values = {"Test Product", "Electronics"};

        Map<String, Object> dataMap = DbConnection.mapColumnsToValues(columns, values);

        dbConn.insert_data(conn, tableName, dataMap);
    }
}