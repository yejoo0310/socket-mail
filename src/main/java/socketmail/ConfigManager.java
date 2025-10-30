package socketmail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties config = new Properties();
    private static final String SECRET_FILE_PATH = "src/main/resources/application-secret.properties";

    static {
        try(InputStream defaults = ConfigManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (defaults != null) {
                config.load(defaults);
            }
        } catch (IOException e) {
            System.err.println("Error loading application properties");
        }

        try(InputStream secrets = new FileInputStream(SECRET_FILE_PATH)) {
            config.load(secrets);
            System.out.println("Secrets loaded");
        } catch (IOException e) {
            System.err.println("Error loading application-secret properties");
        }
    }

    public static String getProperty(String key) {
        return config.getProperty(key);
    }
}
