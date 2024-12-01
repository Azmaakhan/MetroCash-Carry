package scdproject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupPage {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    static void createAndShowGUI() {
        JFrame frame = new JFrame("Signup Page");
        frame.setSize(800, 300);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();

        JButton signUpButton = new JButton("Sign Up");

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String email = emailField.getText();
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String confirmPassword = new String(confirmPasswordField.getPassword());

                    if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        throw new IllegalArgumentException("Please fill in all fields.");
                    }

                    if (!email.contains("@")) {
                        throw new IllegalArgumentException("Invalid email format.");
                    }

                    if (!password.equals(confirmPassword)) {
                        throw new IllegalArgumentException("Password and Confirm Password do not match.");
                    }

                    // Database Connection and Insertion
                    insertIntoDatabase(username, email, password);
                    JOptionPane.showMessageDialog(frame, "Signup successful!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }
        });

        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(confirmPasswordLabel);
        frame.add(confirmPasswordField);
        frame.add(new JPanel()); // Empty panel for spacing
        frame.add(signUpButton);

        // Set background and text color
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

    private static void insertIntoDatabase(String username, String email, String password) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String dbURL = "jdbc:ucanaccess://SCDProject.accdb";
            Connection conn = DriverManager.getConnection(dbURL);

            String insertQuery = "INSERT INTO Registration (Username, Email, Password) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            preparedStatement.executeUpdate();

            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }
}
