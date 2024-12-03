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

        JPanel panel = new JPanel();
        panel.add(createBranchButton);
        panel.add(reportsButton);

        add(panel);
    }

    private void createBranch(ActionEvent e) {
        String branchCode = JOptionPane.showInputDialog(this, "Enter Branch Code:");
        String name = JOptionPane.showInputDialog(this, "Enter Branch Name:");
        String city = JOptionPane.showInputDialog(this, "Enter City:");
        String address = JOptionPane.showInputDialog(this, "Enter Address:");
        String phone = JOptionPane.showInputDialog(this, "Enter Phone Number:");

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO branches (branch_code, name, city, active, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, branchCode);
            statement.setString(2, name);
            statement.setString(3, city);
            statement.setBoolean(4, true);
            statement.setString(5, address);
            statement.setString(6, phone);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Branch Created Successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Creating Branch: " + ex.getMessage());
        }
    }
}
