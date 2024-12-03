package code.functions;

import javax.swing.*;
import java.awt.*;
import code.config.DBConnection;
import java.sql.*;

public class ProductDialog extends JDialog {
    private JTextField nameField, categoryField, originalPriceField, salePriceField, unitPriceField, cartonPriceField;
    private JComboBox<String> vendorDropdown;
    private int productId;

    public ProductDialog(JFrame parent, String title, Integer productId) {
        super(parent, title, true);
        this.productId = productId != null ? productId : -1;

        setSize(400, 400);
        setLayout(new GridLayout(8, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Category:"));
        categoryField = new JTextField();
        add(categoryField);

        add(new JLabel("Vendor:"));
        vendorDropdown = new JComboBox<>();
        populateVendorDropdown();
        add(vendorDropdown);

        add(new JLabel("Original Price:"));
        originalPriceField = new JTextField();
        add(originalPriceField);

        add(new JLabel("Sale Price:"));
        salePriceField = new JTextField();
        add(salePriceField);

        add(new JLabel("Unit Price:"));
        unitPriceField = new JTextField();
        add(unitPriceField);

        add(new JLabel("Carton Price:"));
        cartonPriceField = new JTextField();
        add(cartonPriceField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveProduct());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        if (this.productId != -1) {
            loadProductData();
        }
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void populateVendorDropdown() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT VendorID, Name FROM Vendors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                vendorDropdown.addItem(rs.getString("Name") + " (ID: " + rs.getInt("VendorID") + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vendors: " + ex.getMessage());
        }
    }

    private void loadProductData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Products WHERE ProductID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("Name"));
                categoryField.setText(rs.getString("Category"));
                originalPriceField.setText(String.valueOf(rs.getDouble("OriginalPrice")));
                salePriceField.setText(String.valueOf(rs.getDouble("SalePrice")));
                unitPriceField.setText(String.valueOf(rs.getDouble("UnitPrice")));
                cartonPriceField.setText(String.valueOf(rs.getDouble("CartonPrice")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading product data: " + ex.getMessage());
        }
    }

    private void saveProduct() {
        String name = nameField.getText();
        String category = categoryField.getText();
        String vendorDetails = (String) vendorDropdown.getSelectedItem();
        int vendorId = Integer.parseInt(vendorDetails.split("ID: ")[1].replace(")", ""));
        double originalPrice, salePrice, unitPrice, cartonPrice;
        try {
            originalPrice = Double.parseDouble(originalPriceField.getText());
            salePrice = Double.parseDouble(salePriceField.getText());
            unitPrice = Double.parseDouble(unitPriceField.getText());
            cartonPrice = Double.parseDouble(cartonPriceField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price input.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query;
            if (productId == -1) {
                query = "INSERT INTO Products (Name, Category, VendorID, OriginalPrice, SalePrice, UnitPrice, CartonPrice) VALUES (?, ?, ?, ?, ?, ?, ?)";
            } else {
                query = "UPDATE Products SET Name = ?, Category = ?, VendorID = ?, OriginalPrice = ?, SalePrice = ?, UnitPrice = ?, CartonPrice = ? WHERE ProductID = ?";
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setInt(3, vendorId);
            stmt.setDouble(4, originalPrice);
            stmt.setDouble(5, salePrice);
            stmt.setDouble(6, unitPrice);
            stmt.setDouble(7, cartonPrice);
            if (productId != -1) {
                stmt.setInt(8, productId);
            }
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product saved successfully.");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving product: " + ex.getMessage());
        }
    }
}
