package controller.service.notification;

import java.util.ArrayList;
import java.util.List;

import controller.Daemon;
import model.NotificationChannel;
import controller.service.PropertyManager;

public class NotificationFactory {

    public static List<NotificationService> getNotificationService() {

        List<NotificationService> notificationServices = new ArrayList<>();

        for (NotificationChannel notificationChannel : NotificationChannel.StringToNotificationChannel(PropertyManager.getProperty(Daemon.NOTIFICATION_CHANNELS))) {

            switch (notificationChannel) {
                case MAIL -> notificationServices.add(new MailNotificationService());
                case TELEGRAM -> notificationServices.add(new TelegramNotificationService());
                default -> {}
            }
        }

        return notificationServices;
    }
}
