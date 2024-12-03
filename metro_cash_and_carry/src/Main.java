import code.functions.SplashScreen;

import javax.swing.*;

import static code.functions.SuperAdminSetup.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            addAdminInterferance();
            initializeSystem();
//            SplashScreen splash = new SplashScreen();
//            splash.setVisible(true);
        });
    }
}