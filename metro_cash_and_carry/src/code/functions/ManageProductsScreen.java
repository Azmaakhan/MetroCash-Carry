package code.functions;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import code.config.DBConnection;
import java.sql.*;

public class ManageProductsScreen extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;

    public ManageProductsScreen() {
        setTitle("Manage Products");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        tableModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Category", "Vendor", "Original Price", "Sale Price", "Unit Price", "Carton Price"}, 0);
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(e -> openAddProductDialog());
        JButton editButton = new JButton("Edit Product");
        editButton.addActionListener(e -> openEditProductDialog());
        JButton deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        loadProductData();
        setVisible(true);
    }

    private void loadProductData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.ProductID, p.Name, p.Category, v.Name AS VendorName, p.OriginalPrice, p.SalePrice, p.UnitPrice, p.CartonPrice " +
                    "FROM Products p INNER JOIN Vendors v ON p.VendorID = v.VendorID";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getString("Category"),
                        rs.getString("VendorName"),
                        rs.getDouble("OriginalPrice"),
                        rs.getDouble("SalePrice"),
                        rs.getDouble("UnitPrice"),
                        rs.getDouble("CartonPrice")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading product data: " + ex.getMessage());
        }
    }

    private void openAddProductDialog() {
        new ProductDialog(this, "Add Product", null);
    }

    private void openEditProductDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.");
            return;
        }
        int productId = (int) productTable.getValueAt(selectedRow, 0);
        new ProductDialog(this, "Edit Product", productId);
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }
        int productId = (int) productTable.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM Products WHERE ProductID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            stmt.executeUpdate();
            loadProductData();
            JOptionPane.showMessageDialog(this, "Product deleted successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage());
        }
    }
}
