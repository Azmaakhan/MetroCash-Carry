package code.functions;

import javax.swing.*;

public class SuperAdminScreen extends JFrame {
    public SuperAdminScreen() {
        setTitle("Super Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JButton manageBranches = new JButton("Manage Branches");
        manageBranches.addActionListener(e -> new ManageBranchesScreen());
        add(manageBranches);
        JButton viewReports = new JButton("View Reports");
        viewReports.addActionListener(e -> new ReportsScreen());
        add(viewReports);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setVisible(true);
    }
}
