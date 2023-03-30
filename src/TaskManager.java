import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    public static int taskId;
    public HashMap<Integer, Task> taskList = new HashMap<>();
    public HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    public HashMap<Integer, Epic> epicList = new HashMap<>();

    public void createNewTask(Task task) {
        Scanner scanner = new Scanner(System.in);

        if (task instanceof Subtask) {
            int epicId = scanner.nextInt();

            for (Epic epic : epicList.values()) {
                if (epic.getId() == epicId) {
                    task.setId(++taskId);
                    subtaskList.put(task.getId(), (Subtask) task);
                }
            }
        } else if (task instanceof Epic) {
            task.setId(++taskId);
            epicList.put(task.getId(), (Epic) task);
        } else if (task != null) {
            task.setId(++taskId);
            taskList.put(task.getId(), task);
        }
    }

    public void removeAllTasks() {
        taskList.clear();
        subtaskList.clear();
        epicList.clear();
    }

    public void removeTaskById(Integer id) {
        if (taskList.containsKey(id)) {
            taskList.remove(id);
        } else if (subtaskList.containsKey(id)) {
            subtaskList.remove(id);
        } else epicList.remove(id);
    }

    public Task getTaskById(Integer id) {
        Task foundedTask = null;
        if (taskList.containsKey(id)) {
            foundedTask = taskList.get(id);
        } else if (subtaskList.containsKey(id)) {
            foundedTask = subtaskList.get(id);
        } else if (epicList.containsKey(id)) {
            foundedTask = epicList.get(id);
        }
        return foundedTask;
    }

    public void updateTask(Task task) {
        if (task instanceof Subtask) {
            subtaskList.put(task.getId(), (Subtask) task);
        } else if (task instanceof Epic) {
            if (((Epic) task).getEpicSubtasks().isEmpty()) {
                task.setStatus(Status.NEW);
                return;
            }

            for (Subtask subtask : ((Epic) task).getEpicSubtasks()) {
                if (subtask.getStatus().equals("NEW")) {
                    task.setStatus(Status.NEW);
                } else {
                    task.setStatus(Status.IN_PROGRESS);
                    break;
                }
            }


            for (Subtask subtask : ((Epic) task).getEpicSubtasks()) {
                if (subtask.getStatus().equals("DONE")) {
                    task.setStatus(Status.DONE);
                } else {
                    task.setStatus(Status.IN_PROGRESS);
                    break;
                }
            }

            epicList.put(task.getId(), (Epic) task);
        } else {
            taskList.put(task.getId(), task);
        }
    }

    public ArrayList<String> getTaskList() {
        ArrayList<String> list = new ArrayList<>();

        for (Task task : taskList.values()) {
            list.add(task.toString());
        }

        for (Subtask subtask : subtaskList.values()) {
            list.add(subtask.toString());
        }

        for (Epic epic : epicList.values()) {
            list.add(epic.toString());
        }

        return list;
    }

    public ArrayList<String> getEpicSubtaskList(Integer epicId) {
        ArrayList<String> epicSubtaskList = new ArrayList<>();

        if (epicList.containsKey(epicId)) {
            for (Subtask subtask : epicList.get(epicId).getEpicSubtasks()) {
                epicSubtaskList.add(subtask.getName());
            }
        }
        return epicSubtaskList;
    }
}