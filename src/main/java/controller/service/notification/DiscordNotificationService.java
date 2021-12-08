package controller.service.notification;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Flow;

import controller.Daemon;
import model.GPUInfo;

public class DiscordNotificationService implements NotificationService {

    // Constant
    public static final String DISCORD_TOKEN = "DISCORD_TOKEN";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://discord.com/api/v9/channels/00/messages"))
                    .header("Authorization", "Bot 00")
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"tts\":false,\"embeds\":[{\"title\":\"Hello, Embed!\",\"description\":\"This is an embedded message.\"}]}"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Daemon.logger.info("Status code : ".concat(String.valueOf(response.statusCode())));
            Daemon.logger.debug("Body : ".concat(response.body()));

            if (!(200 <= response.statusCode() && response.statusCode() <= 206)) {

                Daemon.logger.warn("The status code is not 2XX");
            }

        } catch (Exception e) {

            Daemon.logger.error(e);
        }
    }
}
