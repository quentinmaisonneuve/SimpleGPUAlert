import service.PropertyManager;

public class Main {

    // TODO :
    //  Make desktop notification for windows, linux and macos
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
