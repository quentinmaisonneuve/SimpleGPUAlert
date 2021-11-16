package service.notification;

import data.GPUInfo;
import org.json.JSONObject;
import service.JSONManager;
import service.PropertyManager;

import java.util.Locale;

public class TelegramNotificationService implements NotificationService {

    public static final String TELEGRAM_API_LINK = "TELEGRAM_API_LINK";
    public static final String API_TOKEN = "API_TOKEN";
    public static final String CHAT_ID = "CHAT_ID";
    public static final String MAX_MESSAGE_PER_SECOND = "MAX_MESSAGE_PER_SECOND";
    public static final String TELEGRAM_MESSAGE_TEMPLATE = "TELEGRAM_MESSAGE_TEMPLATE";
    public static final String LINE_RETURN = "%0A";

    /**
     * Send telegram notification
     * @param gpuInfo Nvidia GPU informations
     */
    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        String urlString;

        String message = String.format(PropertyManager.properties.getProperty(TELEGRAM_MESSAGE_TEMPLATE),
                new Locale(PropertyManager.properties.getProperty(LOCALE).toUpperCase()),
                LINE_RETURN,
                gpuInfo.getGpuName(),
                LINE_RETURN,
                gpuInfo.getProductUrl());

        urlString = String.format(PropertyManager.properties.getProperty(TELEGRAM_API_LINK),
                PropertyManager.properties.getProperty(API_TOKEN),
                PropertyManager.properties.getProperty(CHAT_ID), message);

        JSONObject response = JSONManager.readJsonFromUrl(urlString.replace(" ", "%20"));

        if ((Boolean) response.get("ok")) {

            System.out.println("Telegram message sent successfully");

        } else {

            System.out.println("Telegram message not send for ".concat(gpuInfo.getGpuName().toString()));
            System.out.println("Error code : ".concat(String.valueOf(response.get("error_code"))));
            System.out.println("Error message : ".concat((String.valueOf(response.get("description")))));
        }
    }
}
