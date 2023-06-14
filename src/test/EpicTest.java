package test;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void shouldGiveRightStartTime() {
        Subtask subtaskWithTime1 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2020,11, 11, 11, 11)
        );

        Subtask subtaskWithTime2 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                20,
                LocalDateTime.of(2022, 11, 11, 11, 11)
        );

        ArrayList<Subtask> subtasksWithTime = new ArrayList<>();
        subtasksWithTime.add(subtaskWithTime1);
        subtasksWithTime.add(subtaskWithTime2);

        Epic epicWithTime = new Epic(
                1,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithTime
        );

        assertNotNull(epicWithTime.getStartTime(), "Время начала выполнения задачи не найдено");
        assertEquals(subtaskWithTime1.getStartTime(), epicWithTime.getStartTime(),
                "Неверно вычисляется время начала выполнения задачи");
    }

    @Test
    void shouldReturnNullStartTime() {
        Subtask subtaskWithNullTime = new Subtask(
                "test-name",
                "test-description",
                Status.NEW
        );

        ArrayList<Subtask> subtasksWithNullTime = new ArrayList<>();
        subtasksWithNullTime.add(subtaskWithNullTime);

        Epic epicWithNullTimeSubtask = new Epic(
                2,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithNullTime
        );

        assertNull(epicWithNullTimeSubtask.getStartTime(), "Время начала выполнения задачи должно быть null");
    }

    @Test
    void shouldGiveRightEndTime() {
        Subtask subtaskWithTime1 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2020,11, 11, 11, 11)
        );

        Subtask subtaskWithTime2 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                20,
                LocalDateTime.of(2022, 11, 11, 11, 11)
        );

        ArrayList<Subtask> subtasksWithTime = new ArrayList<>();
        subtasksWithTime.add(subtaskWithTime1);
        subtasksWithTime.add(subtaskWithTime2);

        Epic epicWithTime = new Epic(
                3,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithTime
        );

        assertNotNull(epicWithTime.getEndTime(), "Время конца выполнения задачи не найдено");
        assertEquals(subtaskWithTime2.getEndTime(), epicWithTime.getEndTime(),
                "Неверно вычисляется время начала выполнения задачи");
    }

    @Test
    void shouldReturnNullEndTime() {
        Subtask subtaskWithNullTime = new Subtask(
                "test-name",
                "test-description",
                Status.NEW
        );

        ArrayList<Subtask> subtasksWithNullTime = new ArrayList<>();
        subtasksWithNullTime.add(subtaskWithNullTime);

        Epic epicWithNullTimeSubtask = new Epic(
                4,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithNullTime
        );

        assertNull(epicWithNullTimeSubtask.getEndTime(), "Время конца выполнения задачи должно быть null");
    }

    @Test
    void shouldGetRightDuration() {
        Subtask subtaskWithTime1 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2020,11, 11, 11, 11)
        );

        Subtask subtaskWithTime2 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                20,
                LocalDateTime.of(2022, 11, 11, 11, 11)
        );

        ArrayList<Subtask> subtasksWithTime = new ArrayList<>();
        subtasksWithTime.add(subtaskWithTime1);
        subtasksWithTime.add(subtaskWithTime2);

        Epic epicWithTime = new Epic(
                5,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithTime
        );

        int expectedDuration = subtaskWithTime1.getDuration() + subtaskWithTime2.getDuration();
        assertEquals(expectedDuration, epicWithTime.getDuration(),
                "Продолжительность эпика рассчитывается неверно");
    }

    @Test
    void getEpicSubtasks() {
        Subtask subtaskWithTime1 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                30,
                LocalDateTime.of(2020,11, 11, 11, 11)
        );

        Subtask subtaskWithTime2 = new Subtask(
                "test-name",
                "test-description",
                Status.NEW,
                20,
                LocalDateTime.of(2022, 11, 11, 11, 11)
        );

        ArrayList<Subtask> subtasksWithTime = new ArrayList<>();
        subtasksWithTime.add(subtaskWithTime1);
        subtasksWithTime.add(subtaskWithTime2);

        Epic epicWithTime = new Epic(
                6,
                "test-name",
                "test-description",
                Status.NEW,
                subtasksWithTime
        );

        ArrayList<Subtask> subtasks = new ArrayList<>(subtasksWithTime);
        assertEquals(subtasks, epicWithTime.getEpicSubtasks(), "Список подзадач неверный");
    }

    @Test
    void getEpicSubtasksIfNull() {
        Epic emptyEpic = new Epic(
                7,
                "test-name",
                "test-description",
                Status.NEW,
                new ArrayList<>()
        );

        ArrayList<Subtask> emptyList = new ArrayList<>();
        assertEquals(emptyList, emptyEpic.getEpicSubtasks(), "Лист подзадач не пустой");
    }
}