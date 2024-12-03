package code.functions;

import code.config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class SuperAdminPanel extends JFrame {
    public SuperAdminPanel() {
        setTitle("Metro POS - Super Admin");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton createBranchButton = new JButton("Create Branch");
        createBranchButton.addActionListener(this::createBranch);

        JButton reportsButton = new JButton("View Reports");
        reportsButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Reports Module Coming Soon!");
        });
        JButton logoutButton = new JButton("Logout");
        

        JPanel panel = new JPanel();
        panel.add(createBranchButton);
        panel.add(reportsButton);

        add(panel);
    }

    private void createBranch(ActionEvent e) {
        // Show input dialog with multiple fields using a custom panel
        JPanel panel = new JPanel(new GridLayout(0, 2));

        // Branch fields
        JTextField branchCodeField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField cityField = new JTextField(15);
        JTextField addressField = new JTextField(15);
        JTextField phoneField = new JTextField(15);

        // Manager (employee) fields
        JTextField managerNameField = new JTextField(15);
        JTextField managerEmailField = new JTextField(15);
        JPasswordField managerPasswordField = new JPasswordField(15);
        JTextField managerSalaryField = new JTextField(15);

        // Add all fields to the panel
        panel.add(new JLabel("Branch Code:"));
        panel.add(branchCodeField);
        panel.add(new JLabel("Branch Name:"));
        panel.add(nameField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);

        // Manager details
        panel.add(new JLabel("Manager Name:"));
        panel.add(managerNameField);
        panel.add(new JLabel("Manager Email:"));
        panel.add(managerEmailField);
        panel.add(new JLabel("Manager Password:"));
        panel.add(managerPasswordField);
        panel.add(new JLabel("Manager Salary:"));
        panel.add(managerSalaryField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Branch and Manager Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String branchCode = branchCodeField.getText();
            String name = nameField.getText();
            String city = cityField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();

            String managerName = managerNameField.getText();
            String managerEmail = managerEmailField.getText();
            String managerPassword = new String(managerPasswordField.getPassword());
            double managerSalary = Double.parseDouble(managerSalaryField.getText());

            // Insert branch into the branches table
            try (Connection connection = DBConnection.getConnection()) {
                // Insert branch details
                String sqlBranch = "INSERT INTO branches (branch_code, name, city, active, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement branchStmt = connection.prepareStatement(sqlBranch);
                branchStmt.setString(1, branchCode);
                branchStmt.setString(2, name);
                branchStmt.setString(3, city);
                branchStmt.setBoolean(4, true);  // Branch is active by default
                branchStmt.setString(5, address);
                branchStmt.setString(6, phone);
                branchStmt.executeUpdate();

                // Insert manager (employee) into employees table
                String sqlEmployee = "INSERT INTO employees (name, emp_no, email, password, branch_code, salary, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement employeeStmt = connection.prepareStatement(sqlEmployee);
                employeeStmt.setString(1, managerName);
                employeeStmt.setString(2, "Manager_" + branchCode);
                employeeStmt.setString(3, managerEmail);
                employeeStmt.setString(4, managerPassword);
                employeeStmt.setString(5, branchCode);
                employeeStmt.setDouble(6, managerSalary);
                employeeStmt.setString(7, "BranchManager");
                employeeStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Branch and Manager Created Successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error Creating Branch and Manager: " + ex.getMessage());
            }
        }
    }
}
