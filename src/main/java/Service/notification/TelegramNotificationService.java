package service.notification;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import data.GPUInfo;
import service.PropertyManager;

public class TelegramNotificationService implements NotificationService {

    public static final String TELEGRAM_API_LINK = "TELEGRAM_API_LINK";
    public static final String LOCALE = "LOCALE";
    public static final String API_TOKEN = "API_TOKEN";
    public static final String CHAT_ID = "CHAT_ID";
    public static final String MAX_MESSAGE_PER_SECOND = "MAX_MESSAGE_PER_SECOND";

    /**
     * Send telegram notification
     * @param gpuInfo Nvidia GPU informations
     */
    public void sendNotification(GPUInfo gpuInfo) {

        String urlString = PropertyManager.properties.getProperty(TELEGRAM_API_LINK);
        String message = "Nvidia " + new Locale(PropertyManager.properties.getProperty(LOCALE)).toString().toUpperCase() +
                "%0AFE Nvidia GeForce RTX " + gpuInfo.getGpuName() +
                "%0A" + gpuInfo.getProductUrl();

        urlString = String.format(urlString, PropertyManager.properties.getProperty(API_TOKEN), PropertyManager.properties.getProperty(CHAT_ID), message);

        try {

            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
