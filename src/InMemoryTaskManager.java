import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class InMemoryTaskManager implements TaskManager {
    public static int taskId;
    public HashMap<Integer, Task> taskMap = new HashMap<>();
    public HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    public HashMap<Integer, Epic> epicMap = new HashMap<>();

    @Override
    public void createNewTask(Task task) {
        Scanner scanner = new Scanner(System.in);

        if (task instanceof Subtask) {
            int epicId = scanner.nextInt();

            if (epicMap.containsKey(epicId)) {
                task.setId(++taskId);
                subtaskMap.put(task.getId(), (Subtask) task);
            }
        } else if (task instanceof Epic) {
            task.setId(++taskId);
            epicMap.put(task.getId(), (Epic) task);
        } else if (task != null) {
            task.setId(++taskId);
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
        subtaskMap.clear();
        epicMap.clear();
    }

    @Override
    public void removeTaskById(Integer id) {
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
        } else if (subtaskMap.containsKey(id)) {
            subtaskMap.remove(id);
        } else epicMap.remove(id);
    }

    @Override
    public Task getTaskById(Integer id) {
        Task foundedTask = null;
        if (taskMap.containsKey(id)) {
            foundedTask = taskMap.get(id);
        } else if (subtaskMap.containsKey(id)) {
            foundedTask = subtaskMap.get(id);
        } else if (epicMap.containsKey(id)) {
            foundedTask = epicMap.get(id);
        }

        return foundedTask;
    }

    @Override
    public void updateTask(Task task) {
        if (task instanceof Subtask) {
            subtaskMap.put(task.getId(), (Subtask) task);
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

            epicMap.put(task.getId(), (Epic) task);
        } else {
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public ArrayList<String> getTaskList() {
        ArrayList<String> list = new ArrayList<>();

        for (Task task : taskMap.values()) {
            list.add(task.toString());
        }

        for (Subtask subtask : subtaskMap.values()) {
            list.add(subtask.toString());
        }

        for (Epic epic : epicMap.values()) {
            list.add(epic.toString());
        }

        return list;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtaskList(Integer epicId) {
        return epicMap.get(epicId).getEpicSubtasks();
    }
}