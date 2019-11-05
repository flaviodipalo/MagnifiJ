package controller.states.ExcommunicationStates;

import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on the victory points received
 * at the end of the game
 */
public class ExcommunicationVictoryPointsToSubtractStateVictoryPoints extends ExcommunicationState implements ExcommunicationVictoryPointsToSubtractState {
    int pointsNeededToLoseOneVictoryPoint;

    public ExcommunicationVictoryPointsToSubtractStateVictoryPoints(int pointsNeededToLoseOneVictoryPoint) {
        this.pointsNeededToLoseOneVictoryPoint = pointsNeededToLoseOneVictoryPoint;
    }

    public void activate(Player player) {
        player.setExcommunicationVictoryPointsToSubtractState(this);
    }

    @Override
    public Resources victoryPointsToSubtract(Player player) {
        return new Resources(0,0,0,0,player.getPlayerResources().getVictoryPoints()/pointsNeededToLoseOneVictoryPoint,0,0,0);
    }
}
