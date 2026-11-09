/**
 * Keeps track of an item's name, borrower, and waitlist.
 *
 * @author Corbin Segna
 * @version 2026.11.09
 */
public class Equipment 
{

    private String name;
    private String borrowerId;
    private QueueInterface<String> waitlist;

    /**
     * Creates an available item with an empty waitlist.
     *
     * @param name the item's name
     * @throws IllegalArgumentException if the name is null or blank
     */
    public Equipment(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Equipment name cannot be blank");
        }
        this.name = name;
        this.borrowerId = null;
        this.waitlist = new LinkedQueue<String>();
    }

    /** 
     * @return the item's name 
     **/
    public String getName() {
        return name;
    }

    /** 
     * @return true if the item is available
     **/
    public boolean isAvailable() {
        return borrowerId == null;
    }

    /**
     *  @return the borrower's ID, or null if the item is available 
     **/
    public String getBorrowerId()
{
        return borrowerId;
    }

    /**
     *  @return a read-only item status with a copy of the waitlist in order 
     **/
    public ItemStatus getStatus() 
    {
        Object[] entries = waitlist.toArray();
        String[] ids = new String[entries.length];
        for (int i = 0; i < entries.length; i++) 
        {
            ids[i] = (String)entries[i];
        }
        return new ItemStatus(name, isAvailable(), borrowerId, ids);
    }

    /** 
     * @param id the new borrower's ID, or null to make the item available 
     **/
    void setBorrowerId(String id) 
    {
        borrowerId = id;
    }

    /** 
     * @return the waitlist that ClubShelf can update 
     **/
    QueueInterface<String> getWaitlist() 
    {
        return waitlist;
    }
    
}
