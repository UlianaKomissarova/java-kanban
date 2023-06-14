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

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private HttpClient httpClient;
    private Gson gson;

    String tasksPath = "http://localhost:8080/tasks/task";
    String subtasksPath = "http://localhost:8080/tasks/subtask";
    String epicsPath = "http://localhost:8080/tasks/epic";
    String epicSubtasksPath = "http://localhost:8080/tasks/subtask/epic";
    String historyPath = "http://localhost:8080/tasks/history";
    String priorityOrDeleteAllTasksPath = "http://localhost:8080/tasks";

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

    @Test
    void createTask() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
        Task task = new Task(
                "name",
                "description",
                Status.NEW
        );

        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createRequest = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, createResponse.statusCode());
        assertEquals("1", createResponse.body());
    }

    @Test
    void createTaskWithDate() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
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
                .POST(body)
                .uri(url)
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, createResponse.statusCode());
        assertEquals("1", createResponse.body());
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
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
                .POST(body)
                .uri(url)
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        URI url1 = URI.create(tasksPath + "/?id=1");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url1)
                .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertEquals("\"name\": \"name\",\n" +
                "\t\"description\": \"description\",\n" +
                "\t\"id\": 1,\n" +
                "\t\"status\": \"NEW\",\n" +
                "\t\"type\": \"TASK\",\n" +
                "\t\"duration\": 30\n" +
                "\t\"startTime\": \"2001-10-10 10:10\"",
                getResponse.body()
        );
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
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
                .POST(body)
                .uri(url)
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertEquals("tasks.Task{name='test1', description='test1', id='1', status='NEW', type='TASK', " +
                        "duration='124', startTime='2001-10-10T10:10'}",
                getResponse.body()
        );
    }

    @Test
    void deleteTasks() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
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
                .POST(body)
                .uri(url)
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().isEmpty());
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
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
                .POST(body)
                .uri(url)
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        URI url1 = URI.create(tasksPath + "/?id=1");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url1)
                .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().isEmpty());
    }

    @Test
    void createSubtask() {

    }

    @Test
    void createEpic() {

    }

}
