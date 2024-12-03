package code.functions;

import code.config.DBConnection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SuperAdminSetup {
    public static void addAdminInterferance() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Super Admin Setup ===");
        System.out.print("Enter Super Admin Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Super Admin Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Super Admin Password: ");
        String password = scanner.nextLine();

        System.out.print("Enter Salary: ");
        double salary = scanner.nextDouble();

        addSuperAdmin(name, email, password, salary);
    }

    public static void addSuperAdmin(String name, String email, String password, double salary) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO employees (name, emp_no, email, password, branch_code, salary, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, "SA-001"); // Default Employee Number for Super Admin
            statement.setString(3, email);
            statement.setString(4, password);
            statement.setString(5, "GLOBAL"); // Branch Code for Super Admin
            statement.setDouble(6, salary);
            statement.setString(7, "SuperAdmin");

            statement.executeUpdate();
            System.out.println("Super Admin added successfully!");
        } catch (SQLException ex) {
            System.err.println("Error adding Super Admin: " + ex.getMessage());
        }
    }
    public static void initializeSystem() {
        createDatabaseTables();
        createNecessaryFiles();
    }

    private static void createDatabaseTables() {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {

            String dropTableSQL = "DROP TABLE IF EXISTS products";

            String createProductsTable = """
                    CREATE TABLE IF NOT EXISTS products (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        category VARCHAR(255) NOT NULL,
                        original_price DECIMAL(10, 2) NOT NULL,
                        sale_price DECIMAL(10, 2) NOT NULL,
                        price_per_unit DECIMAL(10, 2) NOT NULL,
                        price_per_carton DECIMAL(10, 2) NOT NULL
                    );
                    """;

            String createVendorsTable = """
                    CREATE TABLE IF NOT EXISTS vendors (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          address VARCHAR(255) NOT NULL,
                          phone VARCHAR(15) NOT NULL
                      );
                   
                    """;



            String createEmployeesTableSQL = """
                    CREATE TABLE IF NOT EXISTS employees (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        emp_no VARCHAR(50) NOT NULL UNIQUE,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        branch_code VARCHAR(50) NOT NULL,
                        salary DECIMAL(10, 2) NOT NULL,
                        role VARCHAR(100) NOT NULL
                    );
                """;
            String sql = "CREATE TABLE IF NOT EXISTS branches (" +
                    "branch_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "branch_code VARCHAR(50) NOT NULL UNIQUE, " +
                    "name VARCHAR(100), " +
                    "city VARCHAR(50), " +
                    "address VARCHAR(255), " +
                    "phone VARCHAR(15), " +
                    "active BOOLEAN DEFAULT TRUE)";

//            statement.executeUpdate(dropTableSQL);
            statement.executeUpdate(createProductsTable);
            statement.executeUpdate(sql);
            statement.executeUpdate(createEmployeesTableSQL);
            statement.executeUpdate(createVendorsTable);

            System.out.println("Database tables verified/created successfully.");
        } catch (Exception e) {
            System.err.println("Error while creating database tables: " + e.getMessage());
        }
    }

    private static void createNecessaryFiles() {
        // Create temp.txt for offline status
        File tempFile = new File("temp.txt");
        try {
            if (!tempFile.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                    writer.write("false"); // Default to online mode
                }
                System.out.println("temp.txt created successfully.");
            } else {
                System.out.println("temp.txt already exists.");
            }
        } catch (IOException e) {
            System.err.println("Error creating temp.txt: " + e.getMessage());
        }

        // Create data.txt for offline transactions
        File dataFile = new File("data.txt");
        try {
            if (!dataFile.exists()) {
                if (dataFile.createNewFile()) {
                    System.out.println("data.txt created successfully.");
                }
            } else {
                System.out.println("data.txt already exists.");
            }
        } catch (IOException e) {
            System.err.println("Error creating data.txt: " + e.getMessage());
        }
    }

}
