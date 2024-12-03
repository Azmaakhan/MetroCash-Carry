import code.auth.SplashScreen;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
//            createDatabase();
//            initializeSystem();
//            addAdminInterferance();
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
        });
    }
}