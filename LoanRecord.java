import java.time.LocalDate;

/**
 * One loan of one item to one student: who had it, when they got it,
 * and when (or whether) they brought it back. A loan is due LOAN_DAYS
 * after it starts.
 *
 * LoanRecord is immutable. Returning an item creates a new record with a
 * return date, so the view can be handed records without being able to
 * change the history.
 *
 * @author Corbin Segna
 * @version 2026.11.16
 */
public class LoanRecord
{

    /** How many days a student may keep an item before it is late. */
    public static final int LOAN_DAYS = 7;

    private final String studentId;
    private final String clubName;
    private final String itemName;
    private final LocalDate borrowedOn;
    private final LocalDate returnedOn;

    /**
     * Creates an open loan (not yet returned).
     *
     * @param studentId the borrower's ID
     * @param clubName the club that owns the item
     * @param itemName the item's name
     * @param borrowedOn the day the loan started
     * @throws IllegalArgumentException if any value is null or blank
     */
    public LoanRecord(
        String studentId,
        String clubName,
        String itemName,
        LocalDate borrowedOn)
    {
        this(studentId, clubName, itemName, borrowedOn, null);
    }


    private LoanRecord(
        String studentId,
        String clubName,
        String itemName,
        LocalDate borrowedOn,
        LocalDate returnedOn)
    {
        requireText(studentId, "Student ID");
        requireText(clubName, "Club name");
        requireText(itemName, "Item name");
        if (borrowedOn == null)
        {
            throw new IllegalArgumentException("Borrow date cannot be null");
        }
        this.studentId = studentId;
        this.clubName = clubName;
        this.itemName = itemName;
        this.borrowedOn = borrowedOn;
        this.returnedOn = returnedOn;
    }


    /**
     * @return the borrower's ID
     */
    public String getStudentId()
    {
        return studentId;
    }


    /**
     * @return the club that owns the item
     */
    public String getClubName()
    {
        return clubName;
    }


    /**
     * @return the item's name
     */
    public String getItemName()
    {
        return itemName;
    }


    /**
     * @return the day the loan started
     */
    public LocalDate getBorrowedOn()
    {
        return borrowedOn;
    }


    /**
     * @return the day the item came back, or null if it is still out
     */
    public LocalDate getReturnedOn()
    {
        return returnedOn;
    }


    /**
     * @return the last day the item can be returned on time
     */
    public LocalDate getDueDate()
    {
        return borrowedOn.plusDays(LOAN_DAYS);
    }


    /**
     * @return true if the item has been returned
     */
    public boolean isReturned()
    {
        return returnedOn != null;
    }


    /**
     * An open loan is overdue once today is after its due date.
     * A returned loan is never overdue.
     *
     * @param today the date to check against
     * @return true if the item is still out and past its due date
     * @throws IllegalArgumentException if today is null
     */
    public boolean isOverdue(LocalDate today)
    {
        if (today == null)
        {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return !isReturned() && today.isAfter(getDueDate());
    }


    /**
     * @return true if the item was returned after its due date
     */
    public boolean wasReturnedLate()
    {
        return isReturned() && returnedOn.isAfter(getDueDate());
    }


    /**
     * Creates the closed version of this loan. Package-private: only
     * ClubShelf closes loans.
     *
     * @param day the day the item came back
     * @return a new record with the return date filled in
     * @throws IllegalStateException if this loan is already closed
     * @throws IllegalArgumentException if day is null or before the
     *             borrow date
     */
    LoanRecord returnedOn(LocalDate day)
    {
        if (isReturned())
        {
            throw new IllegalStateException("Loan was already returned");
        }
        if (day == null || day.isBefore(borrowedOn))
        {
            throw new IllegalArgumentException(
                "Return date must be on or after the borrow date");
        }
        return new LoanRecord(studentId, clubName, itemName, borrowedOn, day);
    }


    private static void requireText(String value, String label)
    {
        if (value == null || value.trim().isEmpty())
        {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
    }
}
