package controller.service.notification;

import java.util.ArrayList;
import java.util.List;

import controller.Daemon;
import model.NotificationChannel;
import controller.service.PropertyManager;

public class NotificationFactory {

    public static List<NotificationService> getNotificationService() {

        List<NotificationService> notificationServices = new ArrayList<>();

        for (NotificationChannel notificationChannel : NotificationChannel.StringToNotificationChannel(PropertyManager.properties.getProperty(Daemon.NOTIFICATION_CHANNELS))) {

            switch (notificationChannel) {

                case MAIL :
                    notificationServices.add(new MailNotificationService());
                    break;

                case TELEGRAM:
                    notificationServices.add(new TelegramNotificationService());
                    break;

                default:
            }
        }

        return notificationServices;
    }
}
