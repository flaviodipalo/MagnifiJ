package model.cards.developmentcards;

import view.cli.Emoji;

import java.io.Serializable;

/**
 * This class represent the permanent reward
 * of the purple card which gives to the
 * user a certain amount of victory points
 */
public class PurpleReward implements Serializable{
    private int victoryPoints;

    public PurpleReward(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public String toString(){
        return Emoji.VICTORY_POINTS + victoryPoints;
    }
}
