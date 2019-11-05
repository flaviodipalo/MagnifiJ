package controller.states.ExcommunicationStates;

import model.cards.developmentcards.CardColor;
import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on the victory points received
 * at the end of the game
 */
public class ExcommunicationVictoryPointsToSubtractStateCards extends ExcommunicationState implements ExcommunicationVictoryPointsToSubtractState {

    public CardColor getCardColor() {
        return cardColor;
    }
    private CardColor cardColor;
    public ExcommunicationVictoryPointsToSubtractStateCards(CardColor cardColor) {
        this.cardColor = cardColor;
    }

    public void activate(Player player) {
        player.setExcommunicationVictoryPointsToSubtractState(this);
    }

    @Override
    public Resources victoryPointsToSubtract(Player player) {
        return new Resources();
    }
}
