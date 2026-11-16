import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests LoanRecord: due dates, overdue checks, and closing a loan.
 *
 * @author Corbin Segna
 * @version 2026.11.16
 */
public class LoanRecordTest
{
    private static final LocalDate DAY_1 = LocalDate.of(2026, 11, 1);
    private static final LocalDate DUE = DAY_1.plusDays(LoanRecord.LOAN_DAYS);

    private LoanRecord loan;

    /** Creates an open loan that started on DAY_1. */
    @Before
    public void setUp()
    {
        loan = new LoanRecord("001234567", "Outdoor Club", "Tent", DAY_1);
    }


    /** A new loan stores its values and is still out. */
    @Test
    public void testConstructorAndGetters()
    {
        assertEquals("001234567", loan.getStudentId());
        assertEquals("Outdoor Club", loan.getClubName());
        assertEquals("Tent", loan.getItemName());
        assertEquals(DAY_1, loan.getBorrowedOn());
        assertNull(loan.getReturnedOn());
        assertFalse(loan.isReturned());
    }


    /** Null or blank values are rejected. */
    @Test
    public void testConstructorRejectsBadValues()
    {
        assertRejected(null, "Club", "Item", DAY_1);
        assertRejected(" ", "Club", "Item", DAY_1);
        assertRejected("001234567", "", "Item", DAY_1);
        assertRejected("001234567", "Club", null, DAY_1);
        assertRejected("001234567", "Club", "Item", null);
    }


    /** The due date is LOAN_DAYS after borrowing. */
    @Test
    public void testDueDate()
    {
        assertEquals(DAY_1.plusDays(7), loan.getDueDate());
    }


    /** Overdue only after the due date, never on it. */
    @Test
    public void testIsOverdueBoundary()
    {
        assertFalse(loan.isOverdue(DAY_1));
        assertFalse(loan.isOverdue(DUE));
        assertTrue(loan.isOverdue(DUE.plusDays(1)));
    }


    /** Checking overdue against a null date is rejected. */
    @Test(expected = IllegalArgumentException.class)
    public void testIsOverdueRejectsNull()
    {
        loan.isOverdue(null);
    }


    /** Returning creates a closed copy; the original is unchanged. */
    @Test
    public void testReturnedOnCreatesClosedCopy()
    {
        LoanRecord closed = loan.returnedOn(DUE);
        assertTrue(closed.isReturned());
        assertEquals(DUE, closed.getReturnedOn());
        assertFalse(closed.wasReturnedLate());
        assertFalse(closed.isOverdue(DUE.plusDays(30)));
        assertFalse(loan.isReturned());
    }


    /** A return after the due date is marked late. */
    @Test
    public void testLateReturn()
    {
        assertTrue(loan.returnedOn(DUE.plusDays(1)).wasReturnedLate());
        assertFalse(loan.wasReturnedLate());
    }


    /** A loan cannot be returned before it started. */
    @Test(expected = IllegalArgumentException.class)
    public void testReturnBeforeBorrowRejected()
    {
        loan.returnedOn(DAY_1.minusDays(1));
    }


    /** A loan cannot be returned twice. */
    @Test(expected = IllegalStateException.class)
    public void testReturnTwiceRejected()
    {
        loan.returnedOn(DUE).returnedOn(DUE);
    }


    private void assertRejected(
        String id,
        String club,
        String item,
        LocalDate day)
    {
        try
        {
            new LoanRecord(id, club, item, day);
            org.junit.Assert.fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
}
