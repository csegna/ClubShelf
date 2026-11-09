import java.io.PrintStream;
import java.util.Scanner;

/**
 * The text screen for ClubShelf. It prints menus, equipment lists,
 * waitlists, and results, and it reads what the user types.
 *
 * The view does not make any decisions and never calls the model. The
 * controller tells it what to print, and it gives the controller whatever
 * the user typed.
 *
 * The Scanner and PrintStream are passed in so a test can use a Scanner
 * made from a String instead of the keyboard. System.out is a PrintStream,
 * so the app passes new Scanner(System.in) and System.out.
 *
 * @author Angelo Pinillos-Sternberg
 * @version 2026.11.09
 */
public class ConsoleView {

    private final Scanner input;
    private final PrintStream output;

    /**
     * Makes a new ConsoleView.
     *
     * @param input where the typed lines come from, cannot be null
     * @param output where the text is printed, cannot be null
     * @throws IllegalArgumentException if input or output is null
     */
    public ConsoleView(Scanner input, PrintStream output) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("Output cannot be null");
        }
        this.input = input;
        this.output = output;
    }


    /**
     * Prints a prompt and reads the next line the user types. The line is
     * returned exactly as typed, so the controller decides what to do with
     * blank lines or extra spaces.
     *
     * @param prompt the text to print first, or null to print nothing
     * @return the line the user typed, or null if there is no more input
     */
    public String readLine(String prompt) {
        if (prompt != null) {
            output.print(prompt);
        }
        if (!input.hasNextLine()) {
            return null;
        }
        return input.nextLine();
    }


    /**
     * Prints the numbered list of actions a student can pick.
     */
    public void showMenu() {
        output.println();
        output.println("What would you like to do?");
        output.println("  1. View a catalog");
        output.println("  2. Borrow an item");
        output.println("  3. Join a waitlist");
        output.println("  4. Return an item");
        output.println("  5. View a waitlist");
        output.println("  6. Quit");
    }


    /**
     * Prints the clubs as a numbered list starting at 1.
     *
     * @param clubNames the club names in menu order, cannot be null
     * @throws IllegalArgumentException if clubNames is null
     */
    public void showClubs(String[] clubNames) {
        if (clubNames == null) {
            throw new IllegalArgumentException("Club names cannot be null");
        }
        output.println();
        output.println("Choose a club:");
        for (int i = 0; i < clubNames.length; i++) {
            output.println("  " + (i + 1) + ". " + clubNames[i]);
        }
    }


    /**
     * Prints a club's equipment as a numbered list starting at 1. Each line
     * says whether the item is available. A checked out item also shows how
     * many students are waiting.
     *
     * @param items the items in menu order, cannot be null
     * @throws IllegalArgumentException if items is null
     */
    public void showCatalog(ItemStatus[] items) {
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }
        output.println();
        if (items.length == 0) {
            output.println("This club has no equipment to show.");
            return;
        }
        output.println("Equipment:");
        for (int i = 0; i < items.length; i++) {
            ItemStatus item = items[i];
            String status;
            int waiting = item.getWaitlist().length;
            if (item.isAvailable()) {
                status = "Available";
            }
            else if (waiting == 0) {
                status = "Checked out";
            }
            else {
                status = "Checked out, " + waiting + " waiting";
            }
            output.println("  " + (i + 1) + ". " + item.getName() + "  ("
                + status + ")");
        }
    }


    /**
     * Prints the students waiting for an item, front of the line first. If
     * nobody is waiting, it prints a message saying so.
     *
     * @param item the item whose waitlist to print, cannot be null
     * @throws IllegalArgumentException if item is null
     */
    public void showWaitlist(ItemStatus item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        String[] waiting = item.getWaitlist();
        output.println();
        if (waiting.length == 0) {
            output.println("Nobody is waiting for " + item.getName() + ".");
            return;
        }
        output.println("Waitlist for " + item.getName() + ":");
        for (int i = 0; i < waiting.length; i++) {
            output.println("  " + (i + 1) + ". " + waiting[i]);
        }
    }


    /**
     * Prints a sentence telling the student how their request went.
     *
     * SUCCESS is used for borrowing, joining a waitlist, and returning, so
     * this method looks at the queue position and next borrower to tell
     * them apart. A position above 0 means the student joined a waitlist.
     * A next borrower means a return gave the item to someone in line.
     *
     * @param result the result from the model, cannot be null
     * @throws IllegalArgumentException if result is null
     */
    public void showResult(ActionResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }
        Outcome outcome = result.getOutcome();
        int position = result.getQueuePosition();
        String nextBorrower = result.getNextBorrowerId();

        if (outcome == Outcome.SUCCESS) {
            if (position > 0) {
                output.println("You joined the waitlist. You are number "
                    + position + " in line.");
            }
            else if (nextBorrower != null) {
                output.println("Item returned. It now goes to student "
                    + nextBorrower + ", who was first in line.");
            }
            else {
                output.println("Done. Your request went through.");
            }
        }
        else if (outcome == Outcome.INVALID_STUDENT_ID) {
            output.println("That student ID is not valid. "
                + "Enter exactly 9 digits, with no spaces.");
        }
        else if (outcome == Outcome.ITEM_UNAVAILABLE) {
            output.println("Another student has this item right now. "
                + "You can join the waitlist for it.");
        }
        else if (outcome == Outcome.ALREADY_BORROWED) {
            output.println("You already have this item.");
        }
        else if (outcome == Outcome.ALREADY_WAITING) {
            output.println("You are already on the waitlist for this item.");
        }
        else if (outcome == Outcome.ITEM_AVAILABLE) {
            output.println("Nobody has this item, so you can borrow it now.");
        }
        else if (outcome == Outcome.ITEM_NOT_BORROWED) {
            output.println("This item is not checked out, "
                + "so there is nothing to return.");
        }
        else if (outcome == Outcome.WRONG_BORROWER) {
            output.println("That ID does not match the student "
                + "who has this item.");
        }
    }


    /**
     * Prints a message on its own line. The controller uses this for input
     * errors, like a menu number that is too big.
     *
     * @param message the text to print
     */
    public void showMessage(String message) {
        output.println(message);
    }
}
