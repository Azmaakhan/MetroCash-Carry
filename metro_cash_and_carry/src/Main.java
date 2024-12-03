import code.functions.SplashScreen;

import javax.swing.*;

import static code.config.DBConnection.createDatabase;
import static code.functions.SuperAdminSetup.*;

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