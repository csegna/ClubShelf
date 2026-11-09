/**
 * A campus club with the same three items in its catalog.
 *
 * @author Corbin Segna
 * @version 2026.11.09
 */
public class Club 
{

    /** 
     * How many items each club needs. 
     **/
    public static final int ITEM_COUNT = 3;

    private String name;
    private Equipment[] items;

    /**
     * Creates a club and copies the equipment array.
     *
     * @param name the club's name
     * @param equipment the club's three items
     * @throws IllegalArgumentException if the name is null or blank,
     *         the array is null or the wrong size, or an item is null
     */
    public Club(String name, Equipment[] equipment) 
    {
        if (name == null || name.trim().isEmpty()) 
        {
            throw new IllegalArgumentException("Club name cannot be blank");
        }
        if (equipment == null || equipment.length != ITEM_COUNT) 
        {
            throw new IllegalArgumentException(
                "A club must have exactly " + ITEM_COUNT + " items");
        }
        for (Equipment e : equipment) 
        {
            if (e == null) 
            {
                throw new IllegalArgumentException(
                    "Equipment items cannot be null");
            }
        }
        this.name = name;
        this.items = equipment.clone();
    }

    /** 
     * @return the club's name 
     **/
    public String getName()
    {
        return name;
    }

    /**
     *  @return how many items the club has 
     **/
    public int getItemCount() 
    {
        return items.length;
    }

    /**
     * @param itemNumber the menu number, starting at 1
     * @return the item at that number
     * @throws IllegalArgumentException if the number is out of range
     */
    Equipment getItem(int itemNumber) 
    {
        if (itemNumber < 1 || itemNumber > items.length) 
        {
            throw new IllegalArgumentException("Invalid item, retry");
        }

        return items[itemNumber - 1];
    }
}
