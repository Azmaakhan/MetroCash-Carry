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

    public BranchManagerPanel(String email) {
        setTitle("Branch Manager Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Fetch Branch Code for the logged-in Branch Manager
        branchCode = fetchBranchCode(email);

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
        JPanel panel = new JPanel();
        panel.add(addEmployeeButton);
        panel.add(changePasswordButton);
        panel.add(logoutButton);

        add(panel);
    }

    private String fetchBranchCode(String email) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT branch_code FROM employees WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("branch_code");  // Return the branch code for this manager
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching branch code: " + ex.getMessage());
        }
        return null;
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

        // Create a panel to collect all information in one dialog
        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField nameField = new JTextField();
        JTextField empNoField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField salaryField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Employee Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Employee Number:"));
        panel.add(empNoField);
        panel.add(new JLabel("Employee Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Employee Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.CANCEL_OPTION) {
            return; // User canceled
        }

        String name = nameField.getText();
        String empNo = empNoField.getText();
        String email = emailField.getText();
        String salaryStr = salaryField.getText();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || empNo.isEmpty() || email.isEmpty() || salaryStr.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
            return;
        }

        try {
            double salary = Double.parseDouble(salaryStr);

            try (Connection connection = DBConnection.getConnection()) {
                String sql = "INSERT INTO employees (name, emp_no, email, password, branch_code, salary, role) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, empNo);
                statement.setString(3, email);
                statement.setString(4, password); // Use the entered password
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
