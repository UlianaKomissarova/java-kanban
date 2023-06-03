package test;

import managers.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
    }

    @Test
    void save() throws IOException {
        FileBackedTasksManager actualFileBackedTasksManager = new FileBackedTasksManager(
                new File("src/test/resources/actual_tasks.csv")
        );
        
        Task task1 = new Task(
                "Погулять",
                "Идем гулять в парк!",
                Status.NEW,
                20,
                LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        Task task2 = new Task(
                "Поиграть с кошкой",
                "Достань игрушки!",
                Status.NEW
        );

        Subtask subtask5 = new Subtask(
                "Собрать вещи",
                "Возьми все самое нужное с собой",
                Status.NEW,
                10,
                LocalDateTime.of(2001, 10, 10, 10, 10)
        );

        Subtask subtask6 = new Subtask(
                "Найди жилье",
                "Поищи объявления об аренде",
                Status.NEW,
                20,
                LocalDateTime.of(2001, 11, 11, 11, 11)
        );

        ArrayList<Subtask> subtasksForEpic3 = new ArrayList<>();
        subtasksForEpic3.add(subtask5);
        subtasksForEpic3.add(subtask6);

        Subtask subtask7 = new Subtask(
                "Сходить на собеседование",
                "Подготовиться к собеседованию заранее",
                Status.NEW,
                15,
                LocalDateTime.of(2001, 10, 13, 13, 13)
        );

        ArrayList<Subtask> subtaskForEpic4 = new ArrayList<>();
        subtaskForEpic4.add(subtask7);

        Epic epic3 = new Epic(
                "Переезд",
                "Едем жить на море",
                Status.NEW,
                subtasksForEpic3
        );

        Epic epic4 = new Epic(
                "Найти работу",
                "Нужно получать денежки чтобы тратить",
                Status.NEW,
                subtaskForEpic4
        );

        actualFileBackedTasksManager.createNewTask(task1);
        actualFileBackedTasksManager.createNewTask(task2);
        actualFileBackedTasksManager.createNewEpic(epic3);
        actualFileBackedTasksManager.createNewEpic(epic4);
        actualFileBackedTasksManager.createNewSubtask(subtask5);
        actualFileBackedTasksManager.createNewSubtask(subtask6);
        actualFileBackedTasksManager.createNewSubtask(subtask7);
        actualFileBackedTasksManager.historyManager.add(actualFileBackedTasksManager.getTaskById(1));
        actualFileBackedTasksManager.historyManager.add(actualFileBackedTasksManager.getTaskById(6));
        actualFileBackedTasksManager.historyManager.add(actualFileBackedTasksManager.getTaskById(2));
        actualFileBackedTasksManager.save();

        compareTwoFiles(
                "src/test/resources/expected_tasks.csv",
                "src/test/resources/actual_tasks.csv"
        );
    }

    @Test
    void saveEmptyTasksAndHistory() throws IOException {
        FileBackedTasksManager actualFileBackedTasksManager = new FileBackedTasksManager(
                new File("src/test/resources/actual_empty_tasks.csv")
        );
        actualFileBackedTasksManager.save();

        compareTwoFiles(
                "src/test/resources/expected_empty_tasks.csv",
                "src/test/resources/actual_empty_tasks.csv"
        );
    }

    @Test
    void saveEpicWithoutSubtasksAndEmptyHistory() throws IOException {
        Epic epic = new Epic(
                "Переезд",
                "Едем жить на море",
                Status.NEW,
                new ArrayList<>()
        );

        FileBackedTasksManager actualFileBackedTasksManager = new FileBackedTasksManager(
                new File("src/test/resources/actual_epic_without_subtasks.csv")
        );
        actualFileBackedTasksManager.createNewEpic(epic);
        actualFileBackedTasksManager.save();
        compareTwoFiles(
                "src/test/resources/expected_epic_without_subtasks.csv",
                "src/test/resources/actual_epic_without_subtasks.csv"
        );
    }

    private void compareTwoFiles(String expectedFilePath, String actualFilePath) throws IOException {
        FileBackedTasksManager expectedFileBackedTasksManager = FileBackedTasksManager.loadFromFile(
                new File(expectedFilePath)
        );

        FileBackedTasksManager actualFileBackedTasksManager = FileBackedTasksManager.loadFromFile(
                new File(actualFilePath)
        );

        int i = 0;
        Task expectedTask;
        Task task;
        while (i < expectedFileBackedTasksManager.getTasks().size()) {
            expectedTask = expectedFileBackedTasksManager.getTasks().get(i);
            task = actualFileBackedTasksManager.getTasks().get(i);

            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getStartTime(), task.getStartTime());
            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDuration(), task.getDuration());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getType(), task.getType());

            expectedFileBackedTasksManager.getTasks().remove(i);

            i++;
        }

        i = 0;
        while (i < expectedFileBackedTasksManager.historyManager.getHistory().size()) {
            expectedTask = expectedFileBackedTasksManager.historyManager.getHistory().get(i);
            task = actualFileBackedTasksManager.historyManager.getHistory().get(i);

            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getStartTime(), task.getStartTime());
            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDuration(), task.getDuration());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getType(), task.getType());

            expectedFileBackedTasksManager.historyManager.remove(i);

            i++;
        }
    }
}