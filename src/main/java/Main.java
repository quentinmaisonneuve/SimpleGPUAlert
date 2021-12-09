import controller.Daemon;
import controller.service.PropertyManager;

public class Main {

    // TODO :
    //  Before public repo : docker, readme/wiki
    //  Make INSTAGRAM, FACEBOOK, MESSENGER, WHATSAPP notifications
    //  Add POST/PUT configuration for web request notification
    //  Expose API REST with last data

    public static void main(String[] args) {

        // Loading properties
        PropertyManager.loadProperties(args);

        // Run the daemon
        new Daemon().run();
    }
}
