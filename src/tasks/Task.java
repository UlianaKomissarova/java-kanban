package tasks;

import managers.InMemoryTaskManager;

import java.time.LocalDateTime;

public class Task {
    private final String name;
    private final String description;
    private Integer id;
    private Status status;
    private Type type = Type.TASK;
    protected int duration = 0;
    protected LocalDateTime startTime = null;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration);
        } else return null;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id='" + InMemoryTaskManager.taskId + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + (null != startTime ? startTime.toString() : "") + '\'' +
                '}';
    }
}