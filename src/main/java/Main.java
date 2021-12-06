import controller.Daemon;
import controller.service.PropertyManager;

public class Main {

    // TODO :
    //  Before public repo : docker, readme/wiki
    //  Make DISCORD, TWITTER, FACEBOOK, MESSENGER, WHATSAPP, DESKTOP (for windows, linux and macos) notifications
    //  Change thread sleep to : https://www.geeksforgeeks.org/how-to-temporarily-suspend-a-thread-in-java/
    //  Optional :
    //      Expose API REST with last data

    public static void main(String[] args) {

        // Loading properties
        PropertyManager.loadProperties(args);

        // Run the daemon
        new Daemon().run();
    }
}
