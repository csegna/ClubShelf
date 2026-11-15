import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Reads catalog rows in the format Club name | Item | Item.
 *
 * @author Alexander Kyle (GenAI-assisted)
 * @version 2026.11.15
 */
public class CatalogLoader
{
    /**
     * Reads the whole file before returning any clubs.
     *
     * @param fileName the UTF-8 catalog file to read
     * @return the clubs and equipment described in file order
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if a row is invalid or names repeat
     */
    public static Club[] load(String fileName) throws IOException
    {
        if (fileName == null || fileName.trim().isEmpty())
        {
            throw new IllegalArgumentException("Catalog file name cannot be blank");
        }

        ArrayList<Club> clubs = new ArrayList<Club>();
        try (BufferedReader reader = Files.newBufferedReader(
            Paths.get(fileName), StandardCharsets.UTF_8))
        {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                // Some Windows text editors include a UTF-8 marker.
                if (lineNumber == 1 && line.startsWith("\uFEFF"))
                {
                    line = line.substring(1);
                }
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                {
                    continue;
                }

                // Keep trailing empty fields so "Club | Item |" is rejected.
                String[] fields = line.split("\\|", -1);
                if (fields.length < 2)
                {
                    throw invalidRow(lineNumber,
                        "Use Club name | Item name | Item name");
                }
                String clubName = fields[0].trim();
                if (clubName.isEmpty())
                {
                    throw invalidRow(lineNumber, "Club name cannot be blank");
                }
                for (Club club : clubs)
                {
                    if (club.getName().equalsIgnoreCase(clubName))
                    {
                        throw invalidRow(lineNumber,
                            "Club appears more than once: " + clubName);
                    }
                }

                Equipment[] items = new Equipment[fields.length - 1];
                for (int i = 1; i < fields.length; i++)
                {
                    String itemName = fields[i].trim();
                    if (itemName.isEmpty())
                    {
                        throw invalidRow(lineNumber,
                            "Equipment name cannot be blank");
                    }
                    for (int j = 0; j < i - 1; j++)
                    {
                        if (items[j].getName().equalsIgnoreCase(itemName))
                        {
                            throw invalidRow(lineNumber,
                                "Equipment appears more than once: " + itemName);
                        }
                    }
                    items[i - 1] = new Equipment(itemName);
                }
                clubs.add(new Club(clubName, items));
            }
        }
        if (clubs.isEmpty())
        {
            throw new IllegalArgumentException("Catalog file has no clubs");
        }
        return clubs.toArray(new Club[clubs.size()]);
    }

    private static IllegalArgumentException invalidRow(int line, String message)
    {
        return new IllegalArgumentException("Line " + line + ": " + message);
    }
}
