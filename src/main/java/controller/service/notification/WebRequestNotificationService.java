package controller.service.notification;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

import controller.Daemon;
import controller.service.PropertyManager;
import model.GPUInfo;
import model.HttpRequestType;

public class WebRequestNotificationService implements NotificationService {

    // Constants
    public static final String URL_TO_CALL = "URL_TO_CALL";
    public static final String REQUEST_TYPE = "REQUEST_TYPE";
    public static final String HEADERS = "HEADERS";
    public static final String REQUEST_BODY = "REQUEST_BODY";

    @Override
    public void sendNotification(GPUInfo gpuInfo) {

        String url = NotificationManager.formatString(PropertyManager.getProperty(URL_TO_CALL), gpuInfo, true);

        try {

            HttpRequestType httpRequestType = HttpRequestType.StringToHttpRequestType(PropertyManager.getProperty(REQUEST_TYPE));
            String requestBody = null;

            if (httpRequestType == null) {

                httpRequestType = HttpRequestType.GET;
            }

            if (httpRequestType.equals(HttpRequestType.POST) || httpRequestType.equals(HttpRequestType.PUT)) {

                requestBody = NotificationManager.formatString(PropertyManager.getProperty(REQUEST_BODY), gpuInfo, false);

                if (requestBody == null) {

                    requestBody = "";
                }
            }

            HttpClient client = HttpClient.newHttpClient();

            // Creating builder of request with url and headers
            HttpRequest.Builder builder = HttpRequest
                    .newBuilder()
                    .uri(URI.create(url))
                    .headers(Arrays.stream(PropertyManager.getProperty(HEADERS).split(","))
                            .map(String::trim)
                            .toArray(String[]::new));

            // Setting the request method
            switch (httpRequestType) {

                case GET -> builder = builder.GET();
                case PUT -> builder = builder.PUT(HttpRequest.BodyPublishers.ofString(requestBody));
                case POST -> builder = builder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
                default -> {}
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            Daemon.logger.info("Status code : ".concat(String.valueOf(response.statusCode())));
            Daemon.logger.debug("Body : ".concat(response.body()));

            if (!(200 <= response.statusCode() && response.statusCode() <= 206)) {

                Daemon.logger.warn("The status code is not 2XX");
                Daemon.logger.warn("Body : ".concat(response.body()));
            }

        } catch (IllegalArgumentException e) {

            Daemon.logger.error("Bad url or headers");
            Daemon.logger.error("URL : ".concat(url));
            Daemon.logger.error("HEADERS : ".concat(PropertyManager.getProperty(HEADERS)));

        } catch (IOException | InterruptedException e) {

            Daemon.logger.error(e);
        }
    }
}
