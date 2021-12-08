package controller.service.notification;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import controller.Daemon;
import controller.service.PropertyManager;
import model.GPUInfo;

public class WebRequestNotificationService implements NotificationService {

    // Constant
    public static final String URL_TO_CALL = "URL_TO_CALL";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        String url = NotificationManager.formatString(PropertyManager.getProperty(URL_TO_CALL), gpuInfo, true);

        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response;

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Daemon.logger.info("Status code : ".concat(String.valueOf(response.statusCode())));
            Daemon.logger.debug("Body : ".concat(response.body()));

            if (!(200 <= response.statusCode() && response.statusCode() <= 206)) {

                Daemon.logger.warn("The status code is not 2XX");
            }

        } catch (IllegalArgumentException e) {

            Daemon.logger.error("Bad url");
            Daemon.logger.error("URL : ".concat(url));

        } catch (IOException | InterruptedException e) {

            Daemon.logger.error(e);
        }
    }
}
