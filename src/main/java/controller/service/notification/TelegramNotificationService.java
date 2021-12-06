package controller.service.notification;

import org.json.JSONObject;

import controller.Daemon;
import controller.service.JSONManager;
import controller.service.PropertyManager;
import model.GPUInfo;

public class TelegramNotificationService implements NotificationService {

    public static final String TELEGRAM_API_LINK = "TELEGRAM_API_LINK";
    public static final String API_TOKEN = "API_TOKEN";
    public static final String CHAT_ID = "CHAT_ID";
    public static final String TELEGRAM_MESSAGE_TEMPLATE = "TELEGRAM_MESSAGE_TEMPLATE";
    public static final String LINE_RETURN = "%0A";

    /**
     * Send telegram notification
     * @param gpuInfo Nvidia GPU informations
     */
    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        String urlString;

        String message = String.format(PropertyManager.getProperty(TELEGRAM_MESSAGE_TEMPLATE),
                gpuInfo.getLocale().toString().toUpperCase(),
                LINE_RETURN,
                gpuInfo.getGpuName(),
                LINE_RETURN,
                gpuInfo.getProductUrl());

        urlString = String.format(PropertyManager.getProperty(TELEGRAM_API_LINK),
                PropertyManager.getProperty(API_TOKEN),
                PropertyManager.getProperty(CHAT_ID), message);

        JSONObject response = JSONManager.readJsonFromUrl(urlString.replace(" ", "%20").replace("+", "%2B"));

        if ((Boolean) response.get("ok")) {

           Daemon.logger.info("Telegram message sent successfully");

        } else {

            Daemon.logger.error("Telegram message not send for ".concat(gpuInfo.getGpuName().toString()));
            Daemon.logger.error("Error code : ".concat(String.valueOf(response.get("error_code"))));
            Daemon.logger.error("Error message : ".concat((String.valueOf(response.get("description")))));
        }
    }
}
