/** Signals that an operation requires a front entry in an empty queue. */
public class EmptyQueueException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** Creates the exception with the standard empty-queue message. */
    public EmptyQueueException() {
        super("Queue is empty.");
    }
}
