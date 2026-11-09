import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/** Tests the linked queue. */
public class LinkedQueueTest
{
    private LinkedQueue<String> queue;

    @Before
    public void setUp()
    {
        queue = new LinkedQueue<String>();
    }


    @Test
    public void testNewQueue()
    {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertEquals(0, queue.toArray().length);
    }


    @Test
    public void testEnqueueAndDequeue()
    {
        queue.enqueue("first");
        queue.enqueue("second");
        assertFalse(queue.isEmpty());
        assertEquals(2, queue.size());
        assertEquals("first", queue.dequeue());
        assertEquals(1, queue.size());
        assertEquals("second", queue.dequeue());
        assertTrue(queue.isEmpty());

        queue.enqueue("third");
        assertEquals("third", queue.dequeue());
    }


    @Test
    public void testPeek()
    {
        queue.enqueue("first");
        queue.enqueue("second");
        assertEquals("first", queue.peek());
        assertEquals(2, queue.size());
        assertEquals("first", queue.dequeue());
    }


    @Test
    public void testContains()
    {
        queue.enqueue("first");
        queue.enqueue("second");
        assertTrue(queue.contains(new String("second")));
        assertFalse(queue.contains("third"));
        assertFalse(queue.contains(null));
    }


    @Test
    public void testToArray()
    {
        queue.enqueue("first");
        queue.enqueue("second");
        queue.enqueue("first");

        Object[] entries = queue.toArray();
        assertEquals(3, entries.length);
        assertEquals("first", entries[0]);
        assertEquals("second", entries[1]);
        assertEquals("first", entries[2]);

        entries[0] = "changed";
        assertEquals("first", queue.peek());
        assertEquals(3, queue.size());
    }


    @Test(expected = EmptyQueueException.class)
    public void testEmptyDequeue()
    {
        queue.dequeue();
    }


    @Test(expected = EmptyQueueException.class)
    public void testEmptyPeek()
    {
        queue.peek();
    }


    @Test
    public void testNullEntry()
    {
        queue.enqueue("first");
        try
        {
            queue.enqueue(null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected)
        {
            assertEquals(1, queue.size());
            assertEquals("first", queue.peek());
        }
    }
}
