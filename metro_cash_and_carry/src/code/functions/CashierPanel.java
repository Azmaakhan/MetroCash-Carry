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
    private final String branchCode;
    private final String email;

    public CashierPanel(String email, String branchCode) {
        this.branchCode = branchCode;
        this.email = email;
        setTitle("Cashier's Panel");
        setSize(800, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        checkOfflineMode();

        
        JButton processSaleButton = new JButton("Process Sale");
        processSaleButton.addActionListener(this::processSale);

        JButton syncButton = new JButton("Sync Data");
        syncButton.addActionListener(this::syncData);

        JButton passwordChangeButton = new JButton("Change Password");
        passwordChangeButton.addActionListener(this::changePassword);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener((ActionEvent e) -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        
        JPanel panel = new JPanel();
        panel.add(processSaleButton);
        panel.add(syncButton);
        panel.add(passwordChangeButton);
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
                products.getFirst());

        if (selectedProduct == null || selectedProduct.isEmpty()) {
            return;
        }

        int currentStock = getProductStock(selectedProduct);

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
            String sql = "SELECT name FROM products WHERE branch_code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, branchCode);

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
            String sql = "SELECT stock FROM products WHERE name = ? AND branch_code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, productName);
            statement.setString(2, branchCode);

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
            String fetchProductSql = "SELECT sale_price, original_price FROM products WHERE name = ? AND branch_code = ?";
            PreparedStatement fetchProductStmt = connection.prepareStatement(fetchProductSql);
            fetchProductStmt.setString(1, productName);
            fetchProductStmt.setString(2, branchCode);
            ResultSet resultSet = fetchProductStmt.executeQuery();
            if (resultSet.next()) {
                double salePrice = resultSet.getDouble("sale_price");
                double costPrice = resultSet.getDouble("original_price");
                double totalPrice = salePrice * quantity;
                double profit = (salePrice - costPrice) * quantity;

                String updateStockSql = "UPDATE products SET stock = stock - ? WHERE name = ? AND branch_code = ?";
                PreparedStatement updateStockStmt = connection.prepareStatement(updateStockSql);
                updateStockStmt.setInt(1, quantity);
                updateStockStmt.setString(2, productName);
                updateStockStmt.setString(3, branchCode);
                updateStockStmt.executeUpdate();

                String insertSaleSql = "INSERT INTO sales (branch_code, sale_date, product_name, quantity, total_price, profit) " +
                        "VALUES (?, CURRENT_DATE, ?, ?, ?, ?)";
                PreparedStatement insertSaleStmt = connection.prepareStatement(insertSaleSql);
                insertSaleStmt.setString(1, branchCode);
                insertSaleStmt.setString(2, productName);
                insertSaleStmt.setInt(3, quantity);
                insertSaleStmt.setDouble(4, totalPrice);
                insertSaleStmt.setDouble(5, profit);
                insertSaleStmt.executeUpdate();

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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"))) {
            writer.write("");
            JOptionPane.showMessageDialog(this, "Offline transactions synced successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error clearing offline data: " + ex.getMessage());
        }
    }

    private void changePassword(ActionEvent e) {
        String currentPassword = JOptionPane.showInputDialog(this, "Enter Current Password:");
        if (currentPassword == null || currentPassword.isEmpty()) {
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT password FROM employees WHERE branch_code = ? AND email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, branchCode);
            statement.setString(2, email);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                if (!dbPassword.equals(currentPassword)) {
                    JOptionPane.showMessageDialog(this, "Incorrect current password.");
                    return;
                }
                String newPassword = JOptionPane.showInputDialog(this, "Enter New Password:");
                if (newPassword != null && !newPassword.isEmpty()) {
                    String updatePasswordSql = "UPDATE employees SET password = ? WHERE branch_code = ? AND email = ?";
                    PreparedStatement updatePasswordStmt = connection.prepareStatement(updatePasswordSql);
                    updatePasswordStmt.setString(1, newPassword);
                    updatePasswordStmt.setString(2, branchCode);
                    updatePasswordStmt.setString(3, email);
                    updatePasswordStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Password updated successfully.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error changing password: " + ex.getMessage());
        }
    }
}
