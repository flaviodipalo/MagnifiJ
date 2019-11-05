package controller.exceptions;

/**
 * This exception is thrown if the position is empty
 */
public class PositionIsEmptyException extends Exception {

    public PositionIsEmptyException(){
        super("position is Empty");
    }
}
