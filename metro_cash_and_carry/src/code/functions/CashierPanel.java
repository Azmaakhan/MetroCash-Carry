package code.functions;

import code.auth.LoginScreen;
import code.config.DBConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CashierPanel extends JFrame {

    private boolean offlineMode = false;
    private List<String> offlineTransactions = new ArrayList<>();

    public CashierPanel() {
        setTitle("Cashier Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        checkOfflineMode();

        // Buttons
        JButton processSaleButton = new JButton("Process Sale");
        processSaleButton.addActionListener(this::processSale);

        JButton syncButton = new JButton("Sync Data");
        syncButton.addActionListener(this::syncData);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener((ActionEvent e) -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        // Layout
        JPanel panel = new JPanel();
        panel.add(processSaleButton);
        panel.add(syncButton);
        panel.add(logoutButton);

        add(panel);
    }

    private void checkOfflineMode() {
        try (BufferedReader reader = new BufferedReader(new FileReader("temp.txt"))) {
            String status = reader.readLine();
            offlineMode = "true".equalsIgnoreCase(status);
            if (offlineMode) {
                JOptionPane.showMessageDialog(this, "You are in offline mode. Sales data will be stored locally.");
            }
        } catch (IOException ex) {
            offlineMode = false;
            JOptionPane.showMessageDialog(this, "Offline mode file not found. Defaulting to online mode.");
        }
    }

    private void processSale(ActionEvent e) {
        // Fetch product list
        List<String> products = getProducts();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products found.");
            return;
        }

        String selectedProduct = (String) JOptionPane.showInputDialog(this,
                "Select Product:",
                "Product Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                products.toArray(),
                products.get(0));

        if (selectedProduct == null || selectedProduct.isEmpty()) {
            return;
        }

        // Get current stock for the selected product
        int currentStock = getProductStock(selectedProduct);

        // Show dialog to enter quantity with current stock info
        String quantityStr = JOptionPane.showInputDialog(this, "Enter Quantity (Current stock: " + currentStock + "):");
        if (quantityStr == null || quantityStr.isEmpty()) {
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
                return;
            }
            if (currentStock < quantity) {
                JOptionPane.showMessageDialog(this, "Insufficient stock for this product.");
                return;
            }

            if (offlineMode) {
                saveSaleOffline(selectedProduct, quantity);
            } else {
                processSaleOnline(selectedProduct, quantity);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity entered.");
        }
    }

    private List<String> getProducts() {
        List<String> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT name FROM products";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                products.add(resultSet.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching products: " + ex.getMessage());
        }
        return products;
    }

    private int getProductStock(String productName) {
        int stock = 0;
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT stock FROM products WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, productName);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                stock = resultSet.getInt("stock");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching product stock: " + ex.getMessage());
        }
        return stock;
    }

    private void processSaleOnline(String productName, int quantity) {
        try (Connection connection = DBConnection.getConnection()) {
            String fetchProductSql = "SELECT sale_price FROM products WHERE name = ?";
            PreparedStatement fetchProductStmt = connection.prepareStatement(fetchProductSql);
            fetchProductStmt.setString(1, productName);

            ResultSet resultSet = fetchProductStmt.executeQuery();
            if (resultSet.next()) {
                double salePrice = resultSet.getDouble("sale_price");

                double totalPrice = salePrice * quantity;

                String updateStockSql = "UPDATE products SET stock = stock - ? WHERE name = ?";
                PreparedStatement updateStockStmt = connection.prepareStatement(updateStockSql);
                updateStockStmt.setInt(1, quantity);
                updateStockStmt.setString(2, productName);
                updateStockStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Sale processed successfully!\nTotal Price: " + totalPrice);
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error processing sale: " + ex.getMessage());
        }
    }

    private void saveSaleOffline(String productName, int quantity) {
        String transaction = "Product: " + productName + ", Quantity: " + quantity;
        offlineTransactions.add(transaction);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt", true))) {
            writer.write(transaction);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Sale recorded offline:\n" + transaction);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving offline: " + ex.getMessage());
        }
    }

    private void syncData(ActionEvent e) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                offlineTransactions.add(line);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading offline transactions: " + ex.getMessage());
        }

        for (String transaction : offlineTransactions) {
            String[] parts = transaction.split(", ");
            String productName = parts[0].split(": ")[1];
            int quantity = Integer.parseInt(parts[1].split(": ")[1]);

            processSaleOnline(productName, quantity);
        }

        // Clear offline data file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"))) {
            writer.write("");
            JOptionPane.showMessageDialog(this, "Offline transactions synced successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error clearing offline data: " + ex.getMessage());
        }
    }
}
