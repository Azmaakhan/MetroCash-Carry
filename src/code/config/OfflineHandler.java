package code.config;

import java.io.*;

public class OfflineHandler {
    private static final String DATA_FILE = "data.txt";
    private static final String FLAG_FILE = "status.txt";

    public static void saveOfflineData(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(data + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving offline data: " + e.getMessage());
        }
    }

    public static boolean isOfflineMode() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FLAG_FILE))) {
            String status = reader.readLine();
            return "true".equalsIgnoreCase(status);
        } catch (IOException e) {
            return false; // Default to online mode
        }
    }

    public static void setOfflineMode(boolean status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FLAG_FILE))) {
            writer.write(String.valueOf(status));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error updating offline mode status: " + e.getMessage());
        }
    }
}
