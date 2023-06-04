package test;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;

import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private InMemoryTaskManager inMemoryTaskManager;
    private Task task;
    private Subtask subtaskWithNewStatus;
    private Epic epic;
    private Epic emptyEpic;
    private Epic epicWithNewAndDoneSubtasks;
    private Epic epicWithNewSubtask;
    private Epic epicWithDoneSubtasks;
    private Task taskWithStartTime1;
    private Task taskWithStartTime2;
    private Subtask taskWithStartTime3;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();

        task = new Task(
                "test-name",
                "test-description",
                Status.NEW
        );

        subtaskWithNewStatus = new Subtask(
                "test-name",
                "test-description",
                Status.NEW
        );

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtaskWithNewStatus);

        epic = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                subtasks
        );

        emptyEpic = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                new ArrayList<>()
        );

        Subtask subtaskWithDoneStatus = new Subtask("test-name", "test-description", Status.DONE);
        ArrayList<Subtask> subtasksWithNewAndDone = new ArrayList<>();
        subtasksWithNewAndDone.add(subtaskWithNewStatus);
        subtasksWithNewAndDone.add(subtaskWithDoneStatus);
        epicWithNewAndDoneSubtasks = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithNewAndDone
        );

        ArrayList<Subtask> subtasksNew = new ArrayList<>();
        subtasksNew.add(subtaskWithNewStatus);
        epicWithNewSubtask = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                subtasksNew
        );

        ArrayList<Subtask> subtasksDone = new ArrayList<>();
        subtasksDone.add(subtaskWithDoneStatus);
        epicWithDoneSubtasks = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                subtasksDone
        );

        taskWithStartTime1 = new Task(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2021, Month.NOVEMBER, 10, 10, 10)
        );

        taskWithStartTime2 = new Task(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2020, Month.NOVEMBER, 10, 10, 10)
        );

        taskWithStartTime3 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2019, Month.NOVEMBER, 10, 10, 10)
        );
    }

    @Test
    void createNewTask() {
        inMemoryTaskManager.createNewTask(task);
        final int taskId = task.getId();

        Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача c таким id не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        ArrayList<String> tasks = inMemoryTaskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.toString(), tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createNewTaskIfNull() {
        inMemoryTaskManager.createNewTask(null);
        ArrayList<String> tasks = inMemoryTaskManager.getTaskList();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void createNewSubtask() {
        inMemoryTaskManager.createNewSubtask(subtaskWithNewStatus);
        final int subtaskId = subtaskWithNewStatus.getId();

        Task savedSubtask = inMemoryTaskManager.getTaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtaskWithNewStatus, savedSubtask, "Задачи не совпадают.");

        final ArrayList<String> subtasks = inMemoryTaskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtaskWithNewStatus.toString(), subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createNewSubtaskIfNull() {
        inMemoryTaskManager.createNewSubtask(null);
        ArrayList<String> subtasks = inMemoryTaskManager.getSubtaskList();
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void createNewEpic() {
        inMemoryTaskManager.createNewEpic(epic);
        final int epicId = epic.getId();

        Task savedEpic = inMemoryTaskManager.getTaskById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = inMemoryTaskManager.getEpicList();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic.toString(), epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void createNewEpicIfNull() {
        inMemoryTaskManager.createNewEpic(null);
        ArrayList<String> epics = inMemoryTaskManager.getEpicList();
        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void updateTask() {
        inMemoryTaskManager.createNewTask(task);
        inMemoryTaskManager.updateTask(task);
        Task updatedTask = inMemoryTaskManager.getTaskById(task.getId());

        assertNotNull(updatedTask, "Задача c таким id не найдена.");
        assertEquals(task, updatedTask, "Задачи не совпадают.");

        ArrayList<String> tasks = inMemoryTaskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.toString(), tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTaskIfNull() {
        inMemoryTaskManager.updateTask(null);
        ArrayList<String> tasks = inMemoryTaskManager.getTaskList();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void updateSubtask() {
        inMemoryTaskManager.createNewSubtask(subtaskWithNewStatus);
        inMemoryTaskManager.updateSubtask(subtaskWithNewStatus);
        final int subtaskId = subtaskWithNewStatus.getId();

        Task savedSubtask = inMemoryTaskManager.getTaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtaskWithNewStatus, savedSubtask, "Задачи не совпадают.");

        final ArrayList<String> subtasks = inMemoryTaskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtaskWithNewStatus.toString(), subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateSubtaskIfNull() {
        inMemoryTaskManager.updateSubtask(null);
        ArrayList<String> subtasks = inMemoryTaskManager.getSubtaskList();
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void updateEpicWithAllDoneSubtasks() {
        inMemoryTaskManager.updateEpic(epicWithDoneSubtasks);
        assertEquals(Status.DONE.toString(), epicWithDoneSubtasks.getStatus());

        Task savedEpic = inMemoryTaskManager.getTaskById(epicWithDoneSubtasks.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epicWithDoneSubtasks, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = inMemoryTaskManager.getEpicList();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epicWithDoneSubtasks.toString(), epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpicWithAllNewSubtasks() {
        inMemoryTaskManager.updateEpic(epicWithNewSubtask);
        assertEquals(Status.NEW.toString(), epicWithNewSubtask.getStatus());

        Task savedEpic = inMemoryTaskManager.getTaskById(epicWithNewSubtask.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epicWithNewSubtask, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = inMemoryTaskManager.getEpicList();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epicWithNewSubtask.toString(), epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpicWithNotNewSubtasks() {
        inMemoryTaskManager.updateEpic(epicWithNewAndDoneSubtasks);
        assertEquals(Status.IN_PROGRESS.toString(), epicWithNewAndDoneSubtasks.getStatus());

        Task savedEpic = inMemoryTaskManager.getTaskById(epicWithNewAndDoneSubtasks.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epicWithNewAndDoneSubtasks, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = inMemoryTaskManager.getEpicList();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epicWithNewAndDoneSubtasks.toString(), epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpicIfEmptySubtasksList() {
        inMemoryTaskManager.updateEpic(emptyEpic);
        assertEquals(
                Status.NEW.toString(),
                emptyEpic.getStatus(),
                "Неверный статус эпика. Ожидается NEW, получен " + emptyEpic.getStatus()
        );

        Task savedEpic = inMemoryTaskManager.getTaskById(emptyEpic.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(emptyEpic, savedEpic, "Задачи не совпадают.");

        final ArrayList<String> epics = inMemoryTaskManager.getEpicList();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(emptyEpic.toString(), epics.get(0), "Задачи не совпадают.");
    }


    @Test
    void updateEpicIfNull() {
        inMemoryTaskManager.updateEpic(null);
        ArrayList<String> epics = inMemoryTaskManager.getEpicList();
        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void getTaskList() {
        inMemoryTaskManager.createNewTask(task);
        ArrayList<String> taskList = new ArrayList<>();
        taskList.add(task.toString());
        assertNotNull(inMemoryTaskManager.getTaskList(), "Список задач пуст.");
        assertEquals(taskList, inMemoryTaskManager.getTaskList(), "Возвращается неверный TaskList.");
    }

    @Test
    void getSubtaskList() {
        inMemoryTaskManager.createNewSubtask(subtaskWithNewStatus);
        ArrayList<String> subtaskList = new ArrayList<>();
        subtaskList.add(subtaskWithNewStatus.toString());
        assertNotNull(inMemoryTaskManager.getSubtaskList(), "Список задач пуст.");
        assertEquals(subtaskList, inMemoryTaskManager.getSubtaskList(), "Возвращается неверный SubtaskList.");
    }

    @Test
    void getEpicList() {
        inMemoryTaskManager.createNewEpic(epic);
        ArrayList<String> epicList = new ArrayList<>();
        epicList.add(epic.toString());
        assertNotNull(inMemoryTaskManager.getEpicList(), "Список задач пуст.");
        assertEquals(epicList, inMemoryTaskManager.getEpicList(), "Возвращается неверный TaskList.");
    }

    @Test
    void removeAllTasks() {
        inMemoryTaskManager.createNewTask(new Task("testName", "testD", Status.NEW));
        inMemoryTaskManager.createNewSubtask(new Subtask("testSub", "testD", Status.NEW));
        inMemoryTaskManager.createNewEpic(new Epic("testEpic", "testD", Status.NEW, new ArrayList<>()));

        inMemoryTaskManager.removeAllTasks();
        assertEquals(new ArrayList<>(), inMemoryTaskManager.getEpicList(), "Лист задач не очистился.");
    }

    @Test
    void removeTaskById() {
        inMemoryTaskManager.createNewTask(task);
        final int taskId = task.getId();

        Task removedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(removedTask, "Задача c таким id не найдена.");
        assertEquals(task, removedTask, "Задачи не совпадают.");

        inMemoryTaskManager.removeTaskById(taskId);

        assertNull(inMemoryTaskManager.getTaskById(taskId), "Задача c таким id не удалена.");
    }

    @Test
    void getTaskById() {
        inMemoryTaskManager.createNewTask(task);
        final int taskId = task.getId();

        Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача c таким id не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void getEpicSubtaskList() {
        inMemoryTaskManager.createNewEpic(epic);
        assertEquals(
                epic.getEpicSubtasks(),
                inMemoryTaskManager.getEpicSubtaskList(epic.getId()),
                "Сабтаски не совпадают."
        );
    }

    @Test
    void getEpicSubtaskListIfEmptyList() {
        inMemoryTaskManager.createNewEpic(emptyEpic);
        assertEquals(
                new ArrayList<Task>(),
                inMemoryTaskManager.getEpicSubtaskList(emptyEpic.getId()),
                "Лист подзадач не пустой."
        );
    }

    @Test
    void getPrioritizedTasks() {
        inMemoryTaskManager.createNewTask(task);
        inMemoryTaskManager.createNewTask(taskWithStartTime1);
        inMemoryTaskManager.createNewTask(taskWithStartTime2);
        inMemoryTaskManager.createNewSubtask(taskWithStartTime3);

        ArrayList<Task> expectedList = new ArrayList<>();
        expectedList.add(taskWithStartTime3);
        expectedList.add(taskWithStartTime2);
        expectedList.add(taskWithStartTime1);
        expectedList.add(task);

        assertEquals(expectedList, inMemoryTaskManager.getPrioritizedTasks());
    }

    @Test
    void getPrioritizedTasksIfEmptyList() {
        assertEquals(new ArrayList<Task>(), inMemoryTaskManager.getPrioritizedTasks());
    }
}