package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Subtask subtaskWithTime1;
    Subtask subtaskWithTime2;
    ArrayList<Subtask> subtasksWithTime;
    ArrayList<Subtask> subtasksWithNullTime;
    Epic epicWithTime;
    Epic emptyEpic;
    Subtask subtaskWithNullTime;
    Epic epicWithNullTimeSubtask;

    @BeforeEach
    public void beforeEach() {
        subtaskWithTime1 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2020,11, 11, 11, 11)
        );

        subtaskWithTime2 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                20,
                LocalDateTime.of(2022, 11, 11, 11, 11)
        );

        subtaskWithNullTime = new Subtask(
                "test-name",
                "test-description",
                Status.NEW
        );

        subtasksWithTime = new ArrayList<>();
        subtasksWithTime.add(subtaskWithTime1);
        subtasksWithTime.add(subtaskWithTime2);

        epicWithTime = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithTime
        );

        emptyEpic = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                new ArrayList<>()
        );

        subtasksWithNullTime = new ArrayList<>();
        subtasksWithNullTime.add(subtaskWithNullTime);

        epicWithNullTimeSubtask = new Epic(
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithNullTime
        );
    }

    @Test
    void shouldGiveRightStartTime() {
        assertNotNull(epicWithTime.getStartTime(), "Время начала выполнения задачи не найдено");
        assertEquals(subtaskWithTime1.getStartTime(), epicWithTime.getStartTime(),
                "Неверно вычисляется время начала выполнения задачи");
    }

    @Test
    void shouldReturnNullStartTime() {
        assertNull(epicWithNullTimeSubtask.getStartTime(), "Время начала выполнения задачи должно быть null");
    }

    @Test
    void shouldGiveRightEndTime() {
        assertNotNull(epicWithTime.getEndTime(), "Время конца выполнения задачи не найдено");
        assertEquals(subtaskWithTime2.getEndTime(), epicWithTime.getEndTime(),
                "Неверно вычисляется время начала выполнения задачи");
    }

    @Test
    void shouldReturnNullEndTime() {
        assertNull(epicWithNullTimeSubtask.getEndTime(), "Время конца выполнения задачи должно быть null");
    }

    @Test
    void shouldGetRightDuration() {
        int expectedDuration = subtaskWithTime1.getDuration() + subtaskWithTime2.getDuration();
        assertEquals(expectedDuration, epicWithTime.getDuration(),
                "Продолжительность эпика рассчитывается неверно");
    }

    @Test
    void getEpicSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.addAll(subtasksWithTime);
        assertEquals(subtasks, epicWithTime.getEpicSubtasks(), "Список подзадач неверный");
    }

    @Test
    void getEpicSubtasksIfNull() {
        ArrayList<Subtask> emptyList = new ArrayList<>();
        assertEquals(emptyList, emptyEpic.getEpicSubtasks(), "Лист подзадач не пустой");
    }
}