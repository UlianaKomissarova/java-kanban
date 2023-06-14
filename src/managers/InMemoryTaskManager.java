package managers;

import comparators.TaskDateComparator;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    public static int taskId;
    protected HashMap<Integer, Task> taskMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicMap = new HashMap<>();
    protected TreeSet<Task> taskTreeSet = new TreeSet<>(new TaskDateComparator());

    @Override
    public void resetTaskIdCounter() {
        taskId = 0;
    }

    @Override
    public void createNewTask(Task task) {
        if (task != null) {
            task.setId(++taskId);
            taskMap.put(task.getId(), task);
            taskTreeSet.add(task);
        }
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        if (subtask != null) {
            subtask.setId(++taskId);
            subtaskMap.put(subtask.getId(), subtask);
            boolean result = taskTreeSet.add(subtask);
            int a = 1;
        }
    }

    @Override
    public void createNewEpic(Epic epic) {
        if (epic != null) {
            epic.setId(++taskId);
            epicMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            taskMap.put(task.getId(), task);
            taskTreeSet.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            subtaskMap.put(subtask.getId(), subtask);
            taskTreeSet.add(subtask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) return;

        if (epic.getEpicSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            epicMap.put(epic.getId(), epic);
            return;
        }

        String comparingStatus = epic.getEpicSubtasks().get(0).getStatus();
        for (Subtask epicSubtask : epic.getEpicSubtasks()) {
            if (epicSubtask.getStatus().equals(comparingStatus)) {
                epic.setStatus(Status.valueOf(comparingStatus));
            } else {
                epic.setStatus(Status.IN_PROGRESS);
                break;
            }
        }

        epicMap.put(epic.getId(), epic);
    }

    @Override
    public ArrayList<String> getTaskList() {
        ArrayList<String> taskList = new ArrayList<>();

        for (Task task : taskMap.values()) {
            taskList.add(task.toString());
        }

        return taskList;
    }

    @Override
    public ArrayList<String> getSubtaskList() {
        ArrayList<String> subtaskList = new ArrayList<>();

        for (Subtask subtask : subtaskMap.values()) {
            subtaskList.add(subtask.toString());
        }

        return subtaskList;
    }

    @Override
    public ArrayList<String> getEpicList() {
        ArrayList<String> epicList = new ArrayList<>();

        for (Epic epic : epicMap.values()) {
            epicList.add(epic.toString());
        }

        return epicList;
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
        subtaskMap.clear();
        epicMap.clear();
        taskTreeSet.clear();
    }

    public void removeEpics() {
        epicMap.clear();
        taskTreeSet.removeIf(task -> task instanceof Epic);
    }

    public void removeSubtasks() {
        subtaskMap.clear();
        taskTreeSet.removeIf(task -> task instanceof Subtask);
    }

    public void removeTasks() {
        taskMap.clear();
        taskTreeSet.removeIf(task -> !(task instanceof Epic) && !(task instanceof Subtask));
    }

    @Override
    public void removeTaskById(Integer id) {
        if (taskMap.containsKey(id)) {
            taskTreeSet.remove(getTaskById(id));
            taskMap.remove(id);
        } else if (subtaskMap.containsKey(id)) {
            taskTreeSet.remove(getTaskById(id));
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

    public ArrayList<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();
        list.addAll(taskMap.values());
        list.addAll(subtaskMap.values());
        list.addAll(epicMap.values());

        return list;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtaskList(Integer epicId) {
        return epicMap.get(epicId).getEpicSubtasks();
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(taskTreeSet);
    }
}