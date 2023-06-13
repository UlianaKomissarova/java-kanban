package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private URI uri;
    private String apiToken;
    private HttpRequest registration;

    public KVTaskClient(URI uri) throws IOException, InterruptedException {
        this.uri = uri;
        registration = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/register"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        apiToken = client.send(registration, HttpResponse.BodyHandlers.ofString()).body();
    }

    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(uri + "/save/" + key + "?API_TOKEN=" + apiToken))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/load/" + key + "?API_TOKEN=" + apiToken))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
