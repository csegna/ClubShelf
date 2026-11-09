/**
 * Holds the information about one equipment item at one moment: its name,
 * whether it is available, who has it, and who is waiting for it. The model
 * makes these so the view has something to display.
 *
 * Nothing in an ItemStatus can change after it is made. The waitlist array
 * is copied when it comes in and copied again when it goes out, so changing
 * either array does not change this object or the model.
 *
 * @author Angelo Pinillos-Sternberg
 * @version 2026.11.09
 */
public class ItemStatus {

    private final String name;
    private final boolean available;
    private final String borrowerId;
    private final String[] waitlist;

    /**
     * Makes a new ItemStatus.
     *
     * @param name the name of the equipment, cannot be null
     * @param available true if nobody has the item right now
     * @param borrowerId the ID of the student who has the item, or null if
     *     the item is available
     * @param waitlist the IDs of the students waiting, front of the line
     *     first. Use an empty array if nobody is waiting. Cannot be null.
     * @throws IllegalArgumentException if name or waitlist is null
     */
    public ItemStatus(String name, boolean available, String borrowerId,
        String[] waitlist) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (waitlist == null) {
            throw new IllegalArgumentException("Waitlist cannot be null");
        }
        this.name = name;
        this.available = available;
        this.borrowerId = borrowerId;
        this.waitlist = copyOf(waitlist);
    }


    /**
     * Gets the name of the equipment.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Tells whether the item can be borrowed right now.
     *
     * @return true if nobody has the item
     */
    public boolean isAvailable() {
        return available;
    }


    /**
     * Gets the ID of the student who has the item.
     *
     * @return the borrower's ID, or null if the item is available
     */
    public String getBorrowerId() {
        return borrowerId;
    }


    /**
     * Gets the students waiting for this item, in the order they joined.
     * Every call returns a new array.
     *
     * @return a copy of the waiting student IDs, front of the line first
     */
    public String[] getWaitlist() {
        return copyOf(waitlist);
    }


    /**
     * Makes a new array with the same Strings in the same order.
     *
     * @param original the array to copy
     * @return the new array
     */
    private String[] copyOf(String[] original) {
        String[] copy = new String[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i];
        }
        return copy;
    }
}
