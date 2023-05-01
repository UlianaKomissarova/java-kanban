package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public Node<Task> head;
    public Node<Task> tail;
    private HashMap<Integer, Node<Task>> taskHistory = new HashMap<>();

    public void linkLast(Task current) {
        if (taskHistory.size() == 0) {
            head = new Node<>(null, current, null);
            tail = head;
            taskHistory.put(current.getId(), tail);
        } else if (taskHistory.size() == 1) {
            final Node<Task> newNode = new Node<>(head, current, null);
            tail = newNode;
            head.next = newNode;
            taskHistory.put(current.getId(), tail);
        } else {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(tail, current, null);
            tail = newNode;
            oldTail.next = newNode;
            taskHistory.put(current.getId(), tail);
        }
    }

    public List<Task> getHistory() {
        List<Task> taskList = new ArrayList<>();
        Node<Task> currentTask = head;
        if (taskHistory.size() >= 1) {
            for (int i = 0; i < taskHistory.size(); i++) {
                Task task = currentTask.current;
                taskList.add(task);
                currentTask = currentTask.next;
            }
        }

        return taskList;
    }

    public void removeNode(Node<Task> node) {
        if (node.next == null) {
            Integer oldTail = node.current.getId();
            Node<Task> prev = tail.prev;
            prev.next = null;
            tail = prev;
            taskHistory.remove(oldTail);
        } else if (node.prev == null) {
            Integer oldHead = node.current.getId();
            Node<Task> next = head.next;
            next.prev = null;
            head = next;
            taskHistory.remove(oldHead);
        } else {
            Integer deletedNode = node.current.getId();
            node.prev.next = node.next;
            node.next.prev = node.prev;
            taskHistory.remove(deletedNode);
        }
    }

    @Override
    public void add(Task task) {
        if (taskHistory.containsKey(task.getId())) {
            removeNode(taskHistory.get(task.getId()));
        }

        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (taskHistory.containsKey(id)) {
            Task task = taskHistory.get(id).current;
            if (task instanceof Epic) {
                for (Subtask epicSubtask : ((Epic) task).getEpicSubtasks()) {
                    this.remove(epicSubtask.getId());
                }
            }

            removeNode(taskHistory.get(id));
        }
    }
}
