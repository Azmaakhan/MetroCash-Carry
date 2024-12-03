package code.functions;

import code.config.DBConnection;

import javax.swing.*;
import java.awt.*;
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
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
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
        String productName = JOptionPane.showInputDialog(this, "Enter Product Name:");
        String quantityStr = JOptionPane.showInputDialog(this, "Enter Quantity:");
        if (productName == null || productName.isEmpty() || quantityStr == null || quantityStr.isEmpty()) {
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (offlineMode) {
                saveSaleOffline(productName, quantity);
            } else {
                processSaleOnline(productName, quantity);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity entered.");
        }
    }

    private void processSaleOnline(String productName, int quantity) {
        try (Connection connection = DBConnection.getConnection()) {
            String fetchProductSql = "SELECT stock, sale_price FROM products WHERE name = ?";
            PreparedStatement fetchProductStmt = connection.prepareStatement(fetchProductSql);
            fetchProductStmt.setString(1, productName);

            ResultSet resultSet = fetchProductStmt.executeQuery();
            if (resultSet.next()) {
                int stock = resultSet.getInt("stock");
                double salePrice = resultSet.getDouble("sale_price");

                if (stock < quantity) {
                    JOptionPane.showMessageDialog(this, "Insufficient stock for this product.");
                    return;
                }

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
