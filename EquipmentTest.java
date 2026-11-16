import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/** Tests equipment records and their read-only snapshots. */
public class EquipmentTest
{
    private Equipment item;

    @Before
    public void setUp()
    {
        item = new Equipment("Tent");
    }

    @Test
    public void testNewEquipment()
    {
        assertEquals("Tent", item.getName());
        assertTrue(item.isAvailable());
        assertNull(item.getBorrowerId());
        assertTrue(item.getWaitlist().isEmpty());
        assertTrue(item.getStatus().isAvailable());
        assertEquals(0, item.getStatus().getWaitlist().length);
    }

    @Test
    public void testInvalidNames()
    {
        String[] badNames = { null, "", "   " };
        for (String name : badNames)
        {
            try
            {
                new Equipment(name);
                fail("Expected an invalid equipment name to be rejected.");
            }
            catch (IllegalArgumentException expected)
            {
                assertTrue(expected.getMessage().contains("name"));
            }
        }
    }

    @Test
    public void testSetAndClearBorrower()
    {
        item.setBorrowerId("001234567");
        assertFalse(item.isAvailable());
        assertEquals("001234567", item.getBorrowerId());

        item.setBorrowerId(null);
        assertTrue(item.isAvailable());
        assertNull(item.getBorrowerId());
    }

    @Test
    public void testStatusIsAnIndependentSnapshot()
    {
        item.setBorrowerId("001234567");
        item.getWaitlist().enqueue("002234567");
        ItemStatus before = item.getStatus();

        String[] waiting = before.getWaitlist();
        waiting[0] = "999999999";
        item.setBorrowerId("003234567");
        item.getWaitlist().enqueue("004234567");

        assertEquals("Tent", before.getName());
        assertFalse(before.isAvailable());
        assertEquals("001234567", before.getBorrowerId());
        assertArrayEquals(new String[] { "002234567" }, before.getWaitlist());
        assertEquals("003234567", item.getStatus().getBorrowerId());
        assertArrayEquals(new String[] { "002234567", "004234567" },
            item.getStatus().getWaitlist());
    }
}
