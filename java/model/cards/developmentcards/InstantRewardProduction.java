package model.cards.developmentcards;

import controller.exceptions.NotEnoughResourcesException;
import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.players.Player;
import view.cli.Display;
import view.cli.Emoji;

/**
 * This class represent the instant reward which gave
 * to the user the power to make a production move
 */
public class InstantRewardProduction extends InstantReward{
    private int value;

    public InstantRewardProduction(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void receiveInstantReward(Player player) throws TimeOutException {
        try {
            player.startProduction(value);
        } catch (NotEnoughResourcesException | NotEnoughValueException e) {
            Display.println(e);
        }
    }

    @Override
    public int numberOfCharacters() {
        return toString().length();
    }

    @Override
    public int emojiUsed() {
        return 0;
    }
    @Override
    public void printInstantReward(){
        Display.println("---InstantRewardProduction---");
        Display.println("\tvalue: " + value);
    }

    @Override
    public String toString() {
        return "InstantRewardProduction: " + value + " " + Emoji.PRODUCTION;
    }

}
