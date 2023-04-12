package managers;

import tasks.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList<Task> viewedTasks = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (viewedTasks.size() == 10) {
            viewedTasks.removeFirst();
        }
        viewedTasks.addLast(task);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return viewedTasks;
    }
}
