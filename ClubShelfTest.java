import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests borrowing and returns without starting the console. */
public class ClubShelfTest
{
    private static final String BORROWER = "001234567";
    private static final String FIRST_IN_LINE = "002234567";
    private static final String SECOND_IN_LINE = "003234567";

    private ClubShelf shelf;

    @Rule
    public TemporaryFolder files = new TemporaryFolder();

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

    @Test
    public void testClubNamesAreCopied()
    {
        String[] names = shelf.getClubNames();
        assertArrayEquals(new String[] { "Photography Club", "Outdoor Club",
            "Gamers Group" }, names);
        names[0] = "Changed";
        assertEquals("Photography Club", shelf.getClubNames()[0]);
    }

    @Test
    public void testCatalogAndItemStatusAreSnapshots()
    {
        shelf.borrowItem(1, 1, BORROWER);
        shelf.joinWaitlist(1, 1, FIRST_IN_LINE);
        ItemStatus[] catalog = shelf.getCatalog(1);
        ItemStatus before = shelf.getItemStatus(1, 1);
        catalog[0] = null;
        before.getWaitlist()[0] = "999999999";

        shelf.returnItem(1, 1, BORROWER);

        assertEquals(BORROWER, before.getBorrowerId());
        assertArrayEquals(new String[] { FIRST_IN_LINE }, before.getWaitlist());
        assertEquals(FIRST_IN_LINE, shelf.getCatalog(1)[0].getBorrowerId());
    }

    @Test
    public void testBorrowUnavailableItem()
    {
        shelf.borrowItem(1, 1, BORROWER);
        assertEquals(Outcome.ALREADY_BORROWED,
            shelf.borrowItem(1, 1, BORROWER).getOutcome());
        assertEquals(Outcome.ITEM_UNAVAILABLE,
            shelf.borrowItem(1, 1, FIRST_IN_LINE).getOutcome());
        assertEquals(BORROWER, shelf.getItemStatus(1, 1).getBorrowerId());
        assertEquals(0, shelf.getItemStatus(1, 1).getWaitlist().length);
    }

    @Test
    public void testWaitlistRejectsAvailableItemAndCurrentBorrower()
    {
        assertEquals(Outcome.ITEM_AVAILABLE,
            shelf.joinWaitlist(1, 1, FIRST_IN_LINE).getOutcome());
        shelf.borrowItem(1, 1, BORROWER);
        assertEquals(Outcome.ALREADY_BORROWED,
            shelf.joinWaitlist(1, 1, BORROWER).getOutcome());
        assertEquals(0, shelf.getItemStatus(1, 1).getWaitlist().length);
    }

    @Test
    public void testBadIdsCannotJoinOrReturn()
    {
        shelf.borrowItem(1, 1, BORROWER);
        shelf.joinWaitlist(1, 1, FIRST_IN_LINE);
        String[] badIds = { null, "", "12345678", "1234567890",
            "1234 5678", "a12345678" };
        for (String id : badIds)
        {
            assertEquals(Outcome.INVALID_STUDENT_ID,
                shelf.joinWaitlist(1, 1, id).getOutcome());
            assertEquals(Outcome.INVALID_STUDENT_ID,
                shelf.returnItem(1, 1, id).getOutcome());
            assertEquals(BORROWER, shelf.getItemStatus(1, 1).getBorrowerId());
            assertArrayEquals(new String[] { FIRST_IN_LINE },
                shelf.getItemStatus(1, 1).getWaitlist());
        }
    }

    @Test
    public void testReturnUnborrowedItemAndRepeatReturn()
    {
        assertEquals(Outcome.ITEM_NOT_BORROWED,
            shelf.returnItem(1, 1, BORROWER).getOutcome());
        shelf.borrowItem(1, 1, BORROWER);
        shelf.returnItem(1, 1, BORROWER);
        assertEquals(Outcome.ITEM_NOT_BORROWED,
            shelf.returnItem(1, 1, BORROWER).getOutcome());
        assertTrue(shelf.getItemStatus(1, 1).isAvailable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCatalogRejectsClubZero()
    {
        shelf.getCatalog(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCatalogRejectsClubPastEnd()
    {
        shelf.getCatalog(4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStatusRejectsItemZero()
    {
        shelf.getItemStatus(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStatusRejectsItemPastEnd()
    {
        shelf.getItemStatus(1, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBorrowRejectsInvalidClub()
    {
        shelf.borrowItem(0, 1, BORROWER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBorrowRejectsInvalidItem()
    {
        shelf.borrowItem(1, 4, BORROWER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinRejectsInvalidClub()
    {
        shelf.joinWaitlist(4, 1, FIRST_IN_LINE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinRejectsInvalidItem()
    {
        shelf.joinWaitlist(1, 0, FIRST_IN_LINE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReturnRejectsInvalidClub()
    {
        shelf.returnItem(0, 1, BORROWER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReturnRejectsInvalidItem()
    {
        shelf.returnItem(1, 4, BORROWER);
    }

    @Test
    public void testImportAddsSelectableClubs() throws IOException
    {
        String path = writeCatalog("SCUBA VT Club | BC | REGS | Tanks\n"
            + "Robotics Club | Multimeter");

        assertEquals(4, shelf.importCatalog(path));
        assertArrayEquals(new String[] { "Photography Club", "Outdoor Club",
            "Gamers Group", "SCUBA VT Club", "Robotics Club" },
            shelf.getClubNames());
        assertEquals(3, shelf.getCatalog(4).length);
        assertEquals("Tanks", shelf.getItemStatus(4, 3).getName());
        assertEquals(1, shelf.getCatalog(5).length);
        assertEquals(Outcome.SUCCESS,
            shelf.borrowItem(4, 3, BORROWER).getOutcome());
        assertEquals(BORROWER, shelf.getItemStatus(4, 3).getBorrowerId());
    }

    @Test
    public void testImportMergesEquipmentAndKeepsLoans() throws IOException
    {
        shelf.borrowItem(1, 1, BORROWER);
        shelf.joinWaitlist(1, 1, FIRST_IN_LINE);
        shelf.joinWaitlist(1, 1, SECOND_IN_LINE);
        String path = writeCatalog("photography club | dslr camera | Flash\n"
            + "SCUBA VT Club | BC");

        assertEquals(2, shelf.importCatalog(path));
        assertEquals(4, shelf.getClubNames().length);
        assertEquals(4, shelf.getCatalog(1).length);
        assertEquals("Flash", shelf.getItemStatus(1, 4).getName());
        assertEquals(BORROWER, shelf.getItemStatus(1, 1).getBorrowerId());
        assertArrayEquals(new String[] { FIRST_IN_LINE, SECOND_IN_LINE },
            shelf.getItemStatus(1, 1).getWaitlist());
        assertEquals(FIRST_IN_LINE,
            shelf.returnItem(1, 1, BORROWER).getNextBorrowerId());
    }

    @Test
    public void testRepeatedImportSkipsExistingEquipment() throws IOException
    {
        String path = writeCatalog("SCUBA VT Club | BC | REGS | Tanks");
        assertEquals(3, shelf.importCatalog(path));
        shelf.borrowItem(4, 1, BORROWER);
        shelf.joinWaitlist(4, 1, FIRST_IN_LINE);

        assertEquals(0, shelf.importCatalog(path));
        assertEquals(4, shelf.getClubNames().length);
        assertEquals(3, shelf.getCatalog(4).length);
        assertEquals(BORROWER, shelf.getItemStatus(4, 1).getBorrowerId());
        assertArrayEquals(new String[] { FIRST_IN_LINE },
            shelf.getItemStatus(4, 1).getWaitlist());
    }

    @Test
    public void testRejectedImportsLeaveEverythingUnchanged() throws IOException
    {
        shelf.borrowItem(1, 1, BORROWER);
        shelf.joinWaitlist(1, 1, FIRST_IN_LINE);
        String[] originalClubs = shelf.getClubNames();
        String[] badCatalogs = { "New Club | Meter\nBroken row",
            "Photography Club | Flash\nSCUBA VT Club | BC |",
            "New Club | Meter\nnew club | Wrench",
            "New Club | Meter | meter", "# No clubs", "" };

        for (String text : badCatalogs)
        {
            String path = writeCatalog(text);
            try
            {
                shelf.importCatalog(path);
                fail("Expected an invalid import to be rejected.");
            }
            catch (IllegalArgumentException expected)
            {
                assertArrayEquals(originalClubs, shelf.getClubNames());
                for (int club = 1; club <= 3; club++)
                {
                    assertEquals(3, shelf.getCatalog(club).length);
                }
                assertEquals(BORROWER,
                    shelf.getItemStatus(1, 1).getBorrowerId());
                assertArrayEquals(new String[] { FIRST_IN_LINE },
                    shelf.getItemStatus(1, 1).getWaitlist());
            }
        }
    }

    @Test
    public void testMissingImportFileKeepsExistingLoan() throws IOException
    {
        shelf.borrowItem(1, 1, BORROWER);
        shelf.joinWaitlist(1, 1, FIRST_IN_LINE);
        String path = new File(files.getRoot(), "missing.txt").getPath();
        try
        {
            shelf.importCatalog(path);
            fail("Expected the missing file to be rejected.");
        }
        catch (IOException expected)
        {
            assertEquals(3, shelf.getClubNames().length);
            assertEquals(BORROWER, shelf.getItemStatus(1, 1).getBorrowerId());
            assertArrayEquals(new String[] { FIRST_IN_LINE },
                shelf.getItemStatus(1, 1).getWaitlist());
        }
    }

    @Test
    public void testBlankImportFileNames() throws IOException
    {
        String[] badNames = { null, "", "   " };
        for (String name : badNames)
        {
            try
            {
                shelf.importCatalog(name);
                fail("Expected the blank file name to be rejected.");
            }
            catch (IllegalArgumentException expected)
            {
                assertEquals(3, shelf.getClubNames().length);
                assertTrue(shelf.getItemStatus(1, 1).isAvailable());
            }
        }
    }

    private String writeCatalog(String text) throws IOException
    {
        File file = files.newFile();
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
        return file.getAbsolutePath();
    }
}
