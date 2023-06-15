package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private LocalDateTime endTime;
    private ArrayList<Subtask> subtasks;

    public Epic(Integer id, String name, String description, Status status, ArrayList<Subtask> subtasks) {
        super(name, description, status);
        super.setType(Type.EPIC);
        super.setId(id);
        this.subtasks = (subtasks == null ? new ArrayList<>() : subtasks);
        if (this.subtasks.isEmpty()) {
            return;
        }

        endTime = this.subtasks.get(0).getEndTime();
        startTime = this.subtasks.get(0).getStartTime();
        for (Subtask subtask : this.subtasks) {
            subtask.setEpicId(this.getId());

            if (subtask.getStartTime() == null || subtask.getEndTime() == null) {
                continue;
            }

            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }

            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }

            duration += subtask.getDuration();
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ArrayList<Subtask> getEpicSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
            "name='" + super.getName() + '\'' +
            ", description='" + super.getDescription() + '\'' +
            ", id='" + super.getId() + '\'' +
            ", status='" + super.getStatus() + '\'' +
            "epicSubtasks=" + subtasks + '\'' +
            ", type='" + Type.EPIC + '\'' +
            ", duration='" + duration + '\'' +
            ", startTime='" + (null != startTime ? startTime.toString() : "") + '\'' +
            '}';
    }
}