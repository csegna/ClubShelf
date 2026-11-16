import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ConsoleView. Each test gives the view a Scanner made from a
 * String and a PrintStream that writes into memory, so nothing is typed by
 * hand and the printed text can be checked.
 *
 * @author Angelo Pinillos-Sternberg
 * @version 2026.11.16
 */
public class ConsoleViewTest {

    private ByteArrayOutputStream printed;
    private ConsoleView view;

    /** Starts each test with a view that has no input. */
    @Before
    public void setUp() {
        view = viewWithInput("");
    }


    // Constructor

    /** A null Scanner is rejected. */
    @Test
    public void testNullInput() {
        try {
            new ConsoleView(null, System.out);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Input cannot be null", expected.getMessage());
        }
    }


    /** A null PrintStream is rejected. */
    @Test
    public void testNullOutput() {
        try {
            new ConsoleView(new Scanner(""), null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Output cannot be null", expected.getMessage());
        }
    }


    // readLine

    /** Lines come back exactly as typed, one at a time. */
    @Test
    public void testReadLine() {
        view = viewWithInput("2\n  001234567  \n\nabc\n");
        assertEquals("2", view.readLine("Choose: "));
        assertEquals("  001234567  ", view.readLine(null));
        assertEquals("", view.readLine(null));
        assertEquals("abc", view.readLine(null));
        assertEquals("Choose: ", printed());
    }


    /** A null prompt prints nothing. */
    @Test
    public void testReadLineNullPrompt() {
        view = viewWithInput("hello\n");
        assertEquals("hello", view.readLine(null));
        assertEquals("", printed());
    }


    /** Running out of input returns null instead of throwing. */
    @Test
    public void testReadLineEndOfInput() {
        assertNull(view.readLine("Choose: "));
        assertNull(view.readLine("Choose: "));
        assertEquals("Choose: Choose: ", printed());
    }


    /** The last line still counts even with no newline after it. */
    @Test
    public void testReadLineLastLineWithoutNewline() {
        view = viewWithInput("6");
        assertEquals("6", view.readLine(null));
        assertNull(view.readLine(null));
    }


    // showMenu

    /** The menu lists all seven choices in order. */
    @Test
    public void testShowMenu() {
        view.showMenu();
        assertArrayEquals(new String[] { "", "What would you like to do?",
            "  1. View a catalog", "  2. Borrow an item",
            "  3. Join a waitlist", "  4. Return an item",
            "  5. View a waitlist", "  6. Quit",
            "  7. Import club catalog" }, printedLines());
    }


    // showClubs

    /** Clubs are numbered starting at 1. */
    @Test
    public void testShowClubs() {
        view.showClubs(new String[] { "Photography Club", "Robotics Club" });
        assertArrayEquals(new String[] { "", "Choose a club:",
            "  1. Photography Club", "  2. Robotics Club" }, printedLines());
    }


    /** An empty list prints just the heading. */
    @Test
    public void testShowClubsEmpty() {
        view.showClubs(new String[0]);
        assertArrayEquals(new String[] { "", "Choose a club:" },
            printedLines());
    }


    /** A null list is rejected and nothing is printed. */
    @Test
    public void testShowClubsNull() {
        try {
            view.showClubs(null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Club names cannot be null", expected.getMessage());
        }
        assertEquals("", printed());
    }


    // showCatalog

    /** Each item shows whether it is available and how many are waiting. */
    @Test
    public void testShowCatalog() {
        view.showCatalog(new ItemStatus[] {
            new ItemStatus("Tent", true, null, new String[0]),
            new ItemStatus("Sleeping Bag", false, "001234567",
                new String[0]),
            new ItemStatus("Hiking Backpack", false, "001234567",
                new String[] { "002234567", "003234567" }) });
        assertArrayEquals(new String[] { "", "Equipment:",
            "  1. Tent  (Available)",
            "  2. Sleeping Bag  (Checked out)",
            "  3. Hiking Backpack  (Checked out, 2 waiting)" },
            printedLines());
    }


    /** A club with no items gets a message instead of an empty list. */
    @Test
    public void testShowCatalogEmpty() {
        view.showCatalog(new ItemStatus[0]);
        assertArrayEquals(new String[] { "",
            "This club has no equipment to show." }, printedLines());
    }


    /** A null catalog is rejected and nothing is printed. */
    @Test
    public void testShowCatalogNull() {
        try {
            view.showCatalog(null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Items cannot be null", expected.getMessage());
        }
        assertEquals("", printed());
    }


    // showWaitlist

    /** The waitlist prints front of the line first. */
    @Test
    public void testShowWaitlist() {
        view.showWaitlist(new ItemStatus("Tent", false, "001234567",
            new String[] { "002234567", "003234567" }));
        assertArrayEquals(new String[] { "", "Waitlist for Tent:",
            "  1. 002234567", "  2. 003234567" }, printedLines());
    }


    /** An empty waitlist says nobody is waiting. */
    @Test
    public void testShowWaitlistEmpty() {
        view.showWaitlist(new ItemStatus("Tent", true, null, new String[0]));
        assertArrayEquals(new String[] { "", "Nobody is waiting for Tent." },
            printedLines());
    }


    /** A null item is rejected and nothing is printed. */
    @Test
    public void testShowWaitlistNull() {
        try {
            view.showWaitlist(null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Item cannot be null", expected.getMessage());
        }
        assertEquals("", printed());
    }


    // showResult

    /** Joining a waitlist tells the student their spot. */
    @Test
    public void testShowResultJoined() {
        view.showResult(new ActionResult(Outcome.SUCCESS, 2, null));
        assertEquals("You joined the waitlist. You are number 2 in line.",
            onlyLine());
    }


    /** A return that hands the item on names the next student. */
    @Test
    public void testShowResultHandedOn() {
        view.showResult(new ActionResult(Outcome.SUCCESS, 0, "002234567"));
        assertEquals("Item returned. It now goes to student 002234567, "
            + "who was first in line.", onlyLine());
    }


    /** A borrow, or a return with nobody waiting, gets the plain message. */
    @Test
    public void testShowResultPlainSuccess() {
        view.showResult(new ActionResult(Outcome.SUCCESS, 0, null));
        assertEquals("Done. Your request went through.", onlyLine());
    }


    /** Each rejected outcome has its own message. */
    @Test
    public void testShowResultRejections() {
        assertResultMessage(Outcome.INVALID_STUDENT_ID,
            "That student ID is not valid. "
            + "Enter exactly 9 digits, with no spaces.");
        assertResultMessage(Outcome.ITEM_UNAVAILABLE,
            "Another student has this item right now. "
            + "You can join the waitlist for it.");
        assertResultMessage(Outcome.ALREADY_BORROWED,
            "You already have this item.");
        assertResultMessage(Outcome.ALREADY_WAITING,
            "You are already on the waitlist for this item.");
        assertResultMessage(Outcome.ITEM_AVAILABLE,
            "Nobody has this item, so you can borrow it now.");
        assertResultMessage(Outcome.ITEM_NOT_BORROWED,
            "This item is not checked out, so there is nothing to return.");
        assertResultMessage(Outcome.WRONG_BORROWER,
            "That ID does not match the student who has this item.");
    }


    /** Every outcome prints exactly one line, so none are missing. */
    @Test
    public void testShowResultEveryOutcome() {
        for (Outcome outcome : Outcome.values()) {
            view = viewWithInput("");
            view.showResult(new ActionResult(outcome, 0, null));
            assertEquals(outcome.name(), 1, printedLines().length);
        }
    }


    /** A null result is rejected and nothing is printed. */
    @Test
    public void testShowResultNull() {
        try {
            view.showResult(null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Result cannot be null", expected.getMessage());
        }
        assertEquals("", printed());
    }


    // showMessage

    /** A message prints on its own line. */
    @Test
    public void testShowMessage() {
        view.showMessage("Import complete.");
        view.showMessage("Goodbye!");
        assertArrayEquals(new String[] { "Import complete.", "Goodbye!" },
            printedLines());
    }


    /** A null or empty message does not crash the view. */
    @Test
    public void testShowMessageNullAndEmpty() {
        view.showMessage("");
        view.showMessage(null);
        assertEquals(2, printedLines().length);
    }


    // Helpers

    private ConsoleView viewWithInput(String input) {
        printed = new ByteArrayOutputStream();
        return new ConsoleView(new Scanner(input), new PrintStream(printed));
    }


    private String printed() {
        return printed.toString();
    }


    /**
     * Splits the printed text into lines. Blank lines in the middle are
     * kept, and the newline at the very end does not count as a line.
     */
    private String[] printedLines() {
        String text = printed();
        if (text.isEmpty()) {
            return new String[0];
        }
        return text.split("\\R");
    }


    private String onlyLine() {
        String[] lines = printedLines();
        assertEquals(1, lines.length);
        return lines[0];
    }


    private void assertResultMessage(Outcome outcome, String message) {
        view = viewWithInput("");
        view.showResult(new ActionResult(outcome, 0, null));
        assertEquals(message, onlyLine());
        assertTrue(printed().endsWith(System.lineSeparator()));
    }
}
