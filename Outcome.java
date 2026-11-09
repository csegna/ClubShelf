/**
 * The list of things that can happen when a student asks to borrow, join a
 * waitlist, or return an item.
 *
 * An enum is a type with a fixed list of named values. You use a value by
 * writing its name after the type, like Outcome.SUCCESS, and you compare
 * two values with ==. The model gives back one of these values for every
 * request, so the controller and view know what happened.
 *
 * @author Angelo Pinillos-Sternberg
 * @version 2026.11.09
 */
public enum Outcome {
    /** The request worked. */
    SUCCESS,

    /** The student ID was blank or had something other than digits. */
    INVALID_STUDENT_ID,

    /** Another student has the item right now. */
    ITEM_UNAVAILABLE,

    /** The student already has this item. */
    ALREADY_BORROWED,

    /** The student is already in line for this item. */
    ALREADY_WAITING,

    /** The student tried to join the line for an item nobody has. */
    ITEM_AVAILABLE,

    /** The student tried to return an item that nobody has. */
    ITEM_NOT_BORROWED,

    /** The ID used for a return does not match the current borrower. */
    WRONG_BORROWER
}
