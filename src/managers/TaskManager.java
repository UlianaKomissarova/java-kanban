package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    void resetTaskIdCounter();

    void createNewTask(Task task);

    void createNewSubtask(Subtask subtask);

    void createNewEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    ArrayList<String> getTaskList();

    ArrayList<String> getSubtaskList();

    ArrayList<String> getEpicList();

    void removeAllTasks();

    void removeTaskById(Integer id);

    Task getTaskById(Integer id);

    ArrayList<Subtask> getEpicSubtaskList(Integer epicId);

    ArrayList<Task> getPrioritizedTasks();
}
