package code.functions;

import code.auth.LoginScreen;
import code.config.DBConnection;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SuperAdminPanel extends JFrame {
    public SuperAdminPanel() {
        setTitle("Metro POS - Super Admin");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JButton createBranchButton = new JButton("Create Branch");
        createBranchButton.addActionListener(this::createBranch);
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener((ActionEvent e) -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        JPanel panel = new JPanel();
        panel.add(createBranchButton);
        panel.add(logoutButton);

        add(panel);
    }

    private void createBranch(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField branchCodeField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField cityField = new JTextField(15);
        JTextField addressField = new JTextField(15);
        JTextField phoneField = new JTextField(15);

        JTextField managerNameField = new JTextField(15);
        JTextField managerEmailField = new JTextField(15);
        JPasswordField managerPasswordField = new JPasswordField(15);
        JTextField managerSalaryField = new JTextField(15);

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

            try (Connection connection = DBConnection.getConnection()) {

                String sqlBranch = "INSERT INTO branches (branch_code, name, city, active, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement branchStmt = connection.prepareStatement(sqlBranch);
                branchStmt.setString(1, branchCode);
                branchStmt.setString(2, name);
                branchStmt.setString(3, city);
                branchStmt.setBoolean(4, true);
                branchStmt.setString(5, address);
                branchStmt.setString(6, phone);
                branchStmt.executeUpdate();

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
