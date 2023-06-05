package test;

import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtaskWithNewStatus;

    @BeforeEach
    void beforeEach() {
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
    }

    @Test
    void getTaskById() {
        taskManager.createNewTask(task);
        final int taskId = task.getId();

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача c таким id не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void getTaskList() {
        taskManager.createNewTask(task);
        ArrayList<String> taskList = new ArrayList<>();
        taskList.add(task.toString());
        assertNotNull(taskManager.getTaskList(), "Список задач пуст.");
        assertEquals(taskList, taskManager.getTaskList(), "Возвращается неверный TaskList.");
    }

    @Test
    void getSubtaskList() {
        taskManager.createNewSubtask(subtaskWithNewStatus);
        ArrayList<String> subtaskList = new ArrayList<>();
        subtaskList.add(subtaskWithNewStatus.toString());
        assertNotNull(taskManager.getSubtaskList(), "Список задач пуст.");
        assertEquals(subtaskList, taskManager.getSubtaskList(), "Возвращается неверный SubtaskList.");
    }

    @Test
    void getEpicList() {
        taskManager.createNewEpic(epic);
        ArrayList<String> epicList = new ArrayList<>();
        epicList.add(epic.toString());
        assertNotNull(taskManager.getEpicList(), "Список задач пуст.");
        assertEquals(epicList, taskManager.getEpicList(), "Возвращается неверный TaskList.");
    }

    @Test
    void getEpicSubtaskList() {
        taskManager.createNewEpic(epic);
        assertEquals(
                epic.getEpicSubtasks(),
                taskManager.getEpicSubtaskList(epic.getId()),
                "Сабтаски не совпадают."
        );
    }

    @Test
    void getEpicSubtaskListIfEmptyList() {
        Epic emptyEpic = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                new ArrayList<>()
        );

        taskManager.createNewEpic(emptyEpic);
        assertEquals(
                new ArrayList<Task>(),
                taskManager.getEpicSubtaskList(emptyEpic.getId()),
                "Лист подзадач не пустой."
        );
    }

    @Test
    void getPrioritizedTasks() {
        Task taskWithStartTime1 = new Task(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2021, Month.NOVEMBER, 10, 10, 10)
        );

        Task taskWithStartTime2 = new Task(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2020, Month.NOVEMBER, 10, 10, 10)
        );

        Subtask taskWithStartTime3 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2019, Month.NOVEMBER, 10, 10, 10)
        );
        ArrayList<Subtask> epicListWithSubtask3 = new ArrayList<>();
        epicListWithSubtask3.add(taskWithStartTime3);
        Epic epicWithSubtask3 = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                epicListWithSubtask3
        );

        taskManager.createNewTask(task);
        taskManager.createNewTask(taskWithStartTime1);
        taskManager.createNewTask(taskWithStartTime2);
        taskManager.createNewSubtask(taskWithStartTime3);

        ArrayList<Task> expectedList = new ArrayList<>();
        expectedList.add(taskWithStartTime3);
        expectedList.add(taskWithStartTime2);
        expectedList.add(taskWithStartTime1);
        expectedList.add(task);

        assertEquals(expectedList, taskManager.getPrioritizedTasks());
    }

    @Test
    void getPrioritizedTasksIfEmptyList() {
        assertEquals(new ArrayList<Task>(), taskManager.getPrioritizedTasks());
    }
}