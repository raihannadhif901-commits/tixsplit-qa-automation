package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                // Fallback ke config.properties.example jika file utama belum dibuat
                try (InputStream fallback = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties.example")) {
                    if (fallback != null) properties.load(fallback);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal membaca config.properties: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}