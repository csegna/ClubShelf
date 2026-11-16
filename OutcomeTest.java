import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Tests for the Outcome enum. ConsoleView has a message for each value, so
 * this checks that the list of values is the one the view expects.
 *
 * @author Angelo Pinillos-Sternberg
 * @version 2026.11.16
 */
public class OutcomeTest {

    /** The enum has exactly these eight values, in this order. */
    @Test
    public void testValues() {
        assertArrayEquals(new Outcome[] { Outcome.SUCCESS,
            Outcome.INVALID_STUDENT_ID, Outcome.ITEM_UNAVAILABLE,
            Outcome.ALREADY_BORROWED, Outcome.ALREADY_WAITING,
            Outcome.ITEM_AVAILABLE, Outcome.ITEM_NOT_BORROWED,
            Outcome.WRONG_BORROWER }, Outcome.values());
    }


    /** Each value can be looked up by its name. */
    @Test
    public void testValueOf() {
        for (Outcome outcome : Outcome.values()) {
            assertEquals(outcome, Outcome.valueOf(outcome.name()));
        }
    }


    /** A name that is not in the list is rejected. */
    @Test
    public void testValueOfBadName() {
        String[] bad = { "FAILED", "success", "", " SUCCESS" };
        for (String name : bad) {
            try {
                Outcome.valueOf(name);
                fail("Expected IllegalArgumentException for [" + name + "]");
            }
            catch (IllegalArgumentException expected) {
                // This is what should happen.
            }
        }
    }


    /** Looking up null throws a NullPointerException. */
    @Test(expected = NullPointerException.class)
    public void testValueOfNull() {
        Outcome.valueOf(null);
    }
}
