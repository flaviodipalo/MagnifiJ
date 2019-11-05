package controller.states.ExcommunicationStates;

import model.cards.developmentcards.Resources;
import model.cards.developmentcards.YellowCard;
import model.players.Player;

import java.util.ArrayList;

/**
 * If the player has taken an excommunication which will give him a malus on the victoryPoints at
 * the end of the game
 */
public class ExcommunicationVictoryPointsToSubtractStateCardCost extends ExcommunicationState implements ExcommunicationVictoryPointsToSubtractState {
    private int victoryPointsToLose;

    public ExcommunicationVictoryPointsToSubtractStateCardCost(int victoryPointsToLose) {
        this.victoryPointsToLose = victoryPointsToLose;
    }

    @Override
    public void activate(Player player) {
        player.setExcommunicationVictoryPointsToSubtractState(this);
    }

    @Override
    public Resources victoryPointsToSubtract(Player player) {
        int count=0;
        ArrayList<YellowCard> cards = new ArrayList<>(player.getYellowCards());
        for (YellowCard card:cards){
           Resources resources = card.getCost().get(0).getResourcesToPay();
           count = count + resources.getWood()+resources.getStone();
        }
    return new Resources(0,0,0,0,count* victoryPointsToLose,0,0,0);
    }
}
