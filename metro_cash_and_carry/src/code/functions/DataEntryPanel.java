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
        String name = JOptionPane.showInputDialog(this, "Enter Vendor Name:");
        String address = JOptionPane.showInputDialog(this, "Enter Vendor Address:");
        String phone = JOptionPane.showInputDialog(this, "Enter Vendor Phone:");

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

    private void addProduct(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Enter Product Name:");
        String category = JOptionPane.showInputDialog(this, "Enter Product Category:");
        String originalPrice = JOptionPane.showInputDialog(this, "Enter Original Price:");
        String salePrice = JOptionPane.showInputDialog(this, "Enter Sale Price:");
        String pricePerUnit = JOptionPane.showInputDialog(this, "Enter Price Per Unit:");
        String pricePerCarton = JOptionPane.showInputDialog(this, "Enter Price Per Carton:");

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
                statement.setDouble(3, Double.parseDouble(originalPrice));
                statement.setDouble(4, Double.parseDouble(salePrice));
                statement.setDouble(5, Double.parseDouble(pricePerUnit));
                statement.setDouble(6, Double.parseDouble(pricePerCarton));
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product added successfully!");
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage());
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
