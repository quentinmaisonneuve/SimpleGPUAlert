package controller.service.notification;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import controller.Daemon;
import controller.service.PropertyManager;
import model.GPUInfo;
import model.NotificationChannel;

public class NotificationManager {

    // Constant
    public static final String TIMEOUT_NOTIFICATION_CHANNEL = "TIMEOUT_NOTIFICATION_CHANNEL";
    public static final String NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static List<NotificationChannel> listNotificationChannel;
    private static final Map<NotificationChannel, LocalDateTime> lastNotificationMap = new HashMap<>();
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(100);
    private static long timeoutNotificationChannel = -1;

    public static void sendNotification(NotificationChannel channel, GPUInfo gpuInfo) {

        NotificationService notificationService = null;

        switch (channel) {

            case MAIL -> notificationService = new MailNotificationService();
            case TELEGRAM -> notificationService = new TelegramNotificationService();
            case WEB_REQUEST -> notificationService = new WebRequestNotificationService();
            default -> {}
        }

        long delay = 0;

        if (lastNotificationMap.get(channel) != null) {

            Daemon.logger.debug("Last notification : ".concat(lastNotificationMap.get(channel).format(formatter)));
            Daemon.logger.debug("Now : ".concat(LocalDateTime.now().format(formatter)));
            Daemon.logger.debug("Difference : ".concat(String.valueOf(Duration.between(lastNotificationMap.get(channel), LocalDateTime.now()).toMillis())));

            delay = getTimeoutNotificationChannel() * 1000 - Duration.between(lastNotificationMap.get(channel), LocalDateTime.now()).toMillis();

            if (Duration.between(lastNotificationMap.get(channel), LocalDateTime.now()).toMillis() > 0) {

                lastNotificationMap.put(channel, LocalDateTime.now().plusNanos(delay * 1000000));

            } else {

                lastNotificationMap.put(channel, lastNotificationMap.get(channel).plusSeconds(getTimeoutNotificationChannel()));
            }

        } else {

            lastNotificationMap.put(channel, LocalDateTime.now());
        }

        // Setup the thread
        NotificationService finalNotificationService = notificationService;
        executor.schedule(() -> finalNotificationService.sendNotification(gpuInfo), delay, TimeUnit.MILLISECONDS);

        Daemon.logger.debug("Delay : ".concat(String.valueOf(delay)));
    }

    public static List<NotificationChannel> getListNotificationChannel() {

        if (listNotificationChannel == null) {

            listNotificationChannel = NotificationChannel.StringToNotificationChannel(PropertyManager.getProperty(NotificationManager.NOTIFICATION_CHANNEL));
        }

        return listNotificationChannel;
    }

    private static long getTimeoutNotificationChannel() {

        if (timeoutNotificationChannel < 0) {

            timeoutNotificationChannel = Long.parseLong(PropertyManager.getProperty(TIMEOUT_NOTIFICATION_CHANNEL));
        }

        return timeoutNotificationChannel;
    }
}
