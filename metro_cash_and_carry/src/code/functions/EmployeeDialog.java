package code.functions;

import javax.swing.*;
import java.awt.*;
import code.config.DBConnection;
import java.sql.*;

public class EmployeeDialog extends JDialog {
    private JTextField nameField, emailField, salaryField;
    private JComboBox<String> roleDropdown;
    private int branchId, employeeId;

    public EmployeeDialog(JFrame parent, String title, int branchId, Integer employeeId) {
        super(parent, title, true);
        this.branchId = branchId;
        this.employeeId = employeeId != null ? employeeId : -1;

        setSize(400, 300);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Role:"));
        roleDropdown = new JComboBox<>(new String[]{"Cashier", "DataEntryOperator"});
        add(roleDropdown);

        add(new JLabel("Salary:"));
        salaryField = new JTextField();
        add(salaryField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveEmployee());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        if (this.employeeId != -1) {
            loadEmployeeData();
        }
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void loadEmployeeData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Users WHERE UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("Name"));
                emailField.setText(rs.getString("Email"));
                roleDropdown.setSelectedItem(rs.getString("Role"));
                salaryField.setText(String.valueOf(rs.getDouble("Salary")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + ex.getMessage());
        }
    }

    private void saveEmployee() {
        String name = nameField.getText();
        String email = emailField.getText();
        String role = (String) roleDropdown.getSelectedItem();
        double salary;
        try {
            salary = Double.parseDouble(salaryField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid salary amount.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query;
            if (employeeId == -1) {
                query = "INSERT INTO Users (Name, Email, Role, BranchID, Salary, Password) VALUES (?, ?, ?, ?, ?, ?)";
            } else {
                query = "UPDATE Users SET Name = ?, Email = ?, Role = ?, Salary = ? WHERE UserID = ?";
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, role);
            if (employeeId == -1) {
                stmt.setInt(4, branchId);
                stmt.setDouble(5, salary);
                stmt.setString(6, "Password_123"); // Default password
            } else {
                stmt.setDouble(4, salary);
                stmt.setInt(5, employeeId);
            }
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee saved successfully.");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving employee: " + ex.getMessage());
        }
    }
}
