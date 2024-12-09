package code.functions;

import javax.swing.*;
import java.awt.*;
import code.config.DBConnection;
import java.sql.*;

public class VendorDialog extends JDialog {
    private JTextField nameField, contactField, addressField;
    private int vendorId;
    public VendorDialog(JFrame parent, String title, Integer vendorId) {
        super(parent, title, true);
        this.vendorId = vendorId != null ? vendorId : -1;
        setSize(400, 300);
        setLayout(new GridLayout(4, 2));
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Contact:"));
        contactField = new JTextField();
        add(contactField);

        add(new JLabel("Address:"));
        addressField = new JTextField();
        add(addressField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveVendor());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        if (this.vendorId != -1) {
            loadVendorData();
        }
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void loadVendorData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Vendors WHERE VendorID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("Name"));
                contactField.setText(rs.getString("Contact"));
                addressField.setText(rs.getString("Address"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vendor data: " + ex.getMessage());
        }
    }

    private void saveVendor() {
        String name = nameField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();

        try (Connection conn = DBConnection.getConnection()) {
            String query;
            if (vendorId == -1) {
                query = "INSERT INTO Vendors (Name, Contact, Address) VALUES (?, ?, ?)";
            } else {
                query = "UPDATE Vendors SET Name = ?, Contact = ?, Address = ? WHERE VendorID = ?";
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.setString(3, address);
            if (vendorId != -1) {
                stmt.setInt(4, vendorId);
            }
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Vendor saved successfully.");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving vendor: " + ex.getMessage());
        }
    }
}
