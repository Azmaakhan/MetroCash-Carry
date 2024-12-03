package code.functions;

import code.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class SuperAdminSetup {
    public static void main(String[] args) {
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

    private static void addSuperAdmin(String name, String email, String password, double salary) {
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
}
