import java.util.Scanner;

/**
 * Starts the ClubShelf menu.
 *
 * @author Corbin Segna
 * @version 2026.11.09
 */
public class ClubShelfApp
{

    /** 
     * @param args unused 
     **/
    public static void main(String[] args) 
    {
        ClubShelf model = new ClubShelf();
        ConsoleView view = new ConsoleView(new Scanner(System.in),
            System.out);
        ClubShelfController controller = new ClubShelfController(model,
            view);
        controller.run();
    }
}
