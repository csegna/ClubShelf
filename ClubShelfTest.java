import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/** Tests borrowing and returns without starting the console. */
public class ClubShelfTest
{
    private static final String BORROWER = "001234567";
    private static final String FIRST_IN_LINE = "002234567";
    private static final String SECOND_IN_LINE = "003234567";

    private ClubShelf shelf;

    @Before
    public void setUp()
    {
        shelf = new ClubShelf();
    }

    @Test
    public void testStartingCatalog()
    {
        assertEquals(3, shelf.getClubNames().length);
        for (int club = 1; club <= 3; club++)
        {
            ItemStatus[] items = shelf.getCatalog(club);
            assertEquals(3, items.length);
            for (ItemStatus item : items)
            {
                assertTrue(item.isAvailable());
                assertNull(item.getBorrowerId());
                assertEquals(0, item.getWaitlist().length);
            }
        }
    }

    @Test
    public void testReturnGivesItemToFirstStudentInLine()
    {
        assertEquals(Outcome.SUCCESS, shelf.borrowItem(1, 1, BORROWER).getOutcome());
        assertEquals(1, shelf.joinWaitlist(1, 1, FIRST_IN_LINE).getQueuePosition());
        assertEquals(2, shelf.joinWaitlist(1, 1, SECOND_IN_LINE).getQueuePosition());

        ActionResult result = shelf.returnItem(1, 1, BORROWER);
        assertEquals(Outcome.SUCCESS, result.getOutcome());
        assertEquals(FIRST_IN_LINE, result.getNextBorrowerId());

        ItemStatus item = shelf.getItemStatus(1, 1);
        assertFalse(item.isAvailable());
        assertEquals(FIRST_IN_LINE, item.getBorrowerId());
        assertArrayEquals(new String[] { SECOND_IN_LINE }, item.getWaitlist());
    }

    @Test
    public void testRejectedRequestsKeepTheWaitlist()
    {
        shelf.borrowItem(1, 1, BORROWER);
        shelf.joinWaitlist(1, 1, FIRST_IN_LINE);
        assertEquals(Outcome.ALREADY_WAITING,
            shelf.joinWaitlist(1, 1, FIRST_IN_LINE).getOutcome());
        assertEquals(Outcome.WRONG_BORROWER,
            shelf.returnItem(1, 1, SECOND_IN_LINE).getOutcome());

        ItemStatus item = shelf.getItemStatus(1, 1);
        assertEquals(BORROWER, item.getBorrowerId());
        assertArrayEquals(new String[] { FIRST_IN_LINE }, item.getWaitlist());
    }

    @Test
    public void testReturnWithNobodyWaitingMakesItemAvailable()
    {
        shelf.borrowItem(1, 1, BORROWER);
        ActionResult result = shelf.returnItem(1, 1, BORROWER);
        assertEquals(Outcome.SUCCESS, result.getOutcome());
        assertNull(result.getNextBorrowerId());
        assertTrue(shelf.getItemStatus(1, 1).isAvailable());
    }

    @Test
    public void testBadIdsDoNotBorrowItem()
    {
        String[] badIds = { null, "", "12345678", "1234567890", "1234 5678", "a12345678" };
        for (String id : badIds)
        {
            assertEquals(Outcome.INVALID_STUDENT_ID, shelf.borrowItem(1, 1, id).getOutcome());
            assertTrue(shelf.getItemStatus(1, 1).isAvailable());
        }
    }
}
