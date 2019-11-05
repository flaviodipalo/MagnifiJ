package controller.exceptions;

/**
 * This exception is thrown if the player tries to activate a leader card
 * when it is already activated
 */
public class LeaderCardIsActivatedException extends Exception {
    public LeaderCardIsActivatedException() {
        super("This Leader is already Active !");
    }
}
