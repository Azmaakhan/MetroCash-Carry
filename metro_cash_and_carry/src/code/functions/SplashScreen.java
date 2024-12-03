package code.functions;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {
    public SplashScreen() {
        setUndecorated(true);
        setSize(600, 300);
        setLocationRelativeTo(null);
        JLabel label = new JLabel("Metro POS System Loading...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label);

        // Simulate loading
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        add(progressBar, BorderLayout.SOUTH);

        setVisible(true);
        try {
            Thread.sleep(3000); // Simulate loading time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dispose();
    }
}
