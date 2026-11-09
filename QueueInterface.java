/**
 * Attribution: Alexander (genAI-Assisted)
 * A first-in, first-out queue.
 *
 * @param <T> the type of entry stored in the queue
 */
public interface QueueInterface<T> {

    /**
     * Adds an entry to the back of the queue.
     *
     * @param entry the entry to be added
     */
    void enqueue(T entry);

    /**
     * Removes and returns the entry at the front of the queue.
     *
     * @return the entry at the front of the queue
     */
    T dequeue();

    /**
     * Returns the entry at the front of the queue without removing it.
     *
     * @return the entry at the front of the queue
     */
    T peek();

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    boolean isEmpty();
    
}