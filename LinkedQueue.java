/**
 * A singly linked FIFO queue with constant-time access to both ends.
 *
 * @param <T> the type of entry stored in the queue
 */
public class LinkedQueue<T> implements QueueInterface<T> {
    private Node<T> front;
    private Node<T> back;
    private int size;

    /** Creates an empty queue. */
    public LinkedQueue() {
        front = null;
        back = null;
        size = 0;
    }

    @Override
    public void enqueue(T entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Queue entry cannot be null.");
        }

        Node<T> newNode = new Node<>(entry, null);
        if (isEmpty()) {
            front = newNode;
        } else {
            back.next = newNode;
        }
        back = newNode;
        size++;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            throw new EmptyQueueException();
        }

        T entry = front.data;
        front = front.next;
        size--;
        // The next enqueue must start a new chain after the last removal.
        if (front == null) {
            back = null;
        }
        return entry;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyQueueException();
        }
        return front.data;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(T entry) {
        if (entry == null) {
            return false;
        }

        for (Node<T> node = front; node != null; node = node.next) {
            if (entry.equals(node.data)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        // Read the nodes in order without changing the front, back, or size.
        Object[] entries = new Object[size];
        Node<T> node = front;
        for (int index = 0; index < entries.length; index++) {
            entries[index] = node.data;
            node = node.next;
        }
        return entries;
    }

    private static final class Node<T> {
        private final T data;
        private Node<T> next;

        private Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }
}
