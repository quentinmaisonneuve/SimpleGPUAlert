package controller.service.notification;

import java.util.ArrayList;
import java.util.List;

import controller.Daemon;
import model.NotificationChannel;
import controller.service.PropertyManager;

public class NotificationFactory {

    public static List<NotificationService> getNotificationService() {

        List<NotificationService> notificationServices = new ArrayList<>();

        for (NotificationChannel notificationChannel : NotificationChannel.StringToNotificationChannel(PropertyManager.getProperty(Daemon.NOTIFICATION_CHANNEL))) {

            switch (notificationChannel) {
                case MAIL -> notificationServices.add(new MailNotificationService());
                case TELEGRAM -> notificationServices.add(new TelegramNotificationService());
                case WEB_REQUEST -> notificationServices.add(new WebRequestNotificationService());
                default -> {}
            }
        }

        return notificationServices;
    }
}
