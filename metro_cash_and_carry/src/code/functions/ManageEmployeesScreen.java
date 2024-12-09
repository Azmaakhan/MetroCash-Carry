package code.functions;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import code.config.DBConnection;
import java.sql.*;

public class ManageEmployeesScreen extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private int branchId;
    public ManageEmployeesScreen(int branchId) {
        this.branchId = branchId;

        setTitle("Manage Employees");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Employee ID", "Name", "Role", "Email", "Salary"}, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(e -> openAddEmployeeDialog());
        JButton editButton = new JButton("Edit Employee");
        editButton.addActionListener(e -> openEditEmployeeDialog());
        JButton deleteButton = new JButton("Delete Employee");
        deleteButton.addActionListener(e -> deleteSelectedEmployee());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadEmployeeData();
        setVisible(true);
    }

    private void loadEmployeeData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT UserID, Name, Role, Email, Salary FROM Users WHERE BranchID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("UserID"),
                        rs.getString("Name"),
                        rs.getString("Role"),
                        rs.getString("Email"),
                        rs.getDouble("Salary")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage());
        }
    }

    private void openAddEmployeeDialog() {
        new EmployeeDialog(this, "Add Employee", branchId, null);
    }

    private void openEditEmployeeDialog() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
            return;
        }
        int employeeId = (int) employeeTable.getValueAt(selectedRow, 0);
        new EmployeeDialog(this, "Edit Employee", branchId, employeeId);
    }

    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }
        int employeeId = (int) employeeTable.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM Users WHERE UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
            loadEmployeeData();
            JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage());
        }
    }
}
