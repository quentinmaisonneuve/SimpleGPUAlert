import service.PropertyManager;

public class Main {

    // TODO :
    //  Make DISCORD, TWITTER, FACEBOOK, MESSENGER, DESKTOP (for windows, linux and macos) notifications
    //  Add a title template and a message template
    //  Get the response from telegram notification message
    //  Limit the number of telegram notification for one second
    //  Optional :
    //      Expose API REST with last data

    public static void main(String[] args) {

        // Loading properties
        PropertyManager.loadProperties(args);

        // Run the daemon
        new Daemon().run();
    }
}
