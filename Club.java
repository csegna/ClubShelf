/**
 * A campus club and its equipment catalog.
 *
 * @author Corbin Segna
 * @version 2026.11.09
 */
public class Club 
{

    /** 
     * How many items each built-in club starts with. 
     **/
    public static final int ITEM_COUNT = 3;

    private String name;
    private Equipment[] items;

    /**
     * Creates a club and copies the equipment array.
     *
     * @param name the club's name
     * @param equipment the club's items
     * @throws IllegalArgumentException if the name is null or blank,
     *         the array is null or empty, or an item is null
     */
    public Club(String name, Equipment[] equipment) 
    {
        if (name == null || name.trim().isEmpty()) 
        {
            throw new IllegalArgumentException("Club name cannot be blank");
        }
        if (equipment == null || equipment.length == 0) 
        {
            throw new IllegalArgumentException(
                "A club must have at least one item");
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

    /** Checks whether an imported item is already in this club. */
    boolean hasItem(String itemName)
    {
        for (Equipment item : items)
        {
            if (item.getName().equalsIgnoreCase(itemName))
            {
                return true;
            }
        }
        return false;
    }

    /** Adds equipment while keeping existing borrowers and queues intact. */
    void addItem(Equipment item)
    {
        if (item == null)
        {
            throw new IllegalArgumentException("Equipment items cannot be null");
        }
        Equipment[] expanded = new Equipment[items.length + 1];
        System.arraycopy(items, 0, expanded, 0, items.length);
        expanded[items.length] = item;
        items = expanded;
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
