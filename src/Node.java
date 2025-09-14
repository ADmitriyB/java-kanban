public class Node<T> {
    Task task;
    Node<T> prev;
    Node<T> next;

    public Node(Task task, Node<T> prev, Node<T> next) {

        this.task = task;
        this.prev = prev;
        this.next = next;
    }
}
