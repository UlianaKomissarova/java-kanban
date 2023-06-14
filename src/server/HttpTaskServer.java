package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import tasks.*;
import managers.FileBackedTasksManager;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final FileBackedTasksManager taskManager;

    public HttpTaskServer() throws IOException, InterruptedException {
        taskManager = (FileBackedTasksManager) Managers.getDefault();
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/task", this::handleTask);
        server.createContext("/tasks/subtask", this::handleSubtask);
        server.createContext("/tasks/epic", this::handleEpic);
        server.createContext("/tasks/subtask/epic", this::handleSubtaskByEpicId);
        server.createContext("/tasks/history", this::handleHistory);
        server.createContext("/tasks", this::handlePrioritizedTasks);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private void handlePrioritizedTasks(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                getAndSendPrioritizedTasks(httpExchange, query);
            } else if (requestMethod.equals("DELETE")) {
                taskManager.removeAllTasks();
                httpExchange.sendResponseHeaders(201, 0);
            } else {
                System.out.println("Ожидается GET запрос, получен некорректный запрос " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getAndSendPrioritizedTasks(HttpExchange httpExchange, String query) throws IOException {
        if (query == null) {
            ArrayList<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            if (prioritizedTasks != null) {
                httpExchange.sendResponseHeaders(201, 0);
                String response = gson.toJson(prioritizedTasks);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                httpExchange.sendResponseHeaders(400, 0);
            }
        } else {
            httpExchange.sendResponseHeaders(400, 0);
        }
    }

    private void handleHistory(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                getAndSendHistory(httpExchange, query);
            } else {
                System.out.println("Ожидается GET запрос, получен некорректный запрос " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getAndSendHistory(HttpExchange httpExchange, String query) throws IOException {
        if (query == null) {
            List<Task> history = taskManager.historyManager.getHistory();
            if (history != null) {
                httpExchange.sendResponseHeaders(201, 0);
                String response = gson.toJson(history);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                httpExchange.sendResponseHeaders(400, 0);
            }
        } else {
            httpExchange.sendResponseHeaders(400, 0);
        }
    }

    private void handleSubtaskByEpicId(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                getAndSendSubtaskById(httpExchange, query);
            } else {
                System.out.println("Ожидается GET запрос, получен некорректный запрос " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getAndSendSubtaskById(HttpExchange httpExchange, String query) throws IOException {
        if (query == null) {
            httpExchange.sendResponseHeaders(400, 0);
        } else {
            if (query.startsWith("id=")) {
                String[] split = query.split("&")[0].split("=");
                Integer epicId = Integer.parseInt(split[1]);
                List<Subtask> subtasks = taskManager.getEpicSubtaskList(epicId);
                if (subtasks != null) {
                    httpExchange.sendResponseHeaders(200, 0);
                    String response = gson.toJson(subtasks);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);
                }
            } else {
                httpExchange.sendResponseHeaders(400, 0);
            }
        }
    }

    private void handleEpic(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET":
                    getTasksOrTaskById(httpExchange, query, Type.EPIC);

                    break;
                case "POST":
                    updateOrCreateTask(httpExchange, query, Type.EPIC);

                    break;
                case "DELETE":
                    deleteAllTasksOrTaskById(httpExchange, query);

                    break;
                default:
                    System.out.println("Ожидается GET/POST/DELETE, получен некорректный запрос " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void handleSubtask(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET":
                    getTasksOrTaskById(httpExchange, query, Type.SUBTASK);

                    break;
                case "POST":
                    updateOrCreateTask(httpExchange, query, Type.SUBTASK);

                    break;
                case "DELETE":
                    deleteAllTasksOrTaskById(httpExchange, query);

                    break;
                default:
                    System.out.println("Ожидается GET/POST/DELETE, получен некорректный запрос " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void handleTask(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET":
                    getTasksOrTaskById(httpExchange, query, Type.TASK);

                    break;
                case "POST":
                    updateOrCreateTask(httpExchange, query, Type.TASK);

                    break;
                case "DELETE":
                    deleteAllTasksOrTaskById(httpExchange, query);

                    break;
                default:
                    System.out.println("Ожидается GET/POST/DELETE, получен некорректный запрос " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void deleteAllTasksOrTaskById(HttpExchange httpExchange, String query) throws IOException {
        if (query == null) {
            deleteTasks(httpExchange);
            httpExchange.sendResponseHeaders(200, 0);
        } else {
            if (query.startsWith("id=")) {
                String[] split = query.split("&")[0].split("=");
                Integer taskId = Integer.parseInt(split[1]);
                if (taskId != -1) {
                    System.out.println("Удалена задача " + taskManager.getTaskById(taskId).getName());
                    taskManager.removeTaskById(taskId);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получен некорректный id");
                    httpExchange.sendResponseHeaders(404, 0);
                }
            } else {
                httpExchange.sendResponseHeaders(400, 0);
            }
        }
    }

    private void deleteTasks(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        if (path.equals("/tasks/subtask")) {
            taskManager.removeSubtasks();
        } else if (path.equals("/tasks/epic")) {
            taskManager.removeEpics();
        } else {
            taskManager.removeTasks();
        }
    }

    private void updateTask(HttpExchange httpExchange, Task task) throws IOException {
        if (task instanceof Subtask) {
            taskManager.updateSubtask((Subtask) task);
            System.out.println("Обновлена подзадача " + task.getName());
        } else if (task instanceof Epic) {
            ((Epic) task).setSubtasks(taskManager.getEpicSubtaskList(task.getId()));
            taskManager.updateEpic((Epic) task);
            System.out.println("Обновлен эпик " + task.getName());
        } else {
            taskManager.updateTask(task);
            System.out.println("Обновлена задача " + task.getName());
        }

        httpExchange.sendResponseHeaders(201, 0);
        String response = Boolean.toString(true);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void createTask(HttpExchange httpExchange, Task task) throws IOException {
        if (task instanceof Subtask) {
            taskManager.createNewSubtask((Subtask) task);
            System.out.println("Создана подзадача " + task.getName());
        } else if (task instanceof Epic) {
            taskManager.createNewEpic((Epic) task);
            ((Epic) task).setSubtasks(new ArrayList<>());
            System.out.println("Создан эпик " + task.getName());
        } else {
            taskManager.createNewTask(task);
            System.out.println("Создана задача " + task.getName());
        }

        if (task.getId() != null) {
            httpExchange.sendResponseHeaders(201, 0);
            String response = task.getId().toString();
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            httpExchange.sendResponseHeaders(422, 0);
            String response = "Задача не создана";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void getTasksOrTaskById(HttpExchange httpExchange, String query, Type type) throws IOException {
        if (query == null) {
            getAllTasks(httpExchange, type);
        } else {
            if (query.startsWith("id=")) {
                getTaskById(httpExchange, query, type);
            } else {
                httpExchange.sendResponseHeaders(400, 0);
            }
        }
    }

    private void getAllTasks(HttpExchange httpExchange, Type type) throws IOException {
        ArrayList<String> taskList;
        if (type.equals(Type.SUBTASK)) {
            taskList = taskManager.getSubtaskList();
        } else if (type.equals(Type.EPIC)) {
            taskList = taskManager.getEpicList();
        } else {
            taskList = taskManager.getTaskList();
        }

        if (taskList != null) {
            httpExchange.sendResponseHeaders(200, 0);
            String response = gson.toJson(taskList);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            httpExchange.sendResponseHeaders(204, 0);
        }
    }

    private void getTaskById(HttpExchange httpExchange, String query, Type type) throws IOException {
        String[] split = query.split("&")[0].split("=");
        Integer taskId = Integer.parseInt(split[1]);
        Task task = taskManager.getTaskById(taskId);

        if (task != null && task.getType().equals(type)) {
            taskManager.historyManager.add(task);

            httpExchange.sendResponseHeaders(200, 0);
            String response = gson.toJson(task);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            httpExchange.sendResponseHeaders(404, 0);
        }
    }

    private void updateOrCreateTask(HttpExchange httpExchange, String query, Type type) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Task newTask;
        if (type.equals(Type.SUBTASK)) {
            JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);
            newTask = gson.fromJson(jsonObject.get("subtask").toString(), Subtask.class);

            try {
                taskManager.linkSubtaskWithEpic((Subtask) newTask, jsonObject.get("epicId").getAsInt());
            } catch (InvalidParameterException exception) {
                sendBadRequest(httpExchange, "Неверный epicId");
                return;
            }

            if (((Subtask) newTask).getEpicId() == null) {
                sendBadRequest(httpExchange, "Необходимо указать epicId");
                return;
            }
        } else if (type.equals(Type.EPIC)) {
            newTask = gson.fromJson(requestBody, Epic.class);
        } else {
            newTask = gson.fromJson(requestBody, Task.class);
        }
        newTask.setType(type);

        if (null != query && query.startsWith("id=")) {
            String[] split = query.split("&")[0].split("=");
            Integer taskId = Integer.parseInt(split[1]);

            newTask.setId(taskId);
            updateTask(httpExchange, newTask);
        } else {
            createTask(httpExchange, newTask);
        }
    }

    private void sendBadRequest(HttpExchange httpExchange, String message) throws IOException {
        httpExchange.sendResponseHeaders(400, 0);
        String response = gson.toJson(message);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
