package code.config;

import java.sql.*;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "metropos";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "8Id9yPpDioV6KYb";

    private static Connection connection = null;

    public static void createDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            statement.executeUpdate(createDatabaseSQL);
            System.out.println("Database 'metropos' verified/created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating/verifying database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/metropos", DB_USER, DB_PASSWORD);
        }
        return connection;
    }
}
