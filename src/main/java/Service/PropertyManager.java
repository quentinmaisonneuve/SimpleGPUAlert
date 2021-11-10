package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.shared.utils.StringUtils;

/**
 * Manage the property file and import it
 */
public class PropertyManager {

    // Properties object
    public static Properties properties;

    // Constants
    public static final String INCORRECT_PROPERTY_FILE = "Properties file is not correctly formatted or file doesn't exist. You can either put it in the executable's folder with the name '%s' or pass the path in argument.";
    public static final String NAME_PROPERTY_FILE = "configuration.properties";

    /**
     * Load propertie from file
     * @param path Path of the property file
     * @return properties if reading file exist and is correct
     */
    private static Properties loadPropertiesFromFile(String path)  {

        Properties properties = null;

        try (InputStream input = new FileInputStream(path)) {

            properties = new Properties();
            properties.load(input);

        } catch (IOException e) {

            e.printStackTrace();
        }

        return properties;
    }

    /**
     * Load properties from either path from args or from configuration.properties file if exist
     * @param args Argmuments of the main program
     */
    public static void loadProperties(String[] args) {

        // Try with the args
        if (args.length > 0 && StringUtils.isNotBlank(args[0])) {

            properties = PropertyManager.loadPropertiesFromFile(args[0]);
        }

        // Try within the folder
        if (properties == null) {

            properties = PropertyManager.loadPropertiesFromFile("./".concat(NAME_PROPERTY_FILE));
        }

        // Log error message
        if (properties == null) {

            System.out.println(String.format(INCORRECT_PROPERTY_FILE, NAME_PROPERTY_FILE));
        }
    }
}
