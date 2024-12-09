package code.functions;

import code.auth.LoginScreen;
import code.config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataEntryPanel extends JFrame {

    private boolean offlineMode = false;
    private final String branchCode;
    private final String email;

    public DataEntryPanel(String branchCode, String email) {
        this.branchCode = branchCode;
        this.email = email;
        setTitle("Data Entry Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        checkOfflineMode();

        JButton manageVendorsButton = new JButton("Manage Vendors");
        manageVendorsButton.addActionListener(this::manageVendors);

        JButton addProductButton = new JButton("Add Product");
        addProductButton.addActionListener(this::addProduct);

        JButton editProductButton = new JButton("Edit Product");
        editProductButton.addActionListener(this::editProduct);

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
        panel.add(manageVendorsButton);
        panel.add(addProductButton);
        panel.add(editProductButton);
        panel.add(syncButton);
        panel.add(logoutButton);

        add(panel);
    }

    private void checkOfflineMode() {
        try (BufferedReader reader = new BufferedReader(new FileReader("temp.txt"))) {
            String status = reader.readLine();
            offlineMode = "true".equalsIgnoreCase(status);
            if (offlineMode) {
                JOptionPane.showMessageDialog(this, "You are in offline mode. Data will be stored locally.");
            }
        } catch (IOException ex) {
            offlineMode = false;
            JOptionPane.showMessageDialog(this, "Offline mode file not found. Defaulting to online mode.");
        }
    }

    private void manageVendors(ActionEvent e) {
        String[] options = {"Old Vendor", "New Vendor"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Select Vendor Operation:",
                "Vendor Management",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if ("Old Vendor".equals(choice)) {
            fetchVendorInformation();
        } else if ("New Vendor".equals(choice)) {
            addVendorInformation();
        }
    }

    private void fetchVendorInformation() {
        String vendorName = JOptionPane.showInputDialog(this, "Enter Vendor Name:");
        if (vendorName == null || vendorName.isEmpty()) {
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM vendors WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, vendorName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String info = "Vendor Name: " + resultSet.getString("name") +
                        "\nAddress: " + resultSet.getString("address") +
                        "\nPhone: " + resultSet.getString("phone");
                JOptionPane.showMessageDialog(this, info);
            } else {
                JOptionPane.showMessageDialog(this, "Vendor not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching vendor: " + ex.getMessage());
        }
    }
    private void addVendorInformation() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        panel.add(new JLabel("Vendor Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Vendor Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Vendor Phone:"));
        panel.add(phoneField);
        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Vendor Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.");
                return;
            }
            if (offlineMode) {
                saveToOfflineFile("Vendor: " + name + ", Address: " + address + ", Phone: " + phone);
            } else {
                try (Connection connection = DBConnection.getConnection()) {
                    String sql = "INSERT INTO vendors (name, address, phone) VALUES (?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, name);
                    statement.setString(2, address);
                    statement.setString(3, phone);
                    statement.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Vendor added successfully!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding vendor: " + ex.getMessage());
                }
            }
        }
    }

    private void addProduct(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(7, 2));
        JTextField nameField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        JTextField originalPriceField = new JTextField(20);
        JTextField salePriceField = new JTextField(20);
        JTextField pricePerUnitField = new JTextField(20);
        JTextField pricePerCartonField = new JTextField(20);
        JTextField stockField = new JTextField(20);
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Original Price:"));
        panel.add(originalPriceField);
        panel.add(new JLabel("Sale Price:"));
        panel.add(salePriceField);
        panel.add(new JLabel("Price Per Unit:"));
        panel.add(pricePerUnitField);
        panel.add(new JLabel("Price Per Carton:"));
        panel.add(pricePerCartonField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Product Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String originalPriceStr = originalPriceField.getText().trim();
            String salePriceStr = salePriceField.getText().trim();
            String pricePerUnitStr = pricePerUnitField.getText().trim();
            String pricePerCartonStr = pricePerCartonField.getText().trim();
            String stockStr = stockField.getText().trim();

            if (name.isEmpty() || category.isEmpty() || originalPriceStr.isEmpty() || salePriceStr.isEmpty() || pricePerUnitStr.isEmpty() || pricePerCartonStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.");
                return;
            }

            try {
                double originalPrice = Double.parseDouble(originalPriceStr);
                double salePrice = Double.parseDouble(salePriceStr);
                double pricePerUnit = Double.parseDouble(pricePerUnitStr);
                double pricePerCarton = Double.parseDouble(pricePerCartonStr);
                double stock = Double.parseDouble(stockStr);

                if (offlineMode) {
                    saveToOfflineFile("Product: " + name + ", Category: " + category +
                            ", Original Price: " + originalPrice + ", Sale Price: " + salePrice +
                            ", Price Per Unit: " + pricePerUnit + ", Price Per Carton: " + pricePerCarton + ", Stock: " + stock);
                } else {
                    try (Connection connection = DBConnection.getConnection()) {
                        String sql = "INSERT INTO products (name, branch_code, category, stock, original_price, sale_price, price_per_unit, price_per_carton) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, name);
                        statement.setString(2, branchCode);
                        statement.setString(3, category);
                        statement.setDouble(4, stock);
                        statement.setDouble(5, originalPrice);
                        statement.setDouble(6, salePrice);
                        statement.setDouble(7, pricePerUnit);
                        statement.setDouble(8, pricePerCarton);
                        statement.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Product added successfully!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage());
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Please enter valid numerical values for prices.");
            }
        }
    }

    private void saveToOfflineFile(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt", true))) {
            writer.write(data);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Data saved offline: " + data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving offline: " + ex.getMessage());
        }
    }

    private void syncData(ActionEvent e) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Syncing: " + line);
            }
            JOptionPane.showMessageDialog(this, "Data synced successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error syncing data: " + ex.getMessage());
        }
    }

    private void editProduct(ActionEvent e) {
        try (Connection connection = DBConnection.getConnection()) {
            String fetchProductsSql = "SELECT * FROM products";
            PreparedStatement statement = connection.prepareStatement(fetchProductsSql);
            ResultSet resultSet = statement.executeQuery();

            DefaultListModel<String> productListModel = new DefaultListModel<>();
            while (resultSet.next()) {
                productListModel.addElement(resultSet.getString("name"));
            }

            if (productListModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No products available for editing.");
                return;
            }

            JList<String> productList = new JList<>(productListModel);
            int selection = JOptionPane.showConfirmDialog(
                    this, new JScrollPane(productList), "Select Product to Edit",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (selection == JOptionPane.OK_OPTION && !productList.isSelectionEmpty()) {
                String selectedProduct = productList.getSelectedValue();
                editProductDetails(selectedProduct);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching products: " + ex.getMessage());
        }
    }

    private void editProductDetails(String productName) {
        try (Connection connection = DBConnection.getConnection()) {
            String fetchProductSql = "SELECT * FROM products WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(fetchProductSql);
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Product not found.");
                return;
            }

            String category = resultSet.getString("category");
            double originalPrice = resultSet.getDouble("original_price");
            double salePrice = resultSet.getDouble("sale_price");
            int stock = resultSet.getInt("stock");

            JPanel panel = new JPanel(new GridLayout(5, 2));
            JTextField categoryField = new JTextField(category, 20);
            JTextField originalPriceField = new JTextField(String.valueOf(originalPrice), 20);
            JTextField salePriceField = new JTextField(String.valueOf(salePrice), 20);
            JTextField stockField = new JTextField(String.valueOf(stock), 20);

            panel.add(new JLabel("Category:"));
            panel.add(categoryField);
            panel.add(new JLabel("Original Price:"));
            panel.add(originalPriceField);
            panel.add(new JLabel("Sale Price:"));
            panel.add(salePriceField);
            panel.add(new JLabel("Stock:"));
            panel.add(stockField);

            int option = JOptionPane.showConfirmDialog(this, panel, "Edit Product Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String updatedCategory = categoryField.getText().trim();
                double updatedOriginalPrice = Double.parseDouble(originalPriceField.getText().trim());
                double updatedSalePrice = Double.parseDouble(salePriceField.getText().trim());
                int updatedStock = Integer.parseInt(stockField.getText().trim());

                String updateProductSql = "UPDATE products SET category = ?, original_price = ?, sale_price = ?, stock = ? WHERE name = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateProductSql);
                updateStatement.setString(1, updatedCategory);
                updateStatement.setDouble(2, updatedOriginalPrice);
                updateStatement.setDouble(3, updatedSalePrice);
                updateStatement.setInt(4, updatedStock);
                updateStatement.setString(5, productName);
                updateStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product updated successfully!");
            }
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + ex.getMessage());
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
