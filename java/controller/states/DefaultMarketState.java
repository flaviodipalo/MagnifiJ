package controller.states;

import controller.exceptions.NotEnoughValueException;
import model.cards.developmentcards.Resources;
import model.gameboard.MarketPosition;
import model.players.FamilyMember;
import model.players.Player;

/**
 * represent the Default state of the Player for the goToMarket method.
 */
public class DefaultMarketState implements MarketState {
    private Player player;

    public DefaultMarketState(Player player){
        this.player = player;
    }

    @Override
    public Resources goToMarket(FamilyMember familyMember, MarketPosition marketPosition) throws NotEnoughValueException {
        if(familyMember.getValue() < 1)
            throw new NotEnoughValueException();
        marketPosition.setFamilyMember(familyMember);
        return marketPosition.getReward();
    }
}
