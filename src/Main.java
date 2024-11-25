import javax.swing.*;
import code.config.DBInitializer;
import code.config.OfflineHandler;
import code.screens.LoginScreen;
import code.screens.SplashScreen;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DBInitializer.initializeDB();
            OfflineHandler.setOfflineMode(false);
            new SplashScreen();
            new LoginScreen();
        });
    }
}