import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the loan history feature added in Deliverable 4: ClubShelf's
 * getLoanHistory and getOverdueLoans, the ClubShelf(Clock) constructor,
 * and menu option 8. A test clock moves time forward so late loans can
 * be checked without waiting real days.
 *
 * @author Corbin Segna
 * @version 2026.11.16
 */
public class ClubShelfLoanHistoryTest
{
    private static final String A = "001234567";
    private static final String B = "002234567";
    private static final LocalDate DAY_1 = LocalDate.of(2026, 11, 1);

    private TestClock clock;
    private ClubShelf shelf;

    /** Starts every test on DAY_1 with a fresh shelf. */
    @Before
    public void setUp()
    {
        clock = new TestClock(DAY_1);
        shelf = new ClubShelf(clock);
    }


    /** A new shelf has no loans; the no-argument constructor still works. */
    @Test
    public void testNewShelfHasNoLoans()
    {
        assertEquals(0, shelf.getLoanHistory().length);
        assertEquals(0, shelf.getOverdueLoans().length);
        assertEquals(0, new ClubShelf().getLoanHistory().length);
    }


    /** A null clock is rejected. */
    @Test(expected = IllegalArgumentException.class)
    public void testNullClockRejected()
    {
        new ClubShelf(null);
    }


    /** A borrow opens a loan dated today. */
    @Test
    public void testBorrowRecordsLoan()
    {
        shelf.borrowItem(2, 1, A);
        LoanRecord[] history = shelf.getLoanHistory();
        assertEquals(1, history.length);
        assertEquals(A, history[0].getStudentId());
        assertEquals("Outdoor Club", history[0].getClubName());
        assertEquals("Tent", history[0].getItemName());
        assertEquals(DAY_1, history[0].getBorrowedOn());
        assertFalse(history[0].isReturned());
    }


    /** A return closes the loan and opens one for the next student. */
    @Test
    public void testReturnClosesLoanAndOpensHandoff()
    {
        shelf.borrowItem(1, 1, A);
        shelf.joinWaitlist(1, 1, B);
        clock.advanceDays(3);
        shelf.returnItem(1, 1, A);

        LoanRecord[] history = shelf.getLoanHistory();
        assertEquals(2, history.length);
        assertEquals(DAY_1.plusDays(3), history[0].getReturnedOn());
        assertEquals(B, history[1].getStudentId());
        assertEquals(DAY_1.plusDays(3), history[1].getBorrowedOn());
        assertFalse(history[1].isReturned());
    }


    /** Rejected requests never add or change loan records. */
    @Test
    public void testRejectedRequestsLeaveHistoryAlone()
    {
        shelf.borrowItem(1, 1, A);
        shelf.borrowItem(1, 1, B);
        shelf.borrowItem(1, 1, A);
        shelf.returnItem(1, 1, B);
        shelf.returnItem(1, 2, A);
        shelf.borrowItem(1, 1, "bad id");

        LoanRecord[] history = shelf.getLoanHistory();
        assertEquals(1, history.length);
        assertFalse(history[0].isReturned());
    }


    /** Changing the returned array does not change the history. */
    @Test
    public void testLoanHistoryIsACopy()
    {
        shelf.borrowItem(1, 1, A);
        LoanRecord[] history = shelf.getLoanHistory();
        history[0] = null;
        assertEquals(A, shelf.getLoanHistory()[0].getStudentId());
    }


    /** A loan becomes overdue only after LOAN_DAYS have passed. */
    @Test
    public void testOverdueLoans()
    {
        shelf.borrowItem(1, 1, A);
        clock.advanceDays(LoanRecord.LOAN_DAYS);
        assertEquals(0, shelf.getOverdueLoans().length);

        clock.advanceDays(1);
        LoanRecord[] overdue = shelf.getOverdueLoans();
        assertEquals(1, overdue.length);
        assertEquals(A, overdue[0].getStudentId());
    }


    /** Returned loans are never overdue, but a late return is flagged. */
    @Test
    public void testLateReturnIsRecordedNotOverdue()
    {
        shelf.borrowItem(1, 1, A);
        clock.advanceDays(LoanRecord.LOAN_DAYS + 2);
        shelf.returnItem(1, 1, A);

        assertEquals(0, shelf.getOverdueLoans().length);
        assertTrue(shelf.getLoanHistory()[0].wasReturnedLate());
    }


    /** The same student borrowing twice gets two separate records. */
    @Test
    public void testRepeatBorrowGetsNewRecord()
    {
        shelf.borrowItem(1, 1, A);
        shelf.returnItem(1, 1, A);
        clock.advanceDays(1);
        shelf.borrowItem(1, 1, A);

        LoanRecord[] history = shelf.getLoanHistory();
        assertEquals(2, history.length);
        assertTrue(history[0].isReturned());
        assertFalse(history[1].isReturned());
    }


    /** Menu option 8 shows loans and overdue follow-ups through the view. */
    @Test
    public void testHistoryThroughMenu()
    {
        String empty = runMenu(shelf, "8\n6\n");
        assertTrue(empty.contains("No loans yet."));

        shelf.borrowItem(1, 1, A);
        clock.advanceDays(LoanRecord.LOAN_DAYS + 1);
        String output = runMenu(shelf, "8\n6\n");
        assertTrue(output.contains("Loan history"));
        assertTrue(output.contains(A + "  DSLR Camera (Photography Club)"));
        assertTrue(output.contains("OVERDUE - follow up with:"));
    }


    private String runMenu(ClubShelf model, String input)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(input);
            PrintStream printer = new PrintStream(output))
        {
            ConsoleView view = new ConsoleView(scanner, printer);
            new ClubShelfController(model, view).run();
        }
        return output.toString();
    }


    /** A clock the tests can move forward one day at a time. */
    private static class TestClock extends Clock
    {
        private Instant now;

        TestClock(LocalDate start)
        {
            now = start.atStartOfDay(ZoneOffset.UTC).toInstant();
        }


        void advanceDays(int days)
        {
            now = now.plusSeconds(days * 86400L);
        }


        @Override
        public ZoneId getZone()
        {
            return ZoneOffset.UTC;
        }


        @Override
        public Clock withZone(ZoneId zone)
        {
            return this;
        }


        @Override
        public Instant instant()
        {
            return now;
        }
    }
}
