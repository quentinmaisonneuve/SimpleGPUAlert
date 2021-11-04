import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {

    public static Properties loadPropertiesFromFile(String path)  {

        Properties prop = null;

        try (InputStream input = new FileInputStream(path)) {

            prop = new Properties();
            prop.load(input);

        } catch (IOException e) {

            e.printStackTrace();
        }

        return prop;
    }
}
