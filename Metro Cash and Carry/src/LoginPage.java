package scdproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage {
    public LoginPage() {
        JFrame frame = new JFrame("Login Page");
        frame.setSize(500, 250);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setColumns(100); // Set column size to 100

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setColumns(100); // Set column size to 100

        JButton loginButton = new JButton("Login");
        JButton forgetPasswordButton = new JButton("Forget Password");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(username, password)) {
                HomePage x=new HomePage();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.");
            }
        });

        forgetPasswordButton.addActionListener(e -> {
            SwingUtilities.invokeLater(ForgetPassword::createAndShowGUI);
        });

        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(new JPanel()); // Empty panel for spacing
        frame.add(loginButton);
        frame.add(new JPanel()); // Empty panel for spacing
        frame.add(forgetPasswordButton);

        setTextColor(frame, Color.darkGray);

        frame.setVisible(true);
    }

    private static void setTextColor(Container container, Color color) {
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                setTextColor((Container) component, color);
            }
            component.setForeground(color);
        }
    }

    private static boolean authenticateUser(String username, String password) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String dbURL = "jdbc:ucanaccess://SCDProject.accdb";
            Connection conn = DriverManager.getConnection(dbURL);

            String query = "SELECT * FROM Registration WHERE Username = ? AND Password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                conn.close();
                return true; // User found and password matched
            }

            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return false; // User not found or password didn't match
    }
}
