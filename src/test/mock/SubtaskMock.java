package test.mock;

import tasks.Subtask;

public class SubtaskMock {
    private int epicId;
    private Subtask subtask;

    public SubtaskMock(int epicId, Subtask subtask) {
        this.epicId = epicId;
        this.subtask = subtask;
    }
}
