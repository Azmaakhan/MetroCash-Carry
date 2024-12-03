package code.functions;

import javax.swing.*;
import java.awt.*;
import code.config.DBConnection;
import java.sql.*;

public class BranchDialog extends JDialog {
    private JTextField nameField, cityField, addressField, phoneField;
    private JCheckBox activeCheckbox;
    private int branchId;

    public BranchDialog(JFrame parent, String title, Integer branchId) {
        super(parent, title, true);
        this.branchId = branchId != null ? branchId : -1;
        setSize(400, 300);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Branch Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("City:"));
        cityField = new JTextField();
        add(cityField);

        add(new JLabel("Address:"));
        addressField = new JTextField();
        add(addressField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Active:"));
        activeCheckbox = new JCheckBox();
        add(activeCheckbox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveBranch());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        if (this.branchId != -1) {
            loadBranchData();
        }
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void loadBranchData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Branches WHERE BranchID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("Name"));
                cityField.setText(rs.getString("City"));
                addressField.setText(rs.getString("Address"));
                phoneField.setText(rs.getString("Phone"));
                activeCheckbox.setSelected(rs.getBoolean("Active"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading branch data: " + ex.getMessage());
        }
    }

    private void saveBranch() {
        String name = nameField.getText();
        String city = cityField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        boolean active = activeCheckbox.isSelected();

        try (Connection conn = DBConnection.getConnection()) {
            String query;
            if (branchId == -1) {
                query = "INSERT INTO Branches (Name, City, Active, Address, Phone) VALUES (?, ?, ?, ?, ?)";
            } else {
                query = "UPDATE Branches SET Name = ?, City = ?, Active = ?, Address = ?, Phone = ? WHERE BranchID = ?";
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, city);
            stmt.setBoolean(3, active);
            stmt.setString(4, address);
            stmt.setString(5, phone);
            if (branchId != -1) {
                stmt.setInt(6, branchId);
            }
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Branch saved successfully.");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving branch: " + ex.getMessage());
        }
    }
}
