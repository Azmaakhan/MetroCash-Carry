package code.functions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import code.config.DBConnection;
import java.sql.*;

public class SalesEntryScreen extends JFrame {
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JTextField productIDField, quantityField;
    private JLabel totalLabel;
    private double totalAmount = 0.0;

    public SalesEntryScreen() {
        setTitle("Sales Entry");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Quantity", "Price", "Subtotal"}, 0);
        salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(1, 6));
        inputPanel.add(new JLabel("Product ID:"));
        productIDField = new JTextField();
        inputPanel.add(productIDField);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addProductToBill());
        inputPanel.add(addButton);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> removeSelectedProduct());
        inputPanel.add(removeButton);

        add(inputPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: $0.00", JLabel.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(totalLabel, BorderLayout.EAST);

        JButton completeSaleButton = new JButton("Complete Sale");
        completeSaleButton.addActionListener(e -> completeSale());
        bottomPanel.add(completeSaleButton, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addProductToBill() {
        String productID = productIDField.getText();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Products WHERE ProductID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(productID));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("Name");
                double price = rs.getDouble("SalePrice");
                double subtotal = price * quantity;

                tableModel.addRow(new Object[]{productID, name, quantity, price, subtotal});
                totalAmount += subtotal;
                totalLabel.setText("Total: $" + String.format("%.2f", totalAmount));

                productIDField.setText("");
                quantityField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving product: " + ex.getMessage());
        }
    }

    private void removeSelectedProduct() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to remove.");
            return;
        }
        double subtotal = (double) salesTable.getValueAt(selectedRow, 4);
        totalAmount -= subtotal;
        totalLabel.setText("Total: $" + String.format("%.2f", totalAmount));
        tableModel.removeRow(selectedRow);
    }

    private void completeSale() {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int productID = Integer.parseInt((String) tableModel.getValueAt(i, 0));
                int quantity = (int) tableModel.getValueAt(i, 2);
                double subtotal = (double) tableModel.getValueAt(i, 4);

                // Update stock
                String updateStockQuery = "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ?";
                PreparedStatement updateStockStmt = conn.prepareStatement(updateStockQuery);
                updateStockStmt.setInt(1, quantity);
                updateStockStmt.setInt(2, productID);
                updateStockStmt.executeUpdate();

                // Insert into sales table
                String insertSaleQuery = "INSERT INTO Sales (ProductID, Quantity, Subtotal, SaleDate) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                PreparedStatement insertSaleStmt = conn.prepareStatement(insertSaleQuery);
                insertSaleStmt.setInt(1, productID);
                insertSaleStmt.setInt(2, quantity);
                insertSaleStmt.setDouble(3, subtotal);
                insertSaleStmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Sale completed successfully!");
            tableModel.setRowCount(0);
            totalAmount = 0.0;
            totalLabel.setText("Total: $0.00");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error completing sale: " + ex.getMessage());
        }
    }
}
