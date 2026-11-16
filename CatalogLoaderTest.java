import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests catalog files. Test files are removed after each test. */
public class CatalogLoaderTest
{
    @Rule
    public TemporaryFolder files = new TemporaryFolder();

    @Test
    public void testLoadValidCatalog() throws IOException
    {
        String path = writeCatalog("\uFEFF# Club equipment\r\n\n"
            + " SCUBA VT Club | BC | REGS | Tanks \r\n"
            + " Robotics Club | Multimeter \n");

        Club[] clubs = CatalogLoader.load(path);

        assertEquals(2, clubs.length);
        assertEquals("SCUBA VT Club", clubs[0].getName());
        assertEquals(3, clubs[0].getItemCount());
        assertEquals("BC", clubs[0].getItem(1).getName());
        assertEquals("REGS", clubs[0].getItem(2).getName());
        assertEquals("Tanks", clubs[0].getItem(3).getName());
        assertEquals("Robotics Club", clubs[1].getName());
        assertEquals(1, clubs[1].getItemCount());
        assertEquals("Multimeter", clubs[1].getItem(1).getName());
        assertTrue(clubs[0].getItem(1).isAvailable());
        assertNull(clubs[0].getItem(1).getBorrowerId());
        assertTrue(clubs[0].getItem(1).getWaitlist().isEmpty());
    }

    @Test
    public void testBlankFileNames() throws IOException
    {
        String[] badNames = { null, "", "   " };
        for (String name : badNames)
        {
            try
            {
                CatalogLoader.load(name);
                fail("Expected a blank file name to be rejected.");
            }
            catch (IllegalArgumentException expected)
            {
                assertTrue(expected.getMessage().contains("file name"));
            }
        }
    }

    @Test(expected = IOException.class)
    public void testMissingFile() throws IOException
    {
        CatalogLoader.load(new File(files.getRoot(), "missing.txt").getPath());
    }

    @Test
    public void testEmptyCatalogs() throws IOException
    {
        assertInvalidCatalog("", "no clubs");
        assertInvalidCatalog("# Comment only\n \n", "no clubs");
    }

    @Test
    public void testInvalidRows() throws IOException
    {
        String[] badRows = { "Robotics Club", " | Meter",
            "Robotics Club | ", "Robotics Club | Meter |",
            "Robotics Club | Meter || Wrench" };
        for (String row : badRows)
        {
            assertInvalidCatalog(row, "Line 1:");
        }
    }

    @Test
    public void testDuplicateClubNames() throws IOException
    {
        assertInvalidCatalog("Robotics Club | Meter\nrobotics club | Wrench",
            "Line 2:");
    }

    @Test
    public void testDuplicateItemNames() throws IOException
    {
        assertInvalidCatalog("Robotics Club | Meter | meter", "Line 1:");
    }

    @Test
    public void testInvalidLaterRowReportsItsLine() throws IOException
    {
        assertInvalidCatalog("# Catalog\nRobotics Club | Meter\nBroken row",
            "Line 3:");
    }

    private String writeCatalog(String text) throws IOException
    {
        File file = files.newFile();
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
        return file.getAbsolutePath();
    }

    private void assertInvalidCatalog(String text, String message)
        throws IOException
    {
        String path = writeCatalog(text);
        try
        {
            CatalogLoader.load(path);
            fail("Expected the invalid catalog to be rejected.");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(expected.getMessage().contains(message));
        }
    }
}
