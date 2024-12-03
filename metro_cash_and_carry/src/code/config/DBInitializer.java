package code.config;

import java.sql.*;

public class DBInitializer {
    public static void initializeDB() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Branches (
                    BranchID INT PRIMARY KEY AUTO_INCREMENT,
                    Name VARCHAR(100),
                    City VARCHAR(100),
                    Active BOOLEAN,
                    Address VARCHAR(255),
                    Phone VARCHAR(20),
                    NumEmployees INT DEFAULT 0
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Users (
                    UserID INT PRIMARY KEY AUTO_INCREMENT,
                    Name VARCHAR(100),
                    Email VARCHAR(100),
                    Password VARCHAR(100),
                    Role ENUM('SuperAdmin', 'BranchManager', 'Cashier', 'DataEntryOperator'),
                    BranchID INT,
                    Salary DECIMAL(10, 2),
                    FOREIGN KEY (BranchID) REFERENCES Branches(BranchID)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Vendors (
                    VendorID INT PRIMARY KEY AUTO_INCREMENT,
                    Name VARCHAR(100),
                    Contact VARCHAR(100),
                    Address VARCHAR(255),
                    BranchID INT,
                    FOREIGN KEY (BranchID) REFERENCES Branches(BranchID)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Products (
                    ProductID INT PRIMARY KEY AUTO_INCREMENT,
                    Name VARCHAR(100),
                    Category VARCHAR(100),
                    OriginalPrice DECIMAL(10, 2),
                    SalePrice DECIMAL(10, 2),
                    PriceByUnit DECIMAL(10, 2),
                    PriceByCarton DECIMAL(10, 2),
                    VendorID INT,
                    FOREIGN KEY (VendorID) REFERENCES Vendors(VendorID)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Sales (
                    SaleID INT PRIMARY KEY AUTO_INCREMENT,
                    ProductID INT,
                    Quantity INT,
                    SaleDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
                )
            """);

            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error initializing database tables: " + e.getMessage());
        }
    }
}
