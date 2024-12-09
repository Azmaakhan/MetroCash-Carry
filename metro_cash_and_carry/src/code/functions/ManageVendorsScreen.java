package code.functions;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import code.config.DBConnection;
import java.sql.*;

public class ManageVendorsScreen extends JFrame {
    private JTable vendorTable;
    private DefaultTableModel tableModel;

    public ManageVendorsScreen() {
        setTitle("Manage Vendors");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        tableModel = new DefaultTableModel(new String[]{"Vendor ID", "Name", "Contact", "Address"}, 0);
        vendorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vendorTable);
        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Vendor");
        addButton.addActionListener(e -> openAddVendorDialog());
        JButton editButton = new JButton("Edit Vendor");
        editButton.addActionListener(e -> openEditVendorDialog());
        JButton deleteButton = new JButton("Delete Vendor");
        deleteButton.addActionListener(e -> deleteSelectedVendor());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadVendorData();
        setVisible(true);
    }

    private void loadVendorData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Vendors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("VendorID"),
                        rs.getString("Name"),
                        rs.getString("Contact"),
                        rs.getString("Address")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vendor data: " + ex.getMessage());
        }
    }

    private void openAddVendorDialog() {
        new VendorDialog(this, "Add Vendor", null);
    }

    private void openEditVendorDialog() {
        int selectedRow = vendorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vendor to edit.");
            return;
        }
        int vendorId = (int) vendorTable.getValueAt(selectedRow, 0);
        new VendorDialog(this, "Edit Vendor", vendorId);
    }

    private void deleteSelectedVendor() {
        int selectedRow = vendorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vendor to delete.");
            return;
        }
        int vendorId = (int) vendorTable.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM Vendors WHERE VendorID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, vendorId);
            stmt.executeUpdate();
            loadVendorData();
            JOptionPane.showMessageDialog(this, "Vendor deleted successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting vendor: " + ex.getMessage());
        }
    }
}
