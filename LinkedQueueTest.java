import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/** Tests the queue through its public interface. */
public class LinkedQueueTest {
    private QueueInterface<String> queue;

    @Before
    public void setUp() {
        queue = new LinkedQueue<>();
    }

    @Test
    public void newQueueIsEmpty() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertArrayEquals(new Object[0], queue.toArray());
    }

    @Test
    public void dequeueReturnsEntriesInArrivalOrder() {
        queue.enqueue("001234567");
        queue.enqueue("002234567");

        assertFalse(queue.isEmpty());
        assertEquals(2, queue.size());
        assertEquals("001234567", queue.dequeue());
        assertEquals(1, queue.size());
        assertEquals("002234567", queue.dequeue());
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    public void queueCanBeReusedAfterRemovingLastEntry() {
        queue.enqueue("first");
        assertEquals("first", queue.dequeue());

        queue.enqueue("second");
        queue.enqueue("third");
        assertEquals("second", queue.dequeue());
        assertEquals("third", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    public void peekLeavesEntriesInPlace() {
        queue.enqueue("first");
        queue.enqueue("second");

        assertEquals("first", queue.peek());
        assertEquals(2, queue.size());
        assertEquals("first", queue.dequeue());
        assertEquals("second", queue.dequeue());
    }

    @Test(expected = EmptyQueueException.class)
    public void dequeueOnEmptyQueueThrows() {
        queue.dequeue();
    }

    @Test(expected = EmptyQueueException.class)
    public void peekOnEmptyQueueThrows() {
        queue.peek();
    }

    @Test
    public void containsUsesValueEquality() {
        queue.enqueue("first");
        queue.enqueue("second");

        // A distinct String object with the same text should match.
        assertTrue(queue.contains(new String("second")));
        assertFalse(queue.contains("missing"));
        assertFalse(queue.contains(null));
        assertArrayEquals(new Object[] {"first", "second"}, queue.toArray());
    }

    @Test
    public void toArrayCopiesEntriesInOrderIncludingDuplicates() {
        queue.enqueue("first");
        queue.enqueue("second");
        queue.enqueue("first");

        Object[] expected = {"first", "second", "first"};
        Object[] snapshot = queue.toArray();
        assertArrayEquals(expected, snapshot);
        snapshot[0] = "changed";
        assertArrayEquals(expected, queue.toArray());
    }

    @Test
    public void enqueueRejectsNullAndKeepsTheQueueIntact() {
        queue.enqueue("first");

        try {
            queue.enqueue(null);
            fail("Expected enqueue(null) to throw IllegalArgumentException.");
        } catch (IllegalArgumentException expected) {
            // Continue to check that the rejected entry did not change the queue.
        }
        assertEquals(1, queue.size());
        assertEquals("first", queue.dequeue());
        assertTrue(queue.isEmpty());
    }
}
