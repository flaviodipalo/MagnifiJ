package model.cards.developmentcards;

import controller.exceptions.NotEmptyPositionException;
import controller.exceptions.PositionIsEmptyException;
import controller.exceptions.TimeOutException;
import model.players.Player;
import java.io.Serializable;

/**
 * This class contains the instant reward of all the development card
 */
public abstract class InstantReward implements Serializable{
    //public abstract void getInstantReward(RMINetworkInterface player);
    public abstract void printInstantReward();

    @Override
    public abstract String toString();
    /**
     * method used to give to Player the result of the instant reward he got.
     * @param player who receives the instant reward
     */
    public abstract void receiveInstantReward(Player player) throws TimeOutException, NotEmptyPositionException, PositionIsEmptyException;

    public abstract int numberOfCharacters();

    public abstract int emojiUsed();
}