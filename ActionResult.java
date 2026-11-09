/**
 * What the model sends back after a borrow, waitlist, or return request.
 * It holds the Outcome, the student's place in line if they joined a
 * waitlist, and the student who got the item if a return gave it to the
 * next person in line.
 *
 * An ActionResult cannot change after it is made. There are no setters.
 *
 * @author Angelo Pinillos-Sternberg
 * @version 2026.11.09
 */
public class ActionResult {

    private final Outcome outcome;
    private final int queuePosition;
    private final String nextBorrowerId;

    /**
     * Makes a new ActionResult.
     *
     * @param outcome what happened, cannot be null
     * @param queuePosition the student's place in line starting at 1, or 0
     *     if the request had nothing to do with a waitlist. Cannot be
     *     negative.
     * @param nextBorrowerId the ID of the student who got the item after a
     *     return, or null if nobody was waiting or this was not a return
     * @throws IllegalArgumentException if outcome is null or queuePosition
     *     is less than 0
     */
    public ActionResult(Outcome outcome, int queuePosition,
        String nextBorrowerId) {
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome cannot be null");
        }
        if (queuePosition < 0) {
            throw new IllegalArgumentException(
                "Queue position cannot be negative");
        }
        this.outcome = outcome;
        this.queuePosition = queuePosition;
        this.nextBorrowerId = nextBorrowerId;
    }


    /**
     * Gets what happened with the request.
     *
     * @return the outcome
     */
    public Outcome getOutcome() {
        return outcome;
    }


    /**
     * Gets the student's place in line. The front of the line is 1.
     *
     * @return the place in line, or 0 if it does not apply
     */
    public int getQueuePosition() {
        return queuePosition;
    }


    /**
     * Gets the ID of the student who got the item after a return.
     *
     * @return that student's ID, or null if nobody was waiting
     */
    public String getNextBorrowerId() {
        return nextBorrowerId;
    }
}
