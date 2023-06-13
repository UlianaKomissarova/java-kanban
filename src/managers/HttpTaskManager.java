package managers;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient client;
    private final Gson gson;
    private static final String tasksKey = "tasks";
    private static final String subtasksKey = "subtasks";
    private static final String epicsKey = "epics";
    private static final String historyKey = "history";
    private static final String prioritizedTasksKey = "prioritizedTasks";

    public HttpTaskManager(URI uri) throws IOException, InterruptedException {
        super(null);
        client = new KVTaskClient(uri);
        gson = Managers.getGson();
        load();
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(taskMap);
        client.put(tasksKey, jsonTasks);

        String jsonSubtasks = gson.toJson(subtaskMap);
        client.put(subtasksKey, jsonSubtasks);

        String jsonEpics = gson.toJson(epicMap);
        client.put(epicsKey, jsonEpics);

        String jsonHistory = gson.toJson(historyManager.getHistory());
        client.put(historyKey, jsonHistory);

        String jsonPrioritizedTasks = gson.toJson(new ArrayList<>(taskTreeSet));
        client.put(prioritizedTasksKey, jsonPrioritizedTasks);
    }

    public void load() {
        String tasksFromJson = client.load(tasksKey);
        if (tasksFromJson != null && !tasksFromJson.isBlank()) {
            taskMap = gson.fromJson(
                    tasksFromJson,
                    new TypeToken<HashMap<Integer, Task>>() {
                    }.getType()
            );
        }

        String subtasksFromJson = client.load(subtasksKey);
        if (subtasksFromJson != null && !tasksFromJson.isBlank()) {
            subtaskMap = gson.fromJson(
                    subtasksFromJson,
                    new TypeToken<HashMap<Integer, Subtask>>() {
                    }.getType()
            );
        }

        String epicsFromJson = client.load(epicsKey);
        if (epicsFromJson != null && !tasksFromJson.isBlank()) {
            epicMap = gson.fromJson(
                    epicsFromJson,
                    new TypeToken<HashMap<Integer, Epic>>() {
                    }.getType()
            );
        }

        historyManager = Managers.getDefaultHistory();
        String historyFromJson = client.load(historyKey);
        if (historyFromJson != null && !tasksFromJson.isBlank()) {
            List<Task> history = gson.fromJson(
                    historyFromJson,
                    new TypeToken<List<Task>>() {
                    }.getType()
            );
            for (Task task : history) {
                historyManager.add(task);
            }
        }

        String prioritizedTasksFromJson = client.load(prioritizedTasksKey);
        if (prioritizedTasksFromJson != null && !tasksFromJson.isBlank()) {
            taskTreeSet = gson.fromJson(
                    prioritizedTasksFromJson,
                    new TypeToken<TreeSet<Task>>() {
                    }.getType()
            );
        }
    }
}
