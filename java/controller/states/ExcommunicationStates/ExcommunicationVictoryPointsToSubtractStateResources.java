package controller.states.ExcommunicationStates;

import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on the victory points received
 * at the end of the game
 */
public class ExcommunicationVictoryPointsToSubtractStateResources extends ExcommunicationState implements ExcommunicationVictoryPointsToSubtractState {
    int pointsToLooseForEachResource;

    public ExcommunicationVictoryPointsToSubtractStateResources(int pointsToLooseForEachResource) {
        this.pointsToLooseForEachResource = pointsToLooseForEachResource;
    }
    public void activate(Player player) {
        player.setExcommunicationVictoryPointsToSubtractState(this);
    }

    @Override
    public Resources victoryPointsToSubtract(Player player) {
        int count;
        Resources playerResources = player.getPlayerResources();
        count = playerResources.getCoins()+playerResources.getStone()+playerResources.getServants()+playerResources.getWood();
        return new Resources(0,0,0,0,count*pointsToLooseForEachResource,0,0,0);
    }
}
