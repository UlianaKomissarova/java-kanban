package managers;

import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    void createNewTask(Task task);

    void removeAllTasks();

    void removeTaskById(Integer id);

    Task getTaskById(Integer id);

    void updateTask(Task task);

    ArrayList<String> getTaskList();

    ArrayList<Subtask> getEpicSubtaskList(Integer epicId);
}
