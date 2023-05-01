package tasks;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id='" + super.getId() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", epicName='" + epic.getName() + "'" +
                '}';
    }
}