package controller.service;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Manage JSON from URL
 */
public class JSONManager {

    /**
     * Parse JSON from an URL
     * @param url URL
     * @return JSONObjetct parsed
     */
    public static JSONObject readJsonFromUrl(String url)  {

        JSONObject json = null;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = null;

        try {

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
        }

        if (response != null) {

            json = new JSONObject(response.body());
        }

        return json;
    }
}
