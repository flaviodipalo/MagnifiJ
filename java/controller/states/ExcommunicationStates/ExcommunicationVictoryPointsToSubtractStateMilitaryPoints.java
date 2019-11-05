package controller.states.ExcommunicationStates;

import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on the militaryPoints
 */
public class ExcommunicationVictoryPointsToSubtractStateMilitaryPoints extends ExcommunicationState implements ExcommunicationVictoryPointsToSubtractState {
    private int numberOfVictoryPointsForEachMilitaryPoint;

    public ExcommunicationVictoryPointsToSubtractStateMilitaryPoints(int numberOfVictoryPointsForEachMilitaryPoint) {
        this.numberOfVictoryPointsForEachMilitaryPoint = numberOfVictoryPointsForEachMilitaryPoint;
    }

    @Override
    public void activate(Player player) {
        player.setExcommunicationVictoryPointsToSubtractState(this);
    }

    @Override
    public Resources victoryPointsToSubtract(Player player) {
        return new Resources(0,0,0,0,player.getPlayerResources().getMilitaryPoints()*numberOfVictoryPointsForEachMilitaryPoint,0,0,0);
    }
}
