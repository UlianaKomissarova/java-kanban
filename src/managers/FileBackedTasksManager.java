package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private static final String FORMAT = "id,type,name,status,description,epic\n";
    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileManager;
        try {
            fileManager = loadFromFile(new File("resources/task.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileManager.historyManager.add(fileManager.getTaskById(1));
        fileManager.historyManager.add(fileManager.getTaskById(6));
        fileManager.historyManager.add(fileManager.getTaskById(2));
        fileManager.save();

        System.out.println("Проверим список:");
        for (Task task : fileManager.historyManager.getHistory()) {
            System.out.println(task);
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            StringBuilder sb = new StringBuilder(FORMAT);

            ArrayList<Task> taskList = super.getTasks();
            taskList.sort(Comparator.comparing(Task::getId));
            for (Task task : taskList) {
                sb.append(toString(task));
                sb.append(",\n");
            }

            sb.append("\n");
            sb.append(historyToString(this.historyManager));
            writer.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        List<String> lines = Files.readAllLines(Path.of("resources/task.csv"), StandardCharsets.UTF_8);
        lines.remove(lines.get(0));
        int lastElementNumber = lines.size() - 1;
        String history = lines.get(lastElementNumber);
        lines.remove(lastElementNumber);

        for (String line : lines) {
            if (!line.isBlank() && !line.equals("\n")) {
                Task task = manager.fromString(line);
                manager.createNewTask(task);
            }
        }

        HashMap<Integer, Task> taskMap = new HashMap<>();
        HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
        HashMap<Integer, Epic> epicMap = new HashMap<>();

        List<Task> taskList = manager.getTasks();
        for (Task task : taskList) {
            if (task instanceof Subtask) {
                subtaskMap.put(task.getId(), (Subtask) task);
            } else if (task instanceof Epic) {
                epicMap.put(task.getId(), (Epic) task);
            } else if (task != null) {
                taskMap.put(task.getId(), task);
            }
        }

        for (Integer id : historyFromString(history)) {
            if (subtaskMap.containsKey(id)) {
                manager.getTaskById(id);
            } else if (epicMap.containsKey(id)) {
                manager.getTaskById(id);
            } else if (taskMap.containsKey(id)) {
                manager.getTaskById(id);
            }
        }

        return manager;
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> tasks = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        if (tasks.size() > 0) {
            if (tasks.size() == 1) {
                sb.append(tasks.get(0).getId());
            } else {
                for (Task task : tasks) {
                    sb.append(task.getId());
                    if (!Objects.equals(task.getId(), tasks.get(tasks.size() - 1).getId())) {
                        sb.append(",");
                    }
                }
            }
        }

        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] valueArray = value.split(",");
        for (String s : valueArray) {
            if (!s.isEmpty()) {
                history.add(Integer.valueOf(s));
            }
        }

        return history;
    }

    public String toString(Task task) {
        String taskInfo;

        if (task != null) {
            if (task.getType() == Type.SUBTASK) {
                Subtask subtask = (Subtask) task;
                taskInfo = String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), subtask.getType(), subtask.getName(),
                        subtask.getStatus(), subtask.getDescription(), subtask.getEpic().getId());
            } else {
                taskInfo = String.format("%d,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription());
            }
        } else return null;

        return taskInfo;
    }

    public Task fromString(String value) {
        String[] split = value.split(",");
        switch (split[1]) {
            case "SUBTASK":
                Subtask subtask = new Subtask(split[2], split[4], Status.valueOf(split[3]));
                this.linkSubtaskWithEpic(subtask, Integer.valueOf(split[5]));
                return subtask;
            case "TASK":
                return new Task(split[2], split[4], Status.valueOf(split[3]));
            case "EPIC":
                return new Epic(split[2], split[4], Status.valueOf(split[3]), new ArrayList<>());
            default:
                return null;
        }
    }

    private void linkSubtaskWithEpic(Subtask subtask, Integer epicId) {
        Epic epic = this.epicMap.get(epicId);
        subtask.setEpic(epic);
        epic.getEpicSubtasks().add(subtask);
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }
}