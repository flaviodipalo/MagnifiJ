package controller.exceptions;

/**
 * This exception is thrown if the leader card activated has not the
 * require leader requirement
 */
public class NotHaveLeaderRequirementsException extends Exception {
    public NotHaveLeaderRequirementsException(){
        super("You don't have leader requirements");
    }

}
