package code.auth;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {
    public SplashScreen() {
        setTitle("Metro POS - Splash Screen");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel("SPLASH SCREEN", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel label2 = new JLabel("Loading Metro POS...", SwingConstants.CENTER);
        label2.setFont(new Font("Arial", Font.ITALIC, 20));

        add(label);
        add(label2);

        new Timer(3000, e -> {
            this.dispose();
        }).start();
        new LoginScreen().setVisible(true);
    }
}
