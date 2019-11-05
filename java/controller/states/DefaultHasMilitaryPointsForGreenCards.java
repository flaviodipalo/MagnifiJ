package controller.states;

import controller.GameConfiguration;
import model.cards.developmentcards.CardColor;
import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * represent the Default state of the Player for the hasMilitaryPointForGreenCards method.
 */
public class DefaultHasMilitaryPointsForGreenCards implements HasMilitaryPointsForGreenCardsState {
    private Player player;
    private GameConfiguration config = GameConfiguration.getInstance();

    public DefaultHasMilitaryPointsForGreenCards(Player player) {
        this.player = player;
    }

    public boolean hasMilitaryPointsForGreenCards() {
        int numberOfCardsOwned = player.getNumberOfCardsPerColor(CardColor.GREEN);
        Resources militaryPointsNeeded = new Resources(0,0,0,0,0,0, config.getMilitaryPointsNeededForGreenCard()[numberOfCardsOwned],0);
        return player.hasResources(militaryPointsNeeded);
    }
}
