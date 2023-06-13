package test;

import com.google.gson.Gson;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private HttpClient httpClient;
    private Gson gson;

    @BeforeEach
    void start() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        httpClient = HttpClient.newHttpClient();
        gson = Managers.getGson();
    }

    @AfterEach
    void stop() {
        kvServer.stop();
        taskServer.stop();
    }

//    @Test
//    void handleTaskCreate() throws IOException, InterruptedException {
//        URI url = URI.create("http://localhost:8080/tasks/task/");
//        Task task = new Task("name", "description", Status.NEW);
//        String json = gson.toJson(task);
//        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
//        HttpRequest createRequest = HttpRequest.newBuilder()
//                .uri(url)
//                .POST(body)
//                .build();
//        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());
//        assertEquals(201, createResponse.statusCode());
//        assertEquals("1", createResponse.body());
//    }

    @Test
    void handleTaskCreateWithDate() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task(
                "name",
                "description",
                Status.NEW,
                30,
                LocalDateTime.of(2001, 10, 10, 10, 10)
        );
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());
        assertEquals("1", createResponse.body());
    }

    @Test
    void handleSubtask() {

    }

    @Test
    void handleEpic() {

    }

}
