package test;

import managers.HttpTaskManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest {
    HttpTaskManager httpTaskManager;
    KVServer kvServer;

    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        httpTaskManager = (HttpTaskManager) Managers.getDefault();
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
    }

    @Test
    void save() throws IOException, InterruptedException {
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

        Epic epic3 = new Epic(
                3,
                "Переезд",
                "Едем жить на море",
                Status.NEW,
                subtasksForEpic3
        );

        httpTaskManager.createNewTask(task1);
        httpTaskManager.createNewTask(task2);
        httpTaskManager.createNewEpic(epic3);
        httpTaskManager.createNewSubtask(subtask5);
        httpTaskManager.createNewSubtask(subtask6);
        httpTaskManager.historyManager.add(httpTaskManager.getTaskById(1));
        httpTaskManager.historyManager.add(httpTaskManager.getTaskById(6));
        httpTaskManager.historyManager.add(httpTaskManager.getTaskById(2));
        httpTaskManager.getPrioritizedTasks();
        httpTaskManager.save();

        HttpTaskManager newTaskManager = (HttpTaskManager) Managers.getDefault();

        compareTwoManagers(httpTaskManager, newTaskManager);
    }

    private void compareTwoManagers(HttpTaskManager first, HttpTaskManager second) {
        for (Task firstPrioritizedTask : first.getPrioritizedTasks()) {
            for (Task secondPrioritizedTask : second.getPrioritizedTasks()) {
                assertEquals(firstPrioritizedTask, secondPrioritizedTask);
            }
        }


    }
}
