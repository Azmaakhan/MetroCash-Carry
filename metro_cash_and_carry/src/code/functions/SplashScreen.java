package code.functions;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {
    public SplashScreen() {
        setTitle("Metro POS - Splash Screen");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Loading Metro POS...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label);

        new Timer(3000, e -> {
            this.dispose();
        }).start();
        new LoginScreen().setVisible(true);
    }
}
