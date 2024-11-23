import javax.swing.*;
import java.config.DBInitializer;
import java.config.OfflineHandler;
import java.screens.LoginScreen;
import java.screens.SplashScreen;

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