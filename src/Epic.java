import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description, Status status, ArrayList<Subtask> subtasks) {
        super(name, description, status);
        this.subtasks = subtasks;

        for (Subtask subtask : subtasks) {
            subtask.setEpic(this);
        }
    }

    public ArrayList<Subtask> getEpicSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id='" + super.getId() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                "epicSubtasks=" + subtasks +
                '}';
    }
}