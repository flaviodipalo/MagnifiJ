package controller.states.ExcommunicationStates;

import controller.states.MarketState;
import controller.exceptions.NotEnoughValueException;
import model.cards.developmentcards.Resources;
import model.gameboard.MarketPosition;
import model.players.FamilyMember;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on the market
 */
public class ExcommunicationMarketState extends ExcommunicationState implements MarketState {
    private MarketState state;

    public ExcommunicationMarketState(){
    }

    @Override
    public void activate(Player player) {
        this.state = player.getMarketState();
        player.setMarketState(this);
    }

    @Override
    public Resources goToMarket(FamilyMember familyMember, MarketPosition marketPosition) throws NotEnoughValueException {
        return new Resources();
    }
}
