package model.cards.developmentcards;

import controller.exceptions.NotEnoughValueException;
import model.players.Player;
import view.cli.Display;
import view.cli.Emoji;

/**
 * This class allows the user to make an harvest move
 */
public class InstantRewardHarvest extends InstantReward{
    private int value;
    public InstantRewardHarvest(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    @Override
    public void receiveInstantReward(Player player) {
        try {
            player.startHarvest(value);
        } catch (NotEnoughValueException e) {
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
    public String toString(){
        return ("InstantRewardHarvest: " + value + " " + Emoji.STONE);
    }

    public void printInstantReward(){
        Display.println("---InstantRewardHarvest---");
        Display.println("\tvalue: " + value);
    }

}
