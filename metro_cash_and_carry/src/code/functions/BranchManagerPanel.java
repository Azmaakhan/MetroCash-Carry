package code.functions;

import code.config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BranchManagerPanel extends JFrame {

    private String branchCode;

    public BranchManagerPanel() {
        setTitle("Branch Manager Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Fetch Branch Code for the logged-in Branch Manager
        branchCode = fetchBranchCode();

        // Buttons
        JButton addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.addActionListener(this::addEmployee);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(this::changePassword);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener((ActionEvent e) -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        // Layout
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.add(addEmployeeButton);
        panel.add(changePasswordButton);
        panel.add(logoutButton);

        add(panel);
    }

    private String fetchBranchCode() {
        // Assuming the Branch Manager's login is done using `employees` table and branchCode is fetched at login
        // This placeholder implementation assumes you have some user session mechanism.
        // Replace this with your actual session management logic to fetch the branch code.
        return "BR001"; // Example branch code
    }

    private void addEmployee(ActionEvent e) {
        String[] roles = {"Cashier", "DataEntryOperator"};
        String selectedRole = (String) JOptionPane.showInputDialog(
                this,
                "Select Role:",
                "Add Employee",
                JOptionPane.QUESTION_MESSAGE,
                null,
                roles,
                roles[0]
        );

        if (selectedRole == null) {
            return; // User canceled
        }

        String name = JOptionPane.showInputDialog(this, "Enter Employee Name:");
        String empNo = JOptionPane.showInputDialog(this, "Enter Employee Number:");
        String email = JOptionPane.showInputDialog(this, "Enter Employee Email:");
        String salaryStr = JOptionPane.showInputDialog(this, "Enter Salary:");

        try {
            double salary = Double.parseDouble(salaryStr);

            try (Connection connection = DBConnection.getConnection()) {
                String sql = "INSERT INTO employees (name, emp_no, email, password, branch_code, salary, role) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, empNo);
                statement.setString(3, email);
                statement.setString(4, "Password_123"); // Default password
                statement.setString(5, branchCode);
                statement.setDouble(6, salary);
                statement.setString(7, selectedRole);

                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, selectedRole + " added successfully!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid salary format!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage());
        }
    }

    private void changePassword(ActionEvent e) {
        String newPassword = JOptionPane.showInputDialog(this, "Enter New Password:");
        String confirmPassword = JOptionPane.showInputDialog(this, "Confirm New Password:");

        if (newPassword == null || confirmPassword == null) {
            return; // User canceled
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "UPDATE employees SET password = ? WHERE branch_code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newPassword);
            statement.setString(2, branchCode);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No employees found to update!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating password: " + ex.getMessage());
        }
    }
}
