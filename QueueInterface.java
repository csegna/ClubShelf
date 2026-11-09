/**
 * Attribution: Alexander (genAI-Assisted)
 * A first-in, first-out queue.
 *
 * @param <T> the type of entry stored in the queue
 */
public interface QueueInterface<T> {
    /**
     * Adds a non-null entry to the back of the queue.
     *
     * @param entry the entry to add
     * @throws IllegalArgumentException if entry is null
     */
    void enqueue(T entry);

    /**
     * Removes and returns the front entry.
     *
     * @return the removed entry
     * @throws EmptyQueueException if the queue is empty
     */
    T dequeue();

    /**
     * Reads the front entry without removing it.
     *
     * @return the front entry
     * @throws EmptyQueueException if the queue is empty
     */
    T peek();

    /**
     * @return whether the queue contains no entries
     */
    boolean isEmpty();

    /**
     * @return the number of entries in the queue
     */
    int size();

    /**
     * Searches the queue using equals.
     *
     * @param entry the value to look for
     * @return whether a matching value is present
     */
    boolean contains(T entry);

    /**
     * @return a new array of entries in front-to-back order
     */
    Object[] toArray();
}
