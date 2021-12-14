import controller.Daemon;
import controller.service.PropertyManager;

public class Main {

    public static void main(String[] args) {

        // Loading properties
        PropertyManager.loadProperties(args);

        // Run the daemon
        new Daemon().run();
    }
}
