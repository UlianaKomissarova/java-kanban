package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private LocalDateTime endTime;
    private final ArrayList<Subtask> subtasks;

    public Epic(String name, String description, Status status, ArrayList<Subtask> subtasks) {
        super(name, description, status);
        super.setType(Type.EPIC);
        this.subtasks = subtasks;
        if (subtasks.isEmpty()) {
            return;
        }

        endTime = subtasks.get(0).getEndTime();
        startTime = subtasks.get(0).getStartTime();
        for (Subtask subtask : subtasks) {
            subtask.setEpic(this);

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

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id='" + super.getId() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                "epicSubtasks=" + subtasks +
                '}';
    }
}