package controller.states.ExcommunicationStates;

import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on the victory points received
 * at the end of the game
 */
public class ExcommunicationVictoryPointsToSubtractStateDefault extends ExcommunicationState implements ExcommunicationVictoryPointsToSubtractState {
    public ExcommunicationVictoryPointsToSubtractStateDefault() {

    }

    @Override
    public Resources victoryPointsToSubtract(Player player) {
        return new Resources();
    }
}
