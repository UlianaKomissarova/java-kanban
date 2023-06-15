package test;

import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager inMemoryHistoryManager;
    private Task task;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = new InMemoryHistoryManager();

        task = new Task(
            "Погулять",
            "Идем гулять в парк!",
            Status.NEW
        );
        task.setId(1);

        task2 = new Task(
            "Погулять",
            "Идем гулять в парк!",
            Status.NEW
        );
        task2.setId(2);

        task3 = new Task(
            "Погулять",
            "Идем гулять в парк!",
            Status.NEW
        );
        task3.setId(3);
    }

    @Test
    void add() {
        inMemoryHistoryManager.add(task);
        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void addEmptyHistory() {
        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(0, history.size(), "История пустая.");
    }

    @Test
    void addDuplicatedTasks() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);
        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void addManyDuplicatedTasks() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);
        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "История пустая.");
    }

    @Test
    void removeFirstTask() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task.getId());

        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "История пустая.");
        assertEquals(task2, inMemoryHistoryManager.getHistory().get(0));
        assertEquals(task3, inMemoryHistoryManager.getHistory().get(1));
    }

    @Test
    void removeMiddleTask() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task2.getId());

        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "История пустая.");
        assertEquals(task, inMemoryHistoryManager.getHistory().get(0));
        assertEquals(task3, inMemoryHistoryManager.getHistory().get(1));
    }

    @Test
    void removeLastTask() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task3.getId());

        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "История пустая.");
        assertEquals(task, inMemoryHistoryManager.getHistory().get(0));
        assertEquals(task2, inMemoryHistoryManager.getHistory().get(1));
    }

    @Test
    void getHistory() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);

        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(task);
        expectedHistory.add(task2);

        assertNotNull(inMemoryHistoryManager.getHistory(), "История равна null.");
        assertEquals(expectedHistory, inMemoryHistoryManager.getHistory(), "История не совпадает с ожидаемой");
    }

    @Test
    void getEmptyHistory() {
        List<Task> expectedHistory = new ArrayList<>();

        assertNotNull(inMemoryHistoryManager.getHistory(), "История равна null");
        assertEquals(expectedHistory, inMemoryHistoryManager.getHistory(), "История не совпадает с ожидаемой");
    }
}