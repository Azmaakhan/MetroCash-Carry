package code.functions;

import javax.swing.*;
import java.awt.*;

import code.config.DBConnection;
import java.sql.*;

public class LoginScreen extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginScreen() {
        setTitle("Metro POS - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        add(loginButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        setVisible(true);
    }

    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Users WHERE Email = ? AND Password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("Role");
                JOptionPane.showMessageDialog(this, "Welcome " + role + "!");
                navigateToRoleScreen(role, rs.getInt("BranchID"), rs.getInt("UserID"));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!");
        }
    }

    private void navigateToRoleScreen(String role, int branchId, int userId) {
        dispose(); // Close the current screen
        switch (role) {
            case "SuperAdmin" -> new SuperAdminScreen();
//            case "BranchManager" -> new BranchManagerScreen(branchId);
//            case "Cashier" -> new CashierScreen(branchId);
//            case "DataEntryOperator" -> new DataEntryOperatorScreen(branchId, userId);
            default -> JOptionPane.showMessageDialog(this, "Invalid role assigned!");
        }
    }
}
