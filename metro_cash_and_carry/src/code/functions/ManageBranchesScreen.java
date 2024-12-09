package code.functions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import code.config.DBConnection;
import java.sql.*;

public class ManageBranchesScreen extends JFrame {
    private JTable branchTable;
    private DefaultTableModel tableModel;
    public ManageBranchesScreen() {
        setTitle("Manage Branches");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // Branch Table
        tableModel = new DefaultTableModel(new String[]{"Branch ID", "Name", "City", "Active", "Address", "Phone", "Num Employees"}, 0);
        branchTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(branchTable);
        add(scrollPane, BorderLayout.CENTER);
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Branch");
        addButton.addActionListener(e -> openAddBranchDialog());
        JButton editButton = new JButton("Edit Branch");
        editButton.addActionListener(e -> openEditBranchDialog());
        JButton deleteButton = new JButton("Delete Branch");
        deleteButton.addActionListener(e -> deleteSelectedBranch());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        loadBranchData();
        setVisible(true);
    }
    private void loadBranchData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Branches";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("BranchID"),
                        rs.getString("Name"),
                        rs.getString("City"),
                        rs.getBoolean("Active"),
                        rs.getString("Address"),
                        rs.getString("Phone"),
                        rs.getInt("NumEmployees")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading branch data: " + ex.getMessage());
        }
    }

    private void openAddBranchDialog() {
        new BranchDialog(this, "Add Branch", null);
    }
    private void openEditBranchDialog() {
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a branch to edit.");
            return;
        }
        int branchId = (int) branchTable.getValueAt(selectedRow, 0);
        new BranchDialog(this, "Edit Branch", branchId);
    }
    private void deleteSelectedBranch() {
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a branch to delete.");
            return;
        }
        int branchId = (int) branchTable.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM Branches WHERE BranchID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, branchId);
            stmt.executeUpdate();
            loadBranchData();
            JOptionPane.showMessageDialog(this, "Branch deleted successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting branch: " + ex.getMessage());
        }
    }
}
