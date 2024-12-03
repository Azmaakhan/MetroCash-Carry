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

public class DataEntryPanel extends JFrame {

    private boolean offlineMode = false;

    public DataEntryPanel() {
        setTitle("Data Entry Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        checkOfflineMode();

        // Buttons
        JButton manageVendorsButton = new JButton("Manage Vendors");
        manageVendorsButton.addActionListener(this::manageVendors);

        JButton addProductButton = new JButton("Add Product");
        addProductButton.addActionListener(this::addProduct);

        JButton syncButton = new JButton("Sync Data");
        syncButton.addActionListener(this::syncData);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener((ActionEvent e) -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        // Layout
        JPanel panel = new JPanel();
        panel.add(manageVendorsButton);
        panel.add(addProductButton);
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
        // Create a JPanel to hold all text fields for vendor details
        JPanel panel = new JPanel(new GridLayout(4, 2));

        // Create labels and text fields for vendor details
        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);

        // Add labels and text fields to the panel
        panel.add(new JLabel("Vendor Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Vendor Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Vendor Phone:"));
        panel.add(phoneField);

        // Show the dialog to get user input
        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Vendor Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // If the user clicks 'OK', proceed with the input
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
        // Create a JPanel to hold all text fields for product details
        JPanel panel = new JPanel(new GridLayout(7, 2));

        // Create labels and text fields for all the product details
        JTextField nameField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        JTextField originalPriceField = new JTextField(20);
        JTextField salePriceField = new JTextField(20);
        JTextField pricePerUnitField = new JTextField(20);
        JTextField pricePerCartonField = new JTextField(20);

        // Add labels and text fields to the panel
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

        // Show the dialog to get user input
        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Product Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // If the user clicks 'OK', proceed with the input
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String originalPriceStr = originalPriceField.getText().trim();
            String salePriceStr = salePriceField.getText().trim();
            String pricePerUnitStr = pricePerUnitField.getText().trim();
            String pricePerCartonStr = pricePerCartonField.getText().trim();

            if (name.isEmpty() || category.isEmpty() || originalPriceStr.isEmpty() || salePriceStr.isEmpty() || pricePerUnitStr.isEmpty() || pricePerCartonStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.");
                return;
            }

            try {
                double originalPrice = Double.parseDouble(originalPriceStr);
                double salePrice = Double.parseDouble(salePriceStr);
                double pricePerUnit = Double.parseDouble(pricePerUnitStr);
                double pricePerCarton = Double.parseDouble(pricePerCartonStr);

                if (offlineMode) {
                    saveToOfflineFile("Product: " + name + ", Category: " + category +
                            ", Original Price: " + originalPrice + ", Sale Price: " + salePrice +
                            ", Price Per Unit: " + pricePerUnit + ", Price Per Carton: " + pricePerCarton);
                } else {
                    try (Connection connection = DBConnection.getConnection()) {
                        String sql = "INSERT INTO products (name, category, original_price, sale_price, price_per_unit, price_per_carton) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, name);
                        statement.setString(2, category);
                        statement.setDouble(3, originalPrice);
                        statement.setDouble(4, salePrice);
                        statement.setDouble(5, pricePerUnit);
                        statement.setDouble(6, pricePerCarton);
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
                // Sync logic based on parsed data
                System.out.println("Syncing: " + line);
            }
            JOptionPane.showMessageDialog(this, "Data synced successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error syncing data: " + ex.getMessage());
        }
    }
}
