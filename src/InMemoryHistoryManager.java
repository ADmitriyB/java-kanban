

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {



    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;


    @Override
    public void addTaskToHistory(Task task) {
      if (task == null) {
          return;
      }
         remove(task.getId());
         linkLast(task);

    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            history.add(node.task);
            node = node.next;
        }
        return history;
    }

    @Override
    public void remove(int id) {
        final Node<Task> node = nodeMap.remove(id);
        if (node == null) return;

        final Node<Task> prev = node.prev;
        final Node<Task> next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }

    }


    private void linkLast(Task task) {
        final Node<Task> newNode = new Node<>(task, tail, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }


}
