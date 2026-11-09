# ClubShelf

CS 2114 Project 3, Deliverable 3 — First Prototype

ClubShelf is a Java console app for borrowing equipment from Photography Club,
Outdoor Club, and Gamers Group. Each club starts with three items and one copy
of each item. If an item is checked out, students can join its waitlist. When
the borrower returns it, ClubShelf assigns it to the first student in line or
makes it available if nobody is waiting.

## Run the app

In Eclipse, open the ClubShelf project, right-click `ClubShelfApp.java`, and
choose **Run As → Java Application**. Enter your choices in the Console tab.

From the repository folder with a JDK installed, you can also run:

```sh
javac -d bin ClubShelfApp.java
java -cp bin ClubShelfApp
```

To demo a return with a waitlist, use the same club and item for each action:

1. Choose **2** to borrow with ID `001234567`.
2. Choose **3** to join the waitlist with `002234567`, then again with
   `003234567`. They should be in positions 1 and 2.
3. Choose **4** to return as `001234567`. The app should assign the item to
   `002234567` and show `003234567` still waiting.
4. Choose **6** to quit.

## Run the model on its own

Build the app as shown above. With JDK 11 or later, save this short driver as
`ClubShelfModelDemo.java` temporarily in the repository folder:

```java
class ClubShelfModelDemo {
    public static void main(String[] args) {
        ClubShelf shelf = new ClubShelf();
        System.out.println(shelf.borrowItem(1, 1, "001234567").getOutcome());
        System.out.println(shelf.joinWaitlist(1, 1, "002234567").getQueuePosition());
        System.out.println(shelf.returnItem(1, 1, "001234567").getNextBorrowerId());
    }
}
```

```sh
java -cp bin ClubShelfModelDemo.java
```

The driver prints `SUCCESS`, queue position `1`, and the new borrower ID
`002234567`. It calls the model directly. Keep this temporary demo outside
the commit.

## Reflection

**What changed from the specification.** We kept the 13 planned types and
redistributed the implementation. Corbin took the five app, controller, and
model types. Angelo took the view and result types, and Alexander took the
four queue types. The specification had conflicting rules for ID length. The
prototype follows its data and test sections and requires exactly nine digits.
It keeps IDs as strings so leading zeros are preserved. During integration,
the console menu labels were aligned with the controller's option numbers so
each displayed choice triggers the correct action.

**Model and view.** `ClubShelf` handles borrowing rules, ID validation, and
waitlist updates. `ClubShelfController` checks menu choices and calls the
model. `ConsoleView` reads lines and displays results. Calling
`ClubShelf` directly exercises the lending rules without a scanner, a console
view, or a controller.

**What we figured out.** The queue uses `contains` to look for duplicate IDs
and `toArray` to show the waitlist in order without removing entries.
`ActionResult` tells the view whether the request succeeded and carries the
queue position or next borrower's ID when relevant. When the correct borrower
returns an item, the model takes the first ID from the queue and makes that
student the borrower. The app shows this handoff in the console.

**What is still open.** The catalog is built into the program. Loans and
waitlists reset on exit. The prototype has no claim deadlines, loan history,
or support for multiple copies. We still need a team pass through the full
set of bad-input scenarios.

## Next Steps

The owners below are proposed for the next implementation.

1. **Finish MVP verification.** Corbin will check model rules and menu
   recovery for invalid choices, bad IDs, and repeat returns. Alexander will
   check duplicate joins, FIFO handoffs, and that rejected actions leave the
   queue intact. Angelo will check the messages for each outcome and confirm
   they describe the model's rules.
2. **Add Import Club Catalog for Deliverable 4.** The proposed feature is a
   menu action that reads a text file and adds new clubs to the running
   catalog while preserving existing loans and waitlists. A line such as
   `Robotics Club | Multimeter | Soldering Kit` defines a club and its items.
   Alexander will own parsing and file validation. Corbin will own the model
   and controller changes. Angelo will own the menu label, prompts, and
   import messages.
3. **Verify the import.** Alexander will check valid rows, blank fields,
   trailing separators, missing files, and duplicate names. Corbin will check
   that imported clubs are selectable and a rejected import leaves the
   existing catalog, loans, and waitlists intact. Angelo will check that the
   new menu choice reports success and failure clearly.

We expect `ClubShelf` to gain an import operation and update the catalog
lookups, `Club` to accept catalogs with different item counts,
`ClubShelfController.run()` to route the new choice, and
`ConsoleView.showMenu()` to display it. The queue types, `Equipment`,
and the existing borrowing and return methods should be reusable as they are.

Catalog import is the stretch goal planned for Deliverable 4. Saving loans and
waitlists, claim deadlines, loan history, and multiple copies are deferred.

## Attribution

| Type | Contributor | Work |
| --- | --- | --- |
| `ClubShelfApp` | Corbin Segna | Connected the model, view, and controller |
| `ClubShelfController` | Corbin Segna | Implemented the menu loop and input handling |
| `ClubShelf` | Corbin Segna | Implemented catalog access and lending rules |
| `Club` | Corbin Segna | Implemented club records and equipment lookup |
| `Equipment` | Corbin Segna | Implemented item records and waitlists |
| `ConsoleView` | Angelo Pinillos-Sternberg | Implemented console input, menus, and messages |
| `Outcome` | Angelo Pinillos-Sternberg | Defined the lending outcomes |
| `ActionResult` | Angelo Pinillos-Sternberg | Implemented the result returned by lending actions |
| `ItemStatus` | Angelo Pinillos-Sternberg | Implemented the read-only item snapshot |
| `QueueInterface<T>` | Alexander Kyle | Defined the queue operations |
| `LinkedQueue<T>` | Alexander Kyle | Implemented and checked FIFO queue behavior |
| `LinkedQueue.Node<T>` | Alexander Kyle | Implemented the private node inside `LinkedQueue.java` |
| `EmptyQueueException` | Alexander Kyle | Implemented the exception for empty queue operations |

**GenAI note.** Corbin Segna, Angelo Pinillos-Sternberg, and Alexander Kyle
each used generative AI assistance on their assigned work. Alexander used
OpenAI ChatGPT (Codex) to review the scope and specification, draft the queue
code and README, and think through which classes a catalog import would affect.
