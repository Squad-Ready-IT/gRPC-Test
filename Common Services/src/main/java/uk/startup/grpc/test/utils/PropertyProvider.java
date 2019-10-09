package uk.startup.grpc.test.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyProvider {

    private Properties store = new Properties();

    public PropertyProvider(String fileName) {
        try (InputStream is = PropertyProvider.class.getResourceAsStream(fileName)) {
            store.load(is);
        } catch (FileNotFoundException e) {
            System.out.println("Configuration file '" +
                    fileName + "' isn't found");
        } catch (Exception e) {
            System.out.println("Error reading of configuration file '" +
                    fileName + "': " + e.getMessage());
        }
    }

    public String getStringProperty(String key) {
        return store.getProperty(key);
    }

    public Integer getIntProperty(String key) {
        return Integer.parseInt(getStringProperty(key));
    }
}
