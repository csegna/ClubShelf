# ClubShelf

CS 2114 Project 3, Deliverable 4 - Second Prototype + Tests

Borrow equipment, join its FIFO waitlist, and return it to the next student.
Student IDs require exactly nine digits.

## Run

In Eclipse, refresh with **F5**, enable **Project > Build Automatically**, then
**Project > Clean** to compile. Run `ClubShelfApp.java` as **Java Application**.
Right-click the project > **Run As > JUnit Test** (JUnit 4) to run all 109 tests.
Libraries are included in `junit/`. Use the project folder as the working directory.

Menu **7** imports a file; Enter uses `clubs.txt`. Format:
`SCUBA VT Club | BC | REGS | Tanks`. Blank lines and `#` comments are allowed.
Existing equipment is skipped; duplicate names within the file are rejected.

## Reflection

Catalog import changed `ClubShelf` (merging), `Club` (variable catalogs),
`ClubShelfController` (option 7), and `ConsoleView` (menu), matching D3's prediction.
We also added `CatalogLoader` to separate parsing. The queue types, `Equipment`,
and existing borrowing/return methods stayed unchanged.

The model validates the complete file before merging, preserving loans and
waitlists. The controller handles paths and errors. The view only reads/prints,
stores no model state, and needed only a menu label; no model rules moved into it.

We extended D3's new-club import so clubs can gain equipment without resetting
loans. Existing names are skipped without regard to case. Added tests cover imports,
rejected model actions, unchanged state after failure, interleaved queue operations,
and menu recovery. All 109 tests pass.

Loans, waitlists, and imports reset on exit. History, deadlines, and multiple
copies are unsupported. No tests currently fail. A future import-result object
returning both counts would remove club-count calculations from the controller.

## Next Steps

Final-build work, in order:

1. Corbin: final check of the full import/borrow/waitlist/return flow.
2. Alexander: rerun tests after final team edits and check packaging.
3. Angelo: review messages and rehearse the demo with the team.

Committed stretch goal: catalog import. Dropped from this build: saving
loans/waitlists, deadlines, history, and multiple copies.

**Three-minute demo:** 0:00-0:30 import `clubs.txt` (7, Enter);
0:30-1:00 borrow SCUBA's BC as `001234567` (club 4, item 1);
1:00-1:30 join as `002234567`; 1:30-2:00 return as `001234567` and show the handoff;
2:00-2:30 try a missing import file and continue using the menu;
2:30-3:00 show passing tests and quit (6).

## Attribution

Alexander = Alexander Kyle; Corbin = Corbin Segna; Angelo = Angelo Pinillos-Sternberg.

| Class or test file | Contributor | Work |
| --- | --- | --- |
| `ClubShelfApp.java` | Corbin | Wrote app setup |
| `ClubShelfController.java` | Corbin; Alexander | Corbin: wrote actions; Alexander: added import |
| `ClubShelf.java` | Corbin; Alexander | Corbin: wrote lending; Alexander: added import |
| `Club.java` | Corbin; Alexander | Corbin: wrote records; Alexander: added import support |
| `Equipment.java` | Corbin | Wrote records and snapshots |
| `ConsoleView.java` | Angelo; Alexander | Angelo: wrote view; Alexander: added import label |
| `ActionResult.java` | Angelo | Wrote result data |
| `ItemStatus.java` | Angelo | Wrote read-only snapshots |
| `Outcome.java` | Angelo | Defined outcomes |
| `CatalogLoader.java` | Alexander | Wrote parser and validation |
| `QueueInterface.java` | Alexander | Defined queue contract |
| `LinkedQueue.java` | Alexander | Wrote FIFO queue |
| `LinkedQueue.Node` | Alexander | Wrote private node |
| `EmptyQueueException.java` | Alexander | Wrote exception |
| `LinkedQueueTest.java` | Alexander | Wrote/extended queue tests |
| `ClubShelfTest.java` | Alexander | Wrote/extended lending/import tests |
| `ClubShelfControllerTest.java` | Alexander | Wrote/extended menu/recovery tests |
| `CatalogLoaderTest.java` | Alexander | Added catalog tests |
| `ClubTest.java` | Alexander | Added club tests |
| `EquipmentTest.java` | Alexander | Added equipment/snapshot tests |
| `ConsoleViewTest.java` | Angelo | Wrote view tests |
| `ActionResultTest.java` | Angelo | Wrote result tests |
| `ItemStatusTest.java` | Angelo | Wrote snapshot tests |
| `OutcomeTest.java` | Angelo | Wrote enum tests |

**GenAI:** Team members used GenAI on their assigned work. Alexander used OpenAI
ChatGPT (Codex) for queue/import code, test drafts and additions, README revisions,
and compiling/running tests. Original contributors remain credited above; the
added tests and README revisions are GenAI-assisted.
