import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Tests for ActionResult. The constructor is the only place a result can be
 * set up, so most of these tests check what it accepts and what it rejects.
 *
 * @author Angelo Pinillos-Sternberg
 * @version 2026.11.16
 */
public class ActionResultTest {

    /** A plain result keeps its outcome and has no position or borrower. */
    @Test
    public void testPlainResult() {
        ActionResult result = new ActionResult(Outcome.ITEM_UNAVAILABLE, 0,
            null);
        assertEquals(Outcome.ITEM_UNAVAILABLE, result.getOutcome());
        assertEquals(0, result.getQueuePosition());
        assertNull(result.getNextBorrowerId());
    }


    /** A waitlist join keeps the student's spot in line. */
    @Test
    public void testResultWithQueuePosition() {
        ActionResult result = new ActionResult(Outcome.SUCCESS, 3, null);
        assertEquals(Outcome.SUCCESS, result.getOutcome());
        assertEquals(3, result.getQueuePosition());
        assertNull(result.getNextBorrowerId());
    }


    /** A return keeps the ID of the student who got the item next. */
    @Test
    public void testResultWithNextBorrower() {
        ActionResult result = new ActionResult(Outcome.SUCCESS, 0,
            "002234567");
        assertEquals(0, result.getQueuePosition());
        assertEquals("002234567", result.getNextBorrowerId());
    }


    /** Every outcome can be stored in a result. */
    @Test
    public void testEveryOutcome() {
        for (Outcome outcome : Outcome.values()) {
            assertEquals(outcome,
                new ActionResult(outcome, 0, null).getOutcome());
        }
    }


    /** The getters give the same answer every time they are called. */
    @Test
    public void testGettersDoNotChangeResult() {
        ActionResult result = new ActionResult(Outcome.SUCCESS, 2, null);
        result.getQueuePosition();
        result.getOutcome();
        assertEquals(2, result.getQueuePosition());
        assertEquals(Outcome.SUCCESS, result.getOutcome());
    }


    /** A null outcome is rejected. */
    @Test
    public void testNullOutcome() {
        try {
            new ActionResult(null, 0, null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Outcome cannot be null", expected.getMessage());
        }
    }


    /** A negative queue position is rejected. */
    @Test
    public void testNegativeQueuePosition() {
        int[] bad = { -1, -100, Integer.MIN_VALUE };
        for (int position : bad) {
            try {
                new ActionResult(Outcome.SUCCESS, position, null);
                fail("Expected IllegalArgumentException for " + position);
            }
            catch (IllegalArgumentException expected) {
                assertEquals("Queue position cannot be negative",
                    expected.getMessage());
            }
        }
    }


    /** When both values are bad, the outcome is checked first. */
    @Test
    public void testNullOutcomeAndNegativePosition() {
        try {
            new ActionResult(null, -1, null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Outcome cannot be null", expected.getMessage());
        }
    }
}
