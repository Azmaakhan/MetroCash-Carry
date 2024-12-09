package code.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import code.config.DBConnection;
import code.functions.BranchManagerPanel;
import code.functions.CashierPanel;
import code.functions.DataEntryPanel;
import code.functions.SuperAdminPanel;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("Metro POS - Login");
        setSize(400, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener((ActionEvent e) -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            loginUser(email, password);
        });

        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);
    }
    private void loginUser(String email, String password) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT role, branch_code FROM employees WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("role");
                String branchCode = resultSet.getString("branch_code");
                this.dispose();

                switch (role) {
                    case "SuperAdmin":
                        new SuperAdminPanel().setVisible(true);
                        break;
                    case "BranchManager":
                        new BranchManagerPanel(email).setVisible(true);
                        break;
                    case "DataEntryOperator":
                        new DataEntryPanel(branchCode, email).setVisible(true);
                        break;
                    case "Cashier":
                        new CashierPanel(email, branchCode).setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Invalid Role!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Logging In: " + ex.getMessage());
        }
    }
}
