import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Test;

/** Tests the menu using typed input supplied by the test. */
public class ClubShelfControllerTest
{
    @Test
    public void testBorrowJoinAndReturnThroughMenu()
    {
        ClubShelf shelf = new ClubShelf();
        String input = String.join("\n",
            "2", "1", "1", "001234567",  // Borrow club 1, item 1.
            "3", "1", "1", "002234567",  // Join its waitlist.
            "4", "1", "1", "001234567",  // Return it.
            "6");

        String output = runMenu(shelf, input);

        assertTrue(output.contains("number 1 in line"));
        assertTrue(output.contains("now goes to student 002234567"));
        assertEquals("002234567", shelf.getItemStatus(1, 1).getBorrowerId());
        assertEquals(0, shelf.getItemStatus(1, 1).getWaitlist().length);
    }

    @Test
    public void testBadMenuInputAndEndOfInput()
    {
        String output = runMenu(new ClubShelf(), "wrong\n99\n2\n");

        assertTrue(output.contains("Please enter a number"));
        assertTrue(output.contains("Please enter a choice from 1 to 6"));
        assertTrue(output.contains("Goodbye!"));
    }

    private String runMenu(ClubShelf shelf, String input)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(input);
            PrintStream printer = new PrintStream(output))
        {
            ConsoleView view = new ConsoleView(scanner, printer);
            new ClubShelfController(shelf, view).run();
        }
        return output.toString();
    }
}
