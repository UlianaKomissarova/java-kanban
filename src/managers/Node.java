package managers;

public class Node<Task> {
    public Task current;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Node<Task> prev, Task current, Node<Task> next) {
        this.current = current;
        this.next = next;
        this.prev = prev;
    }
}
