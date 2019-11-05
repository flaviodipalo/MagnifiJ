package controller.exceptions;

/**
 * This exception is thrown if the dice linked to the
 * family member has not enough value
 */
public class NotEnoughValueException extends Exception {
    public NotEnoughValueException(){
        super("[Error] Your familyMembers has not the required value");
    }

}
