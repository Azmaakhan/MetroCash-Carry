package code.functions;

import code.auth.LoginScreen;
import code.config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SuperAdminPanel extends JFrame {
    public SuperAdminPanel() {
        setTitle("Metro POS - Super Admin");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton createBranchButton = new JButton("Create Branch");
        createBranchButton.addActionListener(this::createBranch);

        JButton reportsButton = new JButton("View Reports");
        reportsButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Reports Module Coming Soon!");
        });
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener((ActionEvent e) -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        JPanel panel = new JPanel();
        panel.add(createBranchButton);
        panel.add(reportsButton);
        panel.add(logoutButton);

        add(panel);
    }

    private void createBranch(ActionEvent e) {
        // Show input dialog with multiple fields using a custom panel
        JPanel panel = new JPanel(new GridLayout(0, 2));

        // Branch fields
        JTextField branchCodeField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField cityField = new JTextField(15);
        JTextField addressField = new JTextField(15);
        JTextField phoneField = new JTextField(15);

        // Manager (employee) fields
        JTextField managerNameField = new JTextField(15);
        JTextField managerEmailField = new JTextField(15);
        JPasswordField managerPasswordField = new JPasswordField(15);
        JTextField managerSalaryField = new JTextField(15);

        // Add all fields to the panel
        panel.add(new JLabel("Branch Code:"));
        panel.add(branchCodeField);
        panel.add(new JLabel("Branch Name:"));
        panel.add(nameField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);

        // Manager details
        panel.add(new JLabel("Manager Name:"));
        panel.add(managerNameField);
        panel.add(new JLabel("Manager Email:"));
        panel.add(managerEmailField);
        panel.add(new JLabel("Manager Password:"));
        panel.add(managerPasswordField);
        panel.add(new JLabel("Manager Salary:"));
        panel.add(managerSalaryField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Branch and Manager Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String branchCode = branchCodeField.getText();
            String name = nameField.getText();
            String city = cityField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();

            String managerName = managerNameField.getText();
            String managerEmail = managerEmailField.getText();
            String managerPassword = new String(managerPasswordField.getPassword());
            double managerSalary = Double.parseDouble(managerSalaryField.getText());

            // Insert branch into the branches table
            try (Connection connection = DBConnection.getConnection()) {
                // Insert branch details
                String sqlBranch = "INSERT INTO branches (branch_code, name, city, active, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement branchStmt = connection.prepareStatement(sqlBranch);
                branchStmt.setString(1, branchCode);
                branchStmt.setString(2, name);
                branchStmt.setString(3, city);
                branchStmt.setBoolean(4, true);  // Branch is active by default
                branchStmt.setString(5, address);
                branchStmt.setString(6, phone);
                branchStmt.executeUpdate();

                // Insert manager (employee) into employees table
                String sqlEmployee = "INSERT INTO employees (name, emp_no, email, password, branch_code, salary, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement employeeStmt = connection.prepareStatement(sqlEmployee);
                employeeStmt.setString(1, managerName);
                employeeStmt.setString(2, "Manager_" + branchCode);
                employeeStmt.setString(3, managerEmail);
                employeeStmt.setString(4, managerPassword);
                employeeStmt.setString(5, branchCode);
                employeeStmt.setDouble(6, managerSalary);
                employeeStmt.setString(7, "BranchManager");
                employeeStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Branch and Manager Created Successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error Creating Branch and Manager: " + ex.getMessage());
            }
        }
    }

    private void viewReports(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        String[] reportOptions = {"Today", "Weekly", "Monthly", "Yearly", "Specify Range"};
        JComboBox<String> reportTypeComboBox = new JComboBox<>(reportOptions);

        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        JTextField startDateField = new JTextField(15);
        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        JTextField endDateField = new JTextField(15);

        panel.add(new JLabel("Select Report Type:"));
        panel.add(reportTypeComboBox);
        panel.add(startDateLabel);
        panel.add(startDateField);
        panel.add(endDateLabel);
        panel.add(endDateField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Generate Report", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String reportType = (String) reportTypeComboBox.getSelectedItem();
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();

            generateReport(reportType, startDate, endDate);
        }
    }

    private void generateReport(String reportType, String startDate, String endDate) {
        String sql = "";
        switch (reportType) {
            case "Today":
                sql = "SELECT * FROM sales WHERE DATE(sale_date) = CURDATE()";
                break;
            case "Weekly":
                sql = "SELECT * FROM sales WHERE WEEK(sale_date) = WEEK(CURDATE())";
                break;
            case "Monthly":
                sql = "SELECT * FROM sales WHERE MONTH(sale_date) = MONTH(CURDATE())";
                break;
            case "Yearly":
                sql = "SELECT * FROM sales WHERE YEAR(sale_date) = YEAR(CURDATE())";
                break;
            case "Specify Range":
                if (startDate.isEmpty() || endDate.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Start Date and End Date must be provided!");
                    return;
                }
                sql = "SELECT * FROM sales WHERE sale_date BETWEEN ? AND ?";
                break;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (reportType.equals("Specify Range")) {
                statement.setString(1, startDate);
                statement.setString(2, endDate);
            }

            ResultSet resultSet = statement.executeQuery();
            double totalSales = 0;
            double totalProfit = 0;
            double remainingStock = 0;

            while (resultSet.next()) {
                double saleAmount = resultSet.getDouble("sale_amount");
                double profit = resultSet.getDouble("profit");
                double stock = resultSet.getDouble("remaining_stock");

                totalSales += saleAmount;
                totalProfit += profit;
                remainingStock += stock;

                dataset.addValue(saleAmount, "Sales", resultSet.getDate("sale_date").toString());
                dataset.addValue(profit, "Profit", resultSet.getDate("sale_date").toString());
                dataset.addValue(stock, "Stock", resultSet.getDate("sale_date").toString());
            }

            JOptionPane.showMessageDialog(this, String.format(
                    "Report Summary:\nTotal Sales: %.2f\nTotal Profit: %.2f\nRemaining Stock: %.2f",
                    totalSales, totalProfit, remainingStock
            ));

            JFreeChart chart = ChartFactory.createBarChart(
                    "Sales, Profit, and Stock Report",
                    "Date",
                    "Amount",
                    dataset
            );

            JFrame chartFrame = new JFrame("Report Chart");
            chartFrame.setSize(800, 600);
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(new ChartPanel(chart));
            chartFrame.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        }
    }
}
