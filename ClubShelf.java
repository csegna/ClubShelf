/**
 * Holds the three clubs and handles borrowing, waitlists, and returns.
 * Menu numbers start at 1. Invalid numbers throw IllegalArgumentException.
 * Other rejected requests return an ActionResult and leave the items as they were.
 *
 * @author Corbin Segna
 * @version 2026.11.09
 */
public class ClubShelf 
{

    /** 
     * How many digits a student ID needs. 
     **/
    public static final int ID_LENGTH = 9;

    private Club[] clubs;

    /** 
     * Sets up three clubs with three available items each. 
     **/
    public ClubShelf() 
    {
        clubs = new Club[] 
            {
            new Club("Photography Club", new Equipment[] 
                {
                new Equipment("DSLR Camera"),
                new Equipment("Tripod"),
                new Equipment("Telephoto Lens") }),
            
            new Club("Outdoor Club", new Equipment[] 
                {
                new Equipment("Tent"),
                new Equipment("Sleeping Bag"),
                new Equipment("Hiking Backpack") }),
            
            new Club("Gamers Group", new Equipment[] 
                {
                new Equipment("Controller"),
                new Equipment("Board Game Set"),
                new Equipment("VR Headset") }) };
    }


    /** 
     * @return a new array of club names in menu order 
     **/
    public String[] getClubNames() 
    {
        String[] names = new String[clubs.length];
        for (int i = 0; i < clubs.length; i++) {
            names[i] = clubs[i].getName();
        }
        return names;
    }


    /**
     * @param clubNumber the club's menu number
     * @return read-only item statuses in menu order
     */
    public ItemStatus[] getCatalog(int clubNumber) 
    {
        Club club = getClub(clubNumber);
        ItemStatus[] catalog = new ItemStatus[club.getItemCount()];
        for (int i = 0; i < catalog.length; i++) 
        {
            catalog[i] = club.getItem(i + 1).getStatus();
        }
        return catalog;
    }


    /**
     * @param clubNumber the club's menu number
     * @param itemNumber the item's menu number
     * @return the item's read-only status, including its waitlist
     */
    public ItemStatus getItemStatus(int clubNumber, int itemNumber) 
    {
        return getEquipment(clubNumber, itemNumber).getStatus();
    }


    /**
     * Borrows an item if it's available. Students join the waitlist separately.
     *
     * @param clubNumber the club's menu number
     * @param itemNumber the item's menu number
     * @param studentId the student's ID
     * @return SUCCESS, INVALID_STUDENT_ID, ALREADY_BORROWED, or ITEM_UNAVAILABLE
     */
    public ActionResult borrowItem(
        int clubNumber,
        int itemNumber,
        String studentId) 
    {
        Equipment item = getEquipment(clubNumber, itemNumber);
        if (!isValidId(studentId)) {
            return result(Outcome.INVALID_STUDENT_ID);
        }
        if (studentId.equals(item.getBorrowerId())) 
        {
            return result(Outcome.ALREADY_BORROWED);
        }
        if (!item.isAvailable()) 
        {
            return result(Outcome.ITEM_UNAVAILABLE);
        }
        item.setBorrowerId(studentId);
        return result(Outcome.SUCCESS);
    }


    /**
     * Adds a student to the end of an unavailable item's waitlist.
     *
     * @param clubNumber the club's menu number
     * @param itemNumber the item's menu number
     * @param studentId the student's ID
     * @return SUCCESS with the student's spot (starting at 1), or
     *         INVALID_STUDENT_ID, ITEM_AVAILABLE, ALREADY_BORROWED, or ALREADY_WAITING
     */
    public ActionResult joinWaitlist(
        int clubNumber,
        int itemNumber,
        String studentId) 
    {
        Equipment item = getEquipment(clubNumber, itemNumber);
        if (!isValidId(studentId)) 
        {
            return result(Outcome.INVALID_STUDENT_ID);
        }
        if (item.isAvailable()) 
        {
            return result(Outcome.ITEM_AVAILABLE);
        }
        if (studentId.equals(item.getBorrowerId())) 
        {
            return result(Outcome.ALREADY_BORROWED);
        }
        QueueInterface<String> waitlist = item.getWaitlist();
        if (waitlist.contains(studentId)) 
        {
            return result(Outcome.ALREADY_WAITING);
        }
        waitlist.enqueue(studentId);
        return new ActionResult(Outcome.SUCCESS, waitlist.size(), null);
    }


    /**
     * Returns the item and gives it to the first student waiting.
     * If nobody is waiting, the item becomes available.
     *
     * @param clubNumber the club's menu number
     * @param itemNumber the item's menu number
     * @param studentId the ID of the student returning the item
     * @return SUCCESS with the next borrower's ID (null if none), or
     *         INVALID_STUDENT_ID, ITEM_NOT_BORROWED, or WRONG_BORROWER
     */
    public ActionResult returnItem(
        int clubNumber,
        int itemNumber,
        String studentId) 
    {
        Equipment item = getEquipment(clubNumber, itemNumber);
        if (!isValidId(studentId)) 
        {
            return result(Outcome.INVALID_STUDENT_ID);
        }
        if (item.isAvailable()) 
        {
            return result(Outcome.ITEM_NOT_BORROWED);
        }
        if (!studentId.equals(item.getBorrowerId())) 
        {
            return result(Outcome.WRONG_BORROWER);
        }
        QueueInterface<String> waitlist = item.getWaitlist();
        String next = null;
        if (!waitlist.isEmpty()) 
        {
            next = waitlist.dequeue();
        }
        item.setBorrowerId(next); 
        return new ActionResult(Outcome.SUCCESS, 0, next);
    }

    
    /** 
     * Checks if the ID has exactly nine digits. 
     **/
    private boolean isValidId(String id) 
    {
        if (id == null || id.length() != ID_LENGTH) 
        {
            return false;
        }
        for (int i = 0; i < id.length(); i++) 
        {
            if (!Character.isDigit(id.charAt(i))) 
            {
                return false;
            }
        }
        return true;
    }


    private Club getClub(int clubNumber) 
    {
        if (clubNumber < 1 || clubNumber > clubs.length) 
        {
            throw new IllegalArgumentException("Invalid club, retry");
        }
        return clubs[clubNumber - 1];
    }


    private Equipment getEquipment(int clubNumber, int itemNumber) 
    {
        return getClub(clubNumber).getItem(itemNumber);
    }


    private ActionResult result(Outcome outcome) 
    {
        return new ActionResult(outcome, 0, null);
    }
}
