package scdproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ForgetPassword {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    static void createAndShowGUI() {
        JFrame frame = new JFrame("Forget Password");
        frame.setSize(500, 300);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        JLabel newPasswordLabel = new JLabel("New Password:");
        JPasswordField newPasswordField = new JPasswordField();

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();

        JButton updatePasswordButton = new JButton("Update Password");

        updatePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (newPassword.equals(confirmPassword)) {
                    if (updatePassword(email, newPassword)) {
                        JOptionPane.showMessageDialog(frame, "Password updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Email not found. Please enter a valid email.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "New Password and Confirm Password do not match.");
                }
            }
        });

        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(newPasswordLabel);
        frame.add(newPasswordField);
        frame.add(confirmPasswordLabel);
        frame.add(confirmPasswordField);
        frame.add(new JPanel()); // Empty panel for spacing
        frame.add(updatePasswordButton);

        // Set background and text color
        //frame.getContentPane().setBackground(new Color(144, 238, 144)); // Light Green
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

    private static boolean updatePassword(String email, String newPassword) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String dbURL = "jdbc:ucanaccess://SCDProject.accdb";
            Connection conn = DriverManager.getConnection(dbURL);

            String query = "UPDATE Registration SET Password = ? WHERE Email = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, email);

            int rowsAffected = preparedStatement.executeUpdate();

            conn.close();
            return rowsAffected > 0;

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
