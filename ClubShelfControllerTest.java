import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests the menu using typed input supplied by the test. */
public class ClubShelfControllerTest
{
    @Rule
    public TemporaryFolder files = new TemporaryFolder();

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
        assertTrue(output.contains("Please enter a choice from 1 to 7"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    public void testImportAndBorrowThroughMenu() throws IOException
    {
        ClubShelf shelf = new ClubShelf();
        String path = writeCatalog("SCUBA VT Club | BC | REGS | Tanks");
        String input = String.join("\n", "7", "  " + path + "  ",
            "1", "4", "2", "4", "3", "001234567", "6");

        String output = runMenu(shelf, input);

        assertTrue(output.contains("Added 1 club(s) and 3 equipment item(s)"));
        assertTrue(output.contains("SCUBA VT Club"));
        assertTrue(output.contains("Tanks"));
        assertEquals("001234567", shelf.getItemStatus(4, 3).getBorrowerId());
    }

    @Test
    public void testMissingImportFileReturnsToMenu()
    {
        ClubShelf shelf = new ClubShelf();
        String path = new File(files.getRoot(), "missing.txt").getPath();
        String input = String.join("\n", "7", path, "1", "1", "6");

        String output = runMenu(shelf, input);

        assertTrue(output.contains("Could not read catalog:"));
        assertTrue(output.contains("Equipment:"));
        assertTrue(output.contains("Goodbye!"));
        assertEquals(3, shelf.getClubNames().length);
    }

    @Test
    public void testInvalidImportDoesNotStopBorrowing() throws IOException
    {
        ClubShelf shelf = new ClubShelf();
        String path = writeCatalog("SCUBA VT Club | BC | ");
        String input = String.join("\n", "7", path,
            "2", "1", "1", "001234567", "6");

        String output = runMenu(shelf, input);

        assertTrue(output.contains("Import failed:"));
        assertEquals(3, shelf.getClubNames().length);
        assertEquals("001234567", shelf.getItemStatus(1, 1).getBorrowerId());
    }

    @Test
    public void testInvalidSelectionsAndIdRecover()
    {
        ClubShelf shelf = new ClubShelf();
        String input = String.join("\n", "", "wrong", "2147483648",
            "0", "8", "2", "0", "4", "1", "0", "4", "1",
            "bad", "001234567", "6");

        String output = runMenu(shelf, input);

        assertTrue(output.contains("Please enter a number"));
        assertTrue(output.contains("Please enter a choice from 1 to 7"));
        assertTrue(output.contains("Please enter a choice from 1 to 3"));
        assertTrue(output.contains("That student ID is not valid"));
        assertEquals("001234567", shelf.getItemStatus(1, 1).getBorrowerId());
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    public void testEndOfInputDuringImport()
    {
        ClubShelf shelf = new ClubShelf();
        String output = runMenu(shelf, "7");

        assertTrue(output.contains("Goodbye!"));
        assertEquals(3, shelf.getClubNames().length);
    }

    private String writeCatalog(String text) throws IOException
    {
        File file = files.newFile();
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
        return file.getAbsolutePath();
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
