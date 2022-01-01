package controller.service.notification;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.json.JSONObject;

import controller.Daemon;
import controller.service.PropertyManager;
import model.GPUInfo;
import model.discord.DiscordMessage;
import model.discord.Embed;

public class DiscordNotificationService implements NotificationService {

    // Constant
    public static final String DISCORD_MESSAGE_API_ENDPOINT = "DISCORD_MESSAGE_API_ENDPOINT";
    public static final String DISCORD_BOT_TOKEN = "DISCORD_BOT_TOKEN";
    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final String DISCORD_TITLE_TEMPLATE = "DISCORD_TITLE_TEMPLATE";
    public static final String DISCORD_MESSAGE_TEMPLATE = "DISCORD_MESSAGE_TEMPLATE";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        try {

            // Discord message
            DiscordMessage discordMessage = new DiscordMessage();
            Embed embed = new Embed();
            embed.setTitle(NotificationManager.formatString(PropertyManager.getProperty(DISCORD_TITLE_TEMPLATE), gpuInfo, false));
            embed.setUrl(gpuInfo.getProductUrl());
            embed.setDescription(NotificationManager.formatString(PropertyManager.getProperty(DISCORD_MESSAGE_TEMPLATE), gpuInfo, false));
            discordMessage.setEmbeds(List.of(embed));

            // Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(PropertyManager.getProperty(DISCORD_MESSAGE_API_ENDPOINT),PropertyManager.getProperty(CHANNEL_ID))))
                    .header("Authorization", String.format("Bot %s", PropertyManager.getProperty(DISCORD_BOT_TOKEN)))
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new JSONObject(discordMessage).toString()))
                    .build();

            // Response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Daemon.logger.info("Status code : ".concat(String.valueOf(response.statusCode())));
            Daemon.logger.debug("Body : ".concat(response.body()));

            if (!(200 <= response.statusCode() && response.statusCode() <= 206)) {

                Daemon.logger.warn("The status code is not 2XX");
                Daemon.logger.warn("Body : ".concat(response.body()));

            } else {

                Daemon.logger.info("Telegram message sent successfully");
            }

        } catch (Exception e) {

            Daemon.logger.error(e);
        }
    }
}
