import controller.Daemon;
import controller.service.PropertyManager;

public class Main {

    // TODO :
    //  Add a timer when a gpu is marked as available !!!
    //  Before public repo : docker, readme/wiki, pre-built version for windows / linux / macos
    //  Make DISCORD, TWITTER, FACEBOOK, MESSENGER, DESKTOP (for windows, linux and macos) notifications
    //  Optional :
    //      Expose API REST with last data
    //      Limit the number of notification cf implement this for NotificationService : https://stackoverflow.com/questions/667508/whats-a-good-rate-limiting-algorithm

    public static void main(String[] args) {

        // Loading properties
        PropertyManager.loadProperties(args);

        // Run the daemon
        new Daemon().run();
    }
}
