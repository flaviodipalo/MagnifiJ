package controller.exceptions;

/**
 * This exception is thrown if there area not enough player
 */
public class NotEnoughPlayersException extends Exception {

    public NotEnoughPlayersException(){
        super("Not enough model.players for this action");
    }
}
