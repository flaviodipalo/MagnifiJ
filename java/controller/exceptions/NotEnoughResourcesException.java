package controller.exceptions;

/**
 * This exception is thrown if there are not enough resources to made the action
 */
public class NotEnoughResourcesException extends Exception {
    public NotEnoughResourcesException(){
        super("you have not enough resources for this action");
    }
}
