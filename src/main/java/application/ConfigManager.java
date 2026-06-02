package application;

import java.io.*;

public class ConfigManager {
    private static final String FILE_NAME = "config.ser";

    public static void save(AppConfig config) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AppConfig load() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (AppConfig) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new AppConfig();
    }
}