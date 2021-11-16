package service.notification;

import java.util.ArrayList;
import java.util.List;

import data.NotificationChannel;
import service.PropertyManager;

public class NotificationFactory {

    public static final String NOTIFICATION_CHANNELS = "NOTIFICATION_CHANNELS";

    public static List<NotificationService> getNotificationService() {

        List<NotificationService> notificationServices = new ArrayList<>();

        for (NotificationChannel notificationChannel : NotificationChannel.StringToNotificationChannel(PropertyManager.properties.getProperty(NOTIFICATION_CHANNELS))) {

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
