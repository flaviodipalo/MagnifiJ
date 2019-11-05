package controller.exceptions;

/**
 * This exception handles the wrong move if the player has
 * already placed a family member
 */
public class FamilyMemberAlreadyPlacedException extends Exception {
    public FamilyMemberAlreadyPlacedException(){
        super("Family member already placed!");
    }
}
