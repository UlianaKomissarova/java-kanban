package test;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private Epic epicWithNewAndDoneSubtasks;
    private Epic epicWithNewSubtask;
    private Epic epicWithDoneSubtasks;

    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        taskManager = new InMemoryTaskManager();

        Subtask subtaskWithDoneStatus = new Subtask("test-name", "test-description", Status.DONE);
        ArrayList<Subtask> subtasksWithNewAndDone = new ArrayList<>();
        subtasksWithNewAndDone.add(subtaskWithNewStatus);
        subtasksWithNewAndDone.add(subtaskWithDoneStatus);
        epicWithNewAndDoneSubtasks = new Epic(
                11,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithNewAndDone
        );

        ArrayList<Subtask> subtasksNew = new ArrayList<>();
        subtasksNew.add(subtaskWithNewStatus);
        epicWithNewSubtask = new Epic(
                12,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksNew
        );

        ArrayList<Subtask> subtasksDone = new ArrayList<>();
        subtasksDone.add(subtaskWithDoneStatus);
        epicWithDoneSubtasks = new Epic(
                13,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksDone
        );
    }

    @Test
    void createNewTask() {
        taskManager.createNewTask(task);
        final int taskId = task.getId();

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача c таким id не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        ArrayList<String> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.toString(), tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createNewTaskIfNull() {
        taskManager.createNewTask(null);
        ArrayList<String> tasks = taskManager.getTaskList();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void createNewSubtask() {
        taskManager.createNewSubtask(subtaskWithNewStatus);
        final int subtaskId = subtaskWithNewStatus.getId();

        Task savedSubtask = taskManager.getTaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtaskWithNewStatus, savedSubtask, "Задачи не совпадают.");

        final ArrayList<String> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtaskWithNewStatus.toString(), subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createNewSubtaskIfNull() {
        taskManager.createNewSubtask(null);
        ArrayList<String> subtasks = taskManager.getSubtaskList();
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void createNewEpic() {
        taskManager.createNewEpic(epic);
        final int epicId = epic.getId();

        Task savedEpic = taskManager.getTaskById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic.toString(), epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void createNewEpicIfNull() {
        taskManager.createNewEpic(null);
        ArrayList<String> epics = taskManager.getEpicList();
        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void updateTask() {
        taskManager.createNewTask(task);
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(task.getId());

        assertNotNull(updatedTask, "Задача c таким id не найдена.");
        assertEquals(task, updatedTask, "Задачи не совпадают.");

        ArrayList<String> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.toString(), tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTaskIfNull() {
        taskManager.updateTask(null);
        ArrayList<String> tasks = taskManager.getTaskList();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void updateSubtask() {
        taskManager.createNewSubtask(subtaskWithNewStatus);
        taskManager.updateSubtask(subtaskWithNewStatus);
        final int subtaskId = subtaskWithNewStatus.getId();

        Task savedSubtask = taskManager.getTaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtaskWithNewStatus, savedSubtask, "Задачи не совпадают.");

        final ArrayList<String> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtaskWithNewStatus.toString(), subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateSubtaskIfNull() {
        taskManager.updateSubtask(null);
        ArrayList<String> subtasks = taskManager.getSubtaskList();
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void updateEpicWithAllDoneSubtasks() {
        taskManager.updateEpic(epicWithDoneSubtasks);
        assertEquals(Status.DONE.toString(), epicWithDoneSubtasks.getStatus());

        Task savedEpic = taskManager.getTaskById(epicWithDoneSubtasks.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epicWithDoneSubtasks, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = taskManager.getEpicList();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epicWithDoneSubtasks.toString(), epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpicWithAllNewSubtasks() {
        taskManager.updateEpic(epicWithNewSubtask);
        assertEquals(Status.NEW.toString(), epicWithNewSubtask.getStatus());

        Task savedEpic = taskManager.getTaskById(epicWithNewSubtask.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epicWithNewSubtask, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = taskManager.getEpicList();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epicWithNewSubtask.toString(), epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpicWithNotNewSubtasks() {
        taskManager.updateEpic(epicWithNewAndDoneSubtasks);
        assertEquals(Status.IN_PROGRESS.toString(), epicWithNewAndDoneSubtasks.getStatus());

        Task savedEpic = taskManager.getTaskById(epicWithNewAndDoneSubtasks.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epicWithNewAndDoneSubtasks, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = taskManager.getEpicList();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epicWithNewAndDoneSubtasks.toString(), epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpicIfEmptySubtasksList() {
        Epic emptyEpic = new Epic(
                14,
                "test-name",
                "test-description",
                Status.NEW,
                new ArrayList<>()
        );

        taskManager.updateEpic(emptyEpic);
        assertEquals(
                Status.NEW.toString(),
                emptyEpic.getStatus(),
                "Неверный статус эпика. Ожидается NEW, получен " + emptyEpic.getStatus()
        );

        Task savedEpic = taskManager.getTaskById(emptyEpic.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(emptyEpic, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = taskManager.getEpicList();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(emptyEpic.toString(), epics.get(0), "Задачи не совпадают.");
    }


    @Test
    void updateEpicIfNull() {
        taskManager.updateEpic(null);
        ArrayList<String> epics = taskManager.getEpicList();
        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void removeAllTasks() {
        taskManager.createNewTask(new Task("testName", "testD", Status.NEW));
        taskManager.createNewSubtask(new Subtask("testSub", "testD", Status.NEW));
        taskManager.createNewEpic(new Epic(15,"testEpic", "testD", Status.NEW, new ArrayList<>()));

        taskManager.removeAllTasks();
        assertEquals(new ArrayList<>(), taskManager.getEpicList(), "Лист задач не очистился.");
    }

    @Test
    void removeTaskById() {
        taskManager.createNewTask(task);
        final int taskId = task.getId();

        Task removedTask = taskManager.getTaskById(taskId);

        assertNotNull(removedTask, "Задача c таким id не найдена.");
        assertEquals(task, removedTask, "Задачи не совпадают.");

        taskManager.removeTaskById(taskId);

        assertNull(taskManager.getTaskById(taskId), "Задача c таким id не удалена.");
    }
}