package controller.exceptions;

/**
 * This exception is thrown if the position is not empty
 */
public class NotEmptyPositionException extends Exception{
    public NotEmptyPositionException() {
        super("invalid move");
    }
}
