package test;

import com.google.gson.Gson;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import test.mock.SubtaskMock;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        taskServer.getTaskManager().resetTaskIdCounter();
    }

    @AfterEach
    void stop() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void createTaskWithoutDate() throws IOException, InterruptedException {
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
        assertTrue(createResponse.body().matches("\\d+"));
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
        HttpResponse<String> response = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().matches("\\d+"));
    }

    @Test
    void createSubtaskWithDateAndEpic() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(
            "name",
            "description",
            Status.NEW,
            30,
            LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);

        Epic epic = new Epic(
            1,
            "name",
            "description",
            Status.NEW,
            subtasks
        );

        subtask.setEpicId(epic.getId());

        URI epicUri = URI.create(epicsPath);
        String epicJson = gson.toJson(epic);
        final HttpRequest.BodyPublisher epicBody = HttpRequest.BodyPublishers.ofString(epicJson);
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
            .POST(epicBody)
            .uri(epicUri)
            .build();
        HttpResponse<String> epicResponse = httpClient.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, epicResponse.statusCode());
        assertTrue(epicResponse.body().matches("\\d+"));

        URI subtaskUri = URI.create(subtasksPath);
        SubtaskMock mock = new SubtaskMock(Integer.parseInt(epicResponse.body()), subtask);
        String subtaskJson = gson.toJson(mock);
        final HttpRequest.BodyPublisher subtaskBody = HttpRequest.BodyPublishers.ofString(subtaskJson);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
            .POST(subtaskBody)
            .uri(subtaskUri)
            .build();
        HttpResponse<String> subtaskResponse = httpClient.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subtaskResponse.statusCode());
        assertTrue(subtaskResponse.body().matches("\\d+"));
    }

    @Test
    void updateTaskWithDate() throws IOException, InterruptedException {
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
        HttpResponse<String> response = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        Task taskUpd = new Task(
            "upd",
            "upd",
            Status.DONE,
            20,
            LocalDateTime.of(2002, 10, 10, 10, 10)
        );
        taskUpd.setId(Integer.valueOf(response.body()));

        String jsonUpd = gson.toJson(taskUpd);
        URI urlUpd = URI.create(tasksPath + "/?id=" + taskUpd.getId());
        final HttpRequest.BodyPublisher bodyUpd = HttpRequest.BodyPublishers.ofString(jsonUpd);
        HttpRequest requestUpd = HttpRequest.newBuilder()
            .POST(bodyUpd)
            .uri(urlUpd)
            .build();
        HttpResponse<String> responseUpd = httpClient.send(requestUpd, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseUpd.statusCode());
        assertTrue(Boolean.parseBoolean(responseUpd.body()));
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        URI url = URI.create(subtasksPath);
        Subtask subtask = new Subtask(
            "name",
            "description",
            Status.NEW,
            30,
            LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);

        Epic epic = new Epic(
            1,
            "name",
            "description",
            Status.NEW,
            subtasks
        );

        subtask.setEpicId(epic.getId());

        URI epicUri = URI.create(epicsPath);
        String epicJson = gson.toJson(epic);
        final HttpRequest.BodyPublisher epicBody = HttpRequest.BodyPublishers.ofString(epicJson);
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
            .POST(epicBody)
            .uri(epicUri)
            .build();
        HttpResponse<String> epicResponse = httpClient.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, epicResponse.statusCode());
        assertTrue(epicResponse.body().matches("\\d+"));

        URI subtaskUri = URI.create(subtasksPath);
        SubtaskMock mock = new SubtaskMock(Integer.parseInt(epicResponse.body()), subtask);
        String subtaskJson = gson.toJson(mock);
        final HttpRequest.BodyPublisher subtaskBody = HttpRequest.BodyPublishers.ofString(subtaskJson);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
            .POST(subtaskBody)
            .uri(subtaskUri)
            .build();
        HttpResponse<String> subtaskResponse = httpClient.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        Subtask subtaskUpd = new Subtask(
            "upd",
            "upd",
            Status.DONE,
            20,
            LocalDateTime.of(2002, 10, 10, 10, 10)
        );
        subtaskUpd.setId(Integer.valueOf(subtaskResponse.body()));

        SubtaskMock mockUpd = new SubtaskMock(Integer.parseInt(epicResponse.body()), subtaskUpd);
        String jsonUpd = gson.toJson(mockUpd);
        URI urlUpd = URI.create(subtasksPath + "/?id=" + subtaskUpd.getId());
        final HttpRequest.BodyPublisher bodyUpd = HttpRequest.BodyPublishers.ofString(jsonUpd);
        HttpRequest requestUpd = HttpRequest.newBuilder()
            .POST(bodyUpd)
            .uri(urlUpd)
            .build();
        HttpResponse<String> responseUpd = httpClient.send(requestUpd, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseUpd.statusCode());
        assertTrue(Boolean.parseBoolean(responseUpd.body()));
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(
            1,
            "name",
            "description",
            Status.NEW,
            new ArrayList<>()
        );

        URI epicUri = URI.create(epicsPath);
        String epicJson = gson.toJson(epic);
        final HttpRequest.BodyPublisher epicBody = HttpRequest.BodyPublishers.ofString(epicJson);
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
            .POST(epicBody)
            .uri(epicUri)
            .build();
        HttpResponse<String> epicResponse = httpClient.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        Epic epicUpd = new Epic(
            1,
            "upd",
            "upd",
            Status.DONE,
            new ArrayList<>()
        );
        epicUpd.setId(Integer.valueOf(epicResponse.body()));

        URI epicUriUpd = URI.create(epicsPath + "/?id=" + epicUpd.getId());
        String epicJsonUpd = gson.toJson(epicUpd);
        final HttpRequest.BodyPublisher epicBodyUpd = HttpRequest.BodyPublishers.ofString(epicJsonUpd);
        HttpRequest createEpicRequestUpd = HttpRequest.newBuilder()
            .POST(epicBodyUpd)
            .uri(epicUriUpd)
            .build();
        HttpResponse<String> epicResponseUpd = httpClient.send(createEpicRequestUpd, HttpResponse.BodyHandlers.ofString());


        assertEquals(201, epicResponse.statusCode());
        assertTrue(Boolean.parseBoolean(epicResponseUpd.body()));
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

        task.setId(Integer.valueOf(createResponse.body()));

        URI url1 = URI.create(tasksPath + "/?id=" + task.getId());
        HttpRequest getRequest = HttpRequest.newBuilder()
            .GET()
            .uri(url1)
            .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertEquals(gson.toJson(task), getResponse.body());
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
        task.setId(Integer.valueOf(createResponse.body()));

        HttpRequest getRequest = HttpRequest.newBuilder()
            .GET()
            .uri(url)
            .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        ArrayList<String> list = new ArrayList<>();
        list.add(task.toString());

        assertEquals(200, getResponse.statusCode());
        assertEquals(gson.toJson(list), getResponse.body());
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        URI url = URI.create(subtasksPath);
        Subtask subtask = new Subtask(
            "name",
            "description",
            Status.NEW,
            30,
            LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);

        Epic epic = new Epic(
            1,
            "name",
            "description",
            Status.NEW,
            subtasks
        );

        subtask.setEpicId(epic.getId());

        URI epicUri = URI.create(epicsPath);
        String epicJson = gson.toJson(epic);
        final HttpRequest.BodyPublisher epicBody = HttpRequest.BodyPublishers.ofString(epicJson);
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
            .POST(epicBody)
            .uri(epicUri)
            .build();
        HttpResponse<String> epicResponse = httpClient.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI subtaskUri = URI.create(subtasksPath);
        SubtaskMock mock = new SubtaskMock(Integer.parseInt(epicResponse.body()), subtask);
        String subtaskJson = gson.toJson(mock);
        final HttpRequest.BodyPublisher subtaskBody = HttpRequest.BodyPublishers.ofString(subtaskJson);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
            .POST(subtaskBody)
            .uri(subtaskUri)
            .build();
        HttpResponse<String> subtaskResponse = httpClient.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
            .GET()
            .uri(url)
            .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        subtask.setId(Integer.valueOf(subtaskResponse.body()));
        ArrayList<String> list = new ArrayList<>();
        list.add(subtask.toString());

        assertEquals(200, getResponse.statusCode());
        assertEquals(gson.toJson(list), getResponse.body());
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

        task.setId(Integer.valueOf(createResponse.body()));

        URI url1 = URI.create(tasksPath + "/?id=" + task.getId());
        HttpRequest getRequest = HttpRequest.newBuilder()
            .DELETE()
            .uri(url1)
            .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().isEmpty());
    }

    @Test
    void deleteTaskByIncorrectId() throws IOException, InterruptedException {
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

        task.setId(Integer.valueOf(createResponse.body()));

        URI url1 = URI.create(tasksPath + "/?id=" + 5);
        HttpRequest getRequest = HttpRequest.newBuilder()
            .DELETE()
            .uri(url1)
            .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
        Task task1 = new Task(
            "name",
            "description",
            Status.NEW,
            30,
            LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createRequest = HttpRequest.newBuilder()
            .POST(body)
            .uri(url)
            .build();
        HttpResponse<String> response1 = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task(
            "name",
            "description",
            Status.NEW,
            30,
            LocalDateTime.of(2002, 10, 10, 10, 10)
        );

        String json2 = gson.toJson(task2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest createRequest2 = HttpRequest.newBuilder()
            .POST(body2)
            .uri(url)
            .build();
        HttpResponse<String> response2 = httpClient.send(createRequest2, HttpResponse.BodyHandlers.ofString());

        URI priorityUri = URI.create(priorityOrDeleteAllTasksPath);
        HttpRequest getRequest = HttpRequest.newBuilder()
            .GET()
            .uri(priorityUri)
            .build();
        HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        task1.setId(Integer.valueOf(response1.body()));
        task2.setId(Integer.valueOf(response2.body()));
        ArrayList<Task> prioritizedTasks = new ArrayList<>();
        prioritizedTasks.add(task1);
        prioritizedTasks.add(task2);
        String expectedResponse = gson.toJson(prioritizedTasks);

        assertEquals(200, response.statusCode());
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
        Task task1 = new Task(
            "name",
            "description",
            Status.NEW,
            30,
            LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createRequest = HttpRequest.newBuilder()
            .POST(body)
            .uri(url)
            .build();
        HttpResponse<String> response1 = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task(
            "name",
            "description",
            Status.NEW,
            30,
            LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        String json2 = gson.toJson(task2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest createRequest2 = HttpRequest.newBuilder()
            .POST(body2)
            .uri(url)
            .build();
        HttpResponse<String> response2 = httpClient.send(createRequest2, HttpResponse.BodyHandlers.ofString());

        task1.setId(Integer.valueOf(response1.body()));

        URI url1 = URI.create(tasksPath + "/?id=" + task1.getId());
        HttpRequest getRequest = HttpRequest.newBuilder()
            .GET()
            .uri(url1)
            .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        task2.setId(Integer.valueOf(response2.body()));

        URI url2 = URI.create(tasksPath + "/?id=" + task2.getId());
        HttpRequest getRequest2 = HttpRequest.newBuilder()
            .GET()
            .uri(url2)
            .build();
        HttpResponse<String> getResponse2 = httpClient.send(getRequest2, HttpResponse.BodyHandlers.ofString());

        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(task1);
        expectedHistory.add(task2);

        URI historyUri = URI.create(historyPath);
        HttpRequest getHistoryRequest = HttpRequest.newBuilder()
            .GET()
            .uri(historyUri)
            .build();
        HttpResponse<String> getHistoryResponse = httpClient.send(getHistoryRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getHistoryResponse.statusCode());
        assertEquals(gson.toJson(expectedHistory), getHistoryResponse.body());
    }

    @Test
    void deleteAll() throws IOException, InterruptedException {
        URI url = URI.create(tasksPath);
        Task task1 = new Task(
            "name",
            "description",
            Status.NEW,
            30,
            LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest createRequest = HttpRequest.newBuilder()
            .POST(body)
            .uri(url)
            .build();
        HttpResponse<String> taskResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        Epic epic = new Epic(
            1,
            "name",
            "description",
            Status.NEW,
            new ArrayList<>()
        );

        URI epicUri = URI.create(epicsPath);
        String epicJson = gson.toJson(epic);
        final HttpRequest.BodyPublisher epicBody = HttpRequest.BodyPublishers.ofString(epicJson);
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
            .POST(epicBody)
            .uri(epicUri)
            .build();
        HttpResponse<String> epicResponse = httpClient.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        URI priorityUri = URI.create(priorityOrDeleteAllTasksPath);
        HttpRequest getRequest = HttpRequest.newBuilder()
            .DELETE()
            .uri(priorityUri)
            .build();
        HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        URI url = URI.create(epicsPath);
        Epic epic = new Epic(
            1,
            "name",
            "description",
            Status.NEW,
            new ArrayList<>()
        );

        String epicJson = gson.toJson(epic);
        final HttpRequest.BodyPublisher epicBody = HttpRequest.BodyPublishers.ofString(epicJson);
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
            .POST(epicBody)
            .uri(url)
            .build();
        HttpResponse<String> epicResponse = httpClient.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        epic.setId(Integer.valueOf(epicResponse.body()));

        HttpRequest getRequest = HttpRequest.newBuilder()
            .GET()
            .uri(url)
            .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        ArrayList<String> list = new ArrayList<>();
        list.add(epic.toString());

        assertEquals(200, getResponse.statusCode());
        assertEquals(gson.toJson(list), getResponse.body());
    }
}
