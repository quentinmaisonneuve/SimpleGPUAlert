import controller.Daemon;
import controller.service.PropertyManager;

public class Main {

    // TODO :
    //  Before public repo : docker, readme/wiki, pre-built version for windows / linux / macos
    //  Limit the number of telegram notification per one second
    //  Make DISCORD, TWITTER, FACEBOOK, MESSENGER, DESKTOP (for windows, linux and macos) notifications
    //  Optional :
    //      Expose API REST with last data

    public static void main(String[] args) {

        // Loading properties
        PropertyManager.loadProperties(args);

        // Run the daemon
        new Daemon().run();
    }
}
