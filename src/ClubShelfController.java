/**
 * Runs the menu, checks menu choices, and shows results from ClubShelf.
 *
 * @author Corbin Segna
 * @version 2026.11.09
 */
public class ClubShelfController 
{

    private static final int VIEW_CATALOG = 1;
    private static final int BORROW = 2;
    private static final int JOIN_WAITLIST = 3;
    private static final int RETURN = 4;
    private static final int VIEW_WAITLIST = 5;
    private static final int EXIT = 6;

    private ClubShelf model;
    private ConsoleView view;
    private String lastId; 

    /**
     * @param model the ClubShelf model
     * @param view the console view
     * @throws IllegalArgumentException if either is null
     */
    public ClubShelfController(ClubShelf model, ConsoleView view) 
    {
        if (model == null || view == null) {
            throw new IllegalArgumentException(
                "Model and view cannot be null");
        }
        this.model = model;
        this.view = view;
    }


    public void run() 
    {
        view.showMessage("Welcome to ClubShelf!");
        boolean running = true;
        while (running) 
        {
            view.showMenu();
            Integer choice = readNumber("Choose an option: ", 1, EXIT);
            if (choice == null || choice == EXIT) 
            {
                running = false;
            }
            else {
                running = handleChoice(choice);
            }
        }
        view.showMessage("Goodbye!");
    }


    /**
     * Runs the selected menu option.
     *
     * @return false if input ends
     */
    private boolean handleChoice(int choice) 
    {
        int[] selection = readSelection(choice != VIEW_CATALOG);
        if (selection == null) 
        {
            return false;
        }
        int club = selection[0];
        int item = selection[1];
        try 
        {
            switch (choice) 
            {
                case VIEW_CATALOG:
                    view.showCatalog(model.getCatalog(club));
                    return true;
                case VIEW_WAITLIST:
                    view.showWaitlist(model.getItemStatus(club, item));
                    return true;
                case BORROW:
                    return handleBorrow(club, item);
                case JOIN_WAITLIST:
                case RETURN:
                    return attempt(choice, club, item) != null;
                default:
                    return true;
            }
        }
        catch (IllegalArgumentException e) 
        {
            view.showMessage(e.getMessage());
            return true;
        }
    }


    private boolean handleBorrow(int club, int item) 
    {
        ActionResult result = attempt(BORROW, club, item);
        if (result == null) 
        {
            return false;
        }
        if (result.getOutcome() == Outcome.ITEM_UNAVAILABLE) 
        {
            String answer = view.readLine(
                "Join the waitlist instead? (y/n): ");
            if (answer == null) 
            {
                return false;
            }
            if (answer.trim().equalsIgnoreCase("y")) 
            {
                ActionResult joined = model.joinWaitlist(club, item,
                    lastId);
                view.showResult(joined);
                refreshIfSuccess(joined, club, item);
            }
        }
        return true;
    }


    /**
     * Runs the action, shows the result, and asks again if the ID is invalid.
     * Saves the valid ID and shows the waitlist on success.
     *
     * @param action BORROW, JOIN_WAITLIST, or RETURN
     * @return the final result, or null if input ends
     */
    private ActionResult attempt(int action, int club, int item) {
        while (true) {
            String id = view.readLine("Enter your student ID: ");
            if (id == null) {
                return null;
            }
            ActionResult result;
            if (action == BORROW) {
                result = model.borrowItem(club, item, id);
            }
            else if (action == JOIN_WAITLIST) {
                result = model.joinWaitlist(club, item, id);
            }
            else {
                result = model.returnItem(club, item, id);
            }
            view.showResult(result);
            if (result.getOutcome() != Outcome.INVALID_STUDENT_ID) {
                lastId = id;
                refreshIfSuccess(result, club, item);
                return result;
            }
        }
    }


    private void refreshIfSuccess(ActionResult result, int club, int item) 
    {
        if (result.getOutcome() == Outcome.SUCCESS) 
        {
            view.showWaitlist(model.getItemStatus(club, item));
        }
    }


    /**
     * Asks for a club and, if needed, an item.
     *
     * @param needItem whether to ask for an item
     * @return the club and item numbers, or null if input ends.
     *         The item number is 0 when skipped.
     */
    private int[] readSelection(boolean needItem) 
    {
        String[] clubNames = model.getClubNames();
        view.showClubs(clubNames);
        Integer club = readNumber("Choose a club: ", 1, clubNames.length);
        if (club == null) 
        {
            return null;
        }
        int item = 0;
        if (needItem) 
        {
            ItemStatus[] catalog = model.getCatalog(club);
            view.showCatalog(catalog);
            Integer chosen = readNumber("Choose an item: ", 1,
                catalog.length);
            if (chosen == null) 
            {
                return null;
            }
            item = chosen;
        }
        return new int[] { club, item };
    }


    /**
     * Keeps asking for a whole number from min to max.
     *
     * @return the number, or null if input ends
     */
    private Integer readNumber(String prompt, int min, int max) 
    {
        while (true) 
        {
            String line = view.readLine(prompt);
            if (line == null) 
            {
                return null;
            }
            line = line.trim();
            if (line.isEmpty()) 
            {
                view.showMessage("Please enter a choice from " + min
                    + " to " + max + ".");
                continue;
            }
            int value;
            try 
            {
                value = Integer.parseInt(line);
            }
            catch (NumberFormatException e) 
            {
                view.showMessage("Invalid input. Please enter a number.");
                continue;
            }
            if (value < min || value > max) 
            {
                view.showMessage("Please enter a choice from " + min
                    + " to " + max + ".");
                continue;
            }
            return value;
        }
    }
}
