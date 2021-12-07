package controller.service.notification;

import java.awt.*;
import java.net.URI;

import controller.Daemon;
import controller.service.PropertyManager;
import model.GPUInfo;

public class DesktopNotificationService implements NotificationService {

    public final static String DESKTOP_TITLE_TEMPLATE = "DESKTOP_TITLE_TEMPLATE";
    public final static String DESKTOP_MESSAGE_TEMPLATE = "DESKTOP_MESSAGE_TEMPLATE";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        try {

            //Obtain only one instance of the SystemTray object
            SystemTray tray = SystemTray.getSystemTray();

            // Creating notification
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image);
            tray.add(trayIcon);

            // Adding listener to go to the url when the notification is clicked
            trayIcon.addActionListener(e -> {

                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {

                    try {

                        Desktop.getDesktop().browse(new URI(gpuInfo.getProductUrl()));

                    } catch (Exception ex) {

                        Daemon.logger.error(ex);
                    }
                }
            });

            // Display message
            trayIcon.displayMessage(String.format(PropertyManager.getProperty(DESKTOP_TITLE_TEMPLATE),
                    gpuInfo.getLocale().toString().toUpperCase(),
                    gpuInfo.getGpuName()),
                    String.format(PropertyManager.getProperty(DESKTOP_MESSAGE_TEMPLATE),gpuInfo.getProductUrl()),
                    TrayIcon.MessageType.INFO);

        } catch(Exception e) {

            Daemon.logger.error(e);
        }
    }
}
