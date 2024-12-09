package code.functions;

//import org.jfree.chart.*;
//import org.jfree.chart.plot.*;
//import org.jfree.data.category.*;

import javax.swing.*;
import java.awt.*;

public class ReportsScreen extends JFrame {
    public ReportsScreen() {
        setTitle("Reports");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JButton salesReportButton = new JButton("Generate Sales Report");
        salesReportButton.addActionListener(e -> generateSalesReport());
        add(salesReportButton, BorderLayout.CENTER);
        setVisible(true);
    }
    private void generateSalesReport() {
//        try (Connection conn = DBConnection.getConnection()) {
//            String query = "SELECT DATE(SaleDate) AS SaleDate, SUM(Subtotal) AS TotalSales FROM Sales GROUP BY DATE(SaleDate)";
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(query);
//
//            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//            while (rs.next()) {
//                dataset.addValue(rs.getDouble("TotalSales"), "Sales", rs.getDate("SaleDate"));
//            }
//
//            JFreeChart chart = ChartFactory.createLineChart(
//                    "Sales Report",
//                    "Date",
//                    "Total Sales",
//                    dataset,
//                    PlotOrientation.VERTICAL,
//                    true,
//                    true,
//                    false
//            );
//
//            ChartPanel chartPanel = new ChartPanel(chart);
//            JFrame chartFrame = new JFrame("Sales Report Chart");
//            chartFrame.setSize(800, 600);
//            chartFrame.add(chartPanel);
//            chartFrame.setVisible(true);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
//        }
    }
}
