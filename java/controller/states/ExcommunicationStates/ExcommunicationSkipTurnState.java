package controller.states.ExcommunicationStates;

import model.players.Player;

/**
 * If the player has to skip every first turn
 */
public class ExcommunicationSkipTurnState extends ExcommunicationState {

    public ExcommunicationSkipTurnState() {
    }

    @Override
    public void activate(Player player) {
        player.setTurnExcommunicationState(true);
    }

}
