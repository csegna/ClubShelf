import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/** Tests club equipment lookup and the additions used by catalog import. */
public class ClubTest
{
    private Equipment camera;
    private Club club;

    @Before
    public void setUp()
    {
        camera = new Equipment("Camera");
        club = new Club("Photography Club", new Equipment[] { camera });
    }

    @Test
    public void testConstructorCopiesEquipmentArray()
    {
        Equipment[] items = { camera };
        club = new Club("Photography Club", items);
        items[0] = new Equipment("Flash");

        assertEquals("Photography Club", club.getName());
        assertEquals(1, club.getItemCount());
        assertSame(camera, club.getItem(1));
    }

    @Test
    public void testHasItem()
    {
        assertTrue(club.hasItem("Camera"));
        assertTrue(club.hasItem("camera"));
        assertFalse(club.hasItem("Flash"));
        assertFalse(club.hasItem(null));
    }

    @Test
    public void testAddItemKeepsExistingLoanAndWaitlist()
    {
        camera.setBorrowerId("001234567");
        camera.getWaitlist().enqueue("002234567");
        Equipment flash = new Equipment("Flash");

        club.addItem(flash);

        assertEquals(2, club.getItemCount());
        assertSame(camera, club.getItem(1));
        assertSame(flash, club.getItem(2));
        assertEquals("001234567", camera.getBorrowerId());
        assertArrayEquals(new Object[] { "002234567" },
            camera.getWaitlist().toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullItem()
    {
        club.addItem(null);
    }

    @Test
    public void testInvalidClubNames()
    {
        String[] badNames = { null, "", "   " };
        for (String name : badNames)
        {
            try
            {
                new Club(name, new Equipment[] { camera });
                fail("Expected an invalid club name to be rejected.");
            }
            catch (IllegalArgumentException expected)
            {
                assertTrue(expected.getMessage().contains("name"));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEquipmentArray()
    {
        new Club("Photography Club", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyEquipmentArray()
    {
        new Club("Photography Club", new Equipment[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEquipmentEntry()
    {
        new Club("Photography Club", new Equipment[] { camera, null });
    }

    @Test
    public void testInvalidItemNumbers()
    {
        int[] badNumbers = { -1, 0, 2 };
        for (int number : badNumbers)
        {
            try
            {
                club.getItem(number);
                fail("Expected an invalid item number to be rejected.");
            }
            catch (IllegalArgumentException expected)
            {
                assertEquals(1, club.getItemCount());
                assertSame(camera, club.getItem(1));
            }
        }
    }
}
