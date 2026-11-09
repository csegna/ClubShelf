# ClubShelf

ClubShelf is a Java console app for campus equipment lending. Students select
Photography Club, Outdoor Club, or Gamers Group, then choose one of the club's
three items. Each item has one copy and its own first-in, first-out (FIFO)
waitlist. When a borrower returns an item, the model assigns it to the first
waiting student or makes it available if the waitlist is empty.

## Proposed team split

| Owner | Types | Count |
| --- | --- | --- |
| Alexander Kyle | `QueueInterface<T>`, `LinkedQueue<T>`, `EmptyQueueException`, and the private `LinkedQueue.Node<T>` | 4 |
| Teammate who claimed the app and model files | `ClubShelfApp`, `ClubShelfController`, `ClubShelf`, `Equipment`, and `Club` (assumed fifth) | 5 |
| Other teammate | `ConsoleView`, `Outcome`, `ActionResult`, and `ItemStatus` | 4 |

Confirm `Club` as the fifth claimed type before merging. This proposed split
updates the original specification's work assignments. The 13 types include an
interface, an enum, and a private node. Alexander's four types live in three
Java files because `Node<T>` is declared inside `LinkedQueue.java`.

## Build and run

Put the three queue files next to the other source files. They currently use
Java's default package. If the team project has a named package, add the
project's package declaration and use its corresponding source folder.

To compile the queue independently from the directory containing the files:

```sh
javac QueueInterface.java LinkedQueue.java EmptyQueueException.java
```

Once the team's source files are together in the default package, run these
commands from that same directory with a JDK installed:

```sh
javac *.java
java ClubShelfApp
```

Follow the numbered menus to pick a club and item, borrow or return it, or view
or join its waitlist. Use sample student IDs during development and demos.

## Run the model and queue checks

After merging the team model files, compile and run the model driver from the
source directory. It exercises borrowing, waitlist positions, FIFO assignment
after a return, and rejection of a repeat return without launching the console.

```sh
javac -d build QueueInterface.java LinkedQueue.java EmptyQueueException.java Club.java ClubShelf.java Equipment.java Outcome.java ActionResult.java ItemStatus.java tests/ClubShelfModelDriver.java
java -cp build ClubShelfModelDriver
```

In Eclipse, put `tests/LinkedQueueTest.java` in the project's test source
folder, add the JUnit 4.13.2 library to the build path, and select
**Run As → JUnit Test** on the file. If the team uses a named package, match
the package declaration in both test files to the model.

## How the queue connects to the app

Each `Equipment` instance owns one `QueueInterface<String>` backed by a
`LinkedQueue<String>`. The queue stores entries in the order they arrive.
`ClubShelf` applies the lending rules, including rejecting duplicate requests,
requests from the current borrower, and returns from the wrong student.
`ClubShelfController` handles menu input and passes model results and
`ItemStatus` snapshots to `ConsoleView`.

- `enqueue` appends at the back and `dequeue` removes from the front. Each
  operation is O(1), as are `peek`, `size`, and `isEmpty`.
- `contains` compares entries with `equals` and `toArray` copies entries in
  FIFO order without removing them. Both are O(n). The queue permits repeated
  entries, so the model checks `contains(studentId)` before adding a request.
- After a successful join, `size()` is the student's one-based queue position.
  On a valid return, the model checks `isEmpty()` and dequeues once when a
  waiting student exists. The dequeued ID becomes the new borrower.
- For the `ItemStatus` snapshot, copy the queue's `Object[]` into a
  `String[]`, for example with
  `Arrays.copyOf(entries, entries.length, String[].class)`. A direct cast of
  the `Object[]` to `String[]` throws at runtime.

The specification's method signatures accept any nonempty digits-only ID,
while its state description and edge-case tests call for exactly nine digits.
Use the method signatures as the working interpretation and confirm it as a
team before finalizing the model and its tests. Keep IDs as strings to preserve
leading zeros. The queue has no student ID rules.

## Integration checks

1. Open the app and verify that each of the three clubs has three available
   items. Borrow one with ID `001234567`.
2. Add `002234567` and `003234567` to the same item's waitlist. Check that
   positions are 1 and 2, and that viewing the list twice leaves the order
   intact.
3. Attempt a duplicate join, a join by the current borrower, and a return by the
   wrong student. The borrower and waitlist should stay the same.
4. Return the item as `001234567`. Check that `002234567` becomes the borrower
   and only `003234567` remains waiting. Repeat the old borrower's return and
   verify the state is unchanged. Return it as each new borrower in order and
   verify that the item ends up available.
5. Try blank, nonnumeric, and out-of-range menu choices, malformed IDs, and end
   of input. Check that the app recovers from invalid input and exits cleanly
   when input ends.

For the queue in isolation, check FIFO order, equality between distinct String
objects with the same text, null rejection, `peek` and `dequeue` on empty,
enqueue after removing the last entry, and independence of the array returned by
`toArray()`.

## Stretch goal for a text file catalog

After the core app passes integration checks, accept an optional startup path
to a UTF-8 text file such as `catalog.txt`. Each row defines one club. Split
the row on `|` into tokens. The first token is the club name and each later
token is an item name. Reserve `|` as the separator and use `#` at the start
of a line for comments.

```text
# Club | Item | Item | ...
Photography Club | Camera | Tripod | Lens Kit
Outdoor Club | Tent | Sleeping Bag | Camp Stove
Gamers Group | Controller | Board Game | Console
Robotics Club | Multimeter | Soldering Kit
```

1. Read the file at startup and skip blank lines and lines beginning with `#`
   after trimming.
2. Split each remaining line with `line.split("\\|", -1)` and trim each token.
   Keeping empty tokens lets the loader catch missing names, including a
   trailing separator. Require a club name, at least one item, and at least
   one valid club in the file. Reject duplicate club names and duplicate item
   names within a club.
3. Validate the whole catalog before constructing the model. With no file
   specified, use the built-in catalog. If the supplied file is missing or
   invalid, show a startup message with the path and the line number when
   available, then load the built-in catalog.
4. Update `Club` to accept variable catalog lengths. Have the model and
   controller check selections against the actual number of clubs and items.
   Test with an extra club, a club with two items, and an invalid file.

Each loaded item still owns its own queue. Catalog loading defines what can be
borrowed. Saving current loans and waitlists across runs is a separate feature.

## Attribution

These rows cover Alexander Kyle's portion. Add a row for every other class
and test file in the merged prototype with its actual contributors and work.

| Class or test file | Contributor | Work |
| --- | --- | --- |
| `QueueInterface<T>` | Alexander Kyle | GenAI-assisted draft |
| `LinkedQueue<T>` | Alexander Kyle | GenAI-assisted draft and queue verification |
| `LinkedQueue.Node<T>` | Alexander Kyle | GenAI-assisted draft |
| `EmptyQueueException` | Alexander Kyle | GenAI-assisted draft |
| `tests/LinkedQueueTest.java` | Alexander Kyle | GenAI-assisted JUnit draft and test run |
| `tests/ClubShelfModelDriver.java` | Alexander Kyle | GenAI-assisted draft based on the agreed model signatures |

GenAI note. Alexander Kyle used OpenAI ChatGPT (Codex) to analyze the project
specification and scope and to draft the queue, queue tests, model driver,
README, and text-file catalog design.
