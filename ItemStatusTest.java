import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Tests for ItemStatus. A status is a snapshot, so these tests also make
 * sure nobody can change it by editing the arrays that go in or come out.
 *
 * @author Angelo Pinillos-Sternberg
 * @version 2026.11.16
 */
public class ItemStatusTest {

    /** An available item has no borrower and nobody waiting. */
    @Test
    public void testAvailableItem() {
        ItemStatus status = new ItemStatus("Tripod", true, null,
            new String[0]);
        assertEquals("Tripod", status.getName());
        assertTrue(status.isAvailable());
        assertNull(status.getBorrowerId());
        assertEquals(0, status.getWaitlist().length);
    }


    /** A checked out item keeps its borrower and waitlist in order. */
    @Test
    public void testCheckedOutItem() {
        ItemStatus status = new ItemStatus("Tent", false, "001234567",
            new String[] { "002234567", "003234567" });
        assertEquals("Tent", status.getName());
        assertFalse(status.isAvailable());
        assertEquals("001234567", status.getBorrowerId());
        assertArrayEquals(new String[] { "002234567", "003234567" },
            status.getWaitlist());
    }


    /** Changing the array after making the status does not change it. */
    @Test
    public void testWaitlistCopiedOnTheWayIn() {
        String[] waiting = { "002234567", "003234567" };
        ItemStatus status = new ItemStatus("Tent", false, "001234567",
            waiting);
        waiting[0] = "999999999";
        assertEquals("002234567", status.getWaitlist()[0]);
    }


    /** Changing the array from getWaitlist does not change the status. */
    @Test
    public void testWaitlistCopiedOnTheWayOut() {
        ItemStatus status = new ItemStatus("Tent", false, "001234567",
            new String[] { "002234567" });
        String[] first = status.getWaitlist();
        first[0] = "999999999";
        assertEquals("002234567", status.getWaitlist()[0]);
        assertNotSame(status.getWaitlist(), status.getWaitlist());
    }


    /** IDs with leading zeros come back exactly the same. */
    @Test
    public void testLeadingZerosKept() {
        ItemStatus status = new ItemStatus("Tent", false, "000000001",
            new String[] { "000000002" });
        assertEquals("000000001", status.getBorrowerId());
        assertEquals("000000002", status.getWaitlist()[0]);
    }


    /** A blank name is still allowed. The model decides what names are OK. */
    @Test
    public void testBlankNameAllowed() {
        ItemStatus status = new ItemStatus("", true, null, new String[0]);
        assertEquals("", status.getName());
    }


    /** A null name is rejected. */
    @Test
    public void testNullName() {
        try {
            new ItemStatus(null, true, null, new String[0]);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Name cannot be null", expected.getMessage());
        }
    }


    /** A null waitlist is rejected. An empty array has to be used instead. */
    @Test
    public void testNullWaitlist() {
        try {
            new ItemStatus("Tent", false, "001234567", null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Waitlist cannot be null", expected.getMessage());
        }
    }


    /** When both are null, the name is checked first. */
    @Test
    public void testNullNameAndWaitlist() {
        try {
            new ItemStatus(null, true, null, null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Name cannot be null", expected.getMessage());
        }
    }
}
