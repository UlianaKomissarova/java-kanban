package managers;

import exceptions.ManagerSaveException;
import server.KVServer;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public HistoryManager historyManager = new InMemoryHistoryManager();
    private static final String FORMAT = "id,type,name,status,description,duration,startTime,epic\n";
    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public void save() {
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

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        manager.resetTaskIdCounter();

        List<String> lines = Files.readAllLines(Path.of(file.toURI()), StandardCharsets.UTF_8);
        lines.remove(lines.get(0));

        if (lines.isEmpty()) {
            return manager;
        }

        int lastElementNumber = lines.size() - 1;
        String history = lines.get(lastElementNumber);
        lines.remove(lastElementNumber);

        for (String line : lines) {
            if (!line.isBlank() && !line.equals("\n")) {
                Task task = manager.fromString(line);
                if (task instanceof Subtask) {
                    manager.createNewSubtask((Subtask) task);
                } else if (task instanceof Epic) {
                    manager.createNewEpic((Epic) task);
                } else if (task != null) {
                    manager.createNewTask(task);
                }
            }
        }

        for (Integer id : historyFromString(history)) {
            manager.historyManager.add(manager.getTaskById(id));
        }
        manager.save();

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            if (task.getType() == Type.SUBTASK) {
                Subtask subtask = (Subtask) task;
                if (task.getStartTime() != null && task.getDuration() != 0) {
                    taskInfo = String.format(
                            "%d,%s,%s,%s,%s,%d,%s,%d",
                            subtask.getId(),
                            subtask.getType(),
                            subtask.getName(),
                            subtask.getStatus(),
                            subtask.getDescription(),
                            subtask.getDuration(),
                            subtask.getStartTime().format(formatter),
                            subtask.getEpicId()
                    );
                } else {
                    taskInfo = String.format(
                            "%d,%s,%s,%s,%s,%d,%s,%d",
                            subtask.getId(),
                            subtask.getType(),
                            subtask.getName(),
                            subtask.getStatus(),
                            subtask.getDescription(),
                            0,
                            null,
                            subtask.getEpicId()
                    );
                }
            } else  {
                if (task.getStartTime() != null && task.getDuration() != 0 && !(task instanceof Epic)) {
                    taskInfo = String.format(
                            "%d,%s,%s,%s,%s,%d,%s",
                            task.getId(),
                            task.getType(),
                            task.getName(),
                            task.getStatus(),
                            task.getDescription(),
                            task.getDuration(),
                            task.getStartTime().format(formatter)
                    );
                } else {
                    taskInfo = String.format(
                            "%d,%s,%s,%s,%s",
                            task.getId(),
                            task.getType(),
                            task.getName(),
                            task.getStatus(),
                            task.getDescription()
                    );
                }
            }
        } else return null;

        return taskInfo;
    }

    public Task fromString(String value) {
        String[] split = value.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        switch (split[1]) {
            case "SUBTASK":
                Subtask subtask;
                if (split.length == 8 && Integer.parseInt(split[5]) != 0 && split[6] != null) {
                    subtask = new Subtask(
                            split[2],
                            split[4],
                            Status.valueOf(split[3]),
                            Integer.parseInt(split[5]),
                            LocalDateTime.parse(split[6], formatter)
                    );
                    this.linkSubtaskWithEpic(subtask, Integer.valueOf(split[7]));
                } else {
                    subtask = new Subtask(
                            split[2],
                            split[4],
                            Status.valueOf(split[3])
                    );
                    this.linkSubtaskWithEpic(subtask, Integer.valueOf(split[5]));
                }
                return subtask;
            case "TASK":
                Task task;
                if (split.length == 7 && Integer.parseInt(split[5]) != 0 && split[6] != null) {
                    task = new Task(
                            split[2],
                            split[4],
                            Status.valueOf(split[3]),
                            Integer.parseInt(split[5]),
                            LocalDateTime.parse(split[6], formatter)
                    );
                } else {
                    task = new Task(
                            split[2],
                            split[4],
                            Status.valueOf(split[3])
                    );
                }

                return task;
            case "EPIC":
                return new Epic(
                        split[2],
                        split[4],
                        Status.valueOf(split[3]),
                        new ArrayList<>()
                );
            default:
                return null;
        }
    }

    public void linkSubtaskWithEpic(Subtask subtask, Integer epicId) throws InvalidParameterException {
        Epic epic = this.epicMap.get(epicId);
        if (epic == null) {
            throw new InvalidParameterException("Задан неверный epicId");
        }

        subtask.setEpicId(epic.getId());
        epic.getEpicSubtasks().add(subtask);
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
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