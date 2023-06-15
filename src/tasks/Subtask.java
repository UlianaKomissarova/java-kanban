package tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
        super.setType(Type.SUBTASK);
    }

    public Subtask(String name, String description, Status status, int duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        super.setType(Type.SUBTASK);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
            "name='" + super.getName() + '\'' +
            ", description='" + super.getDescription() + '\'' +
            ", id='" + super.getId() + '\'' +
            ", status='" + super.getStatus() + '\'' +
            ", epicId='" + epicId + '\'' +
            ", type='" + Type.SUBTASK + '\'' +
            ", duration='" + duration + '\'' +
            ", startTime='" + (null != startTime ? startTime.toString() : "") + '\'' +
            '}';
    }
}