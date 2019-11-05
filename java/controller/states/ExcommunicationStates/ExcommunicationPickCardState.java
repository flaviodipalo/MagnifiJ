package controller.states.ExcommunicationStates;

import controller.states.PickCardState;
import controller.exceptions.*;
import model.cards.developmentcards.CardColor;
import model.cards.developmentcards.Resources;
import model.gameboard.TowerPosition;
import model.players.FamilyMember;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus everytime he picks a card
 */
public class ExcommunicationPickCardState extends ExcommunicationState implements PickCardState {
    private PickCardState state;
    private CardColor cardColor;
    private int bonusDiceValue;
    private Resources discount = new Resources();


    public ExcommunicationPickCardState(CardColor cardColor,int bonusDiceValue) {
        this.cardColor = cardColor;
        this.bonusDiceValue = bonusDiceValue;
    }

    public void activate(Player player){
        this.state = player.getPickCardState();
        player.setPickCardState(this);
    }

    //
    public void pickCard(FamilyMember familyMember, TowerPosition position) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, NotEmptyPositionException, PositionIsEmptyException {
        pickCard(familyMember,position,0,new Resources());
    }
    public void pickCard(FamilyMember familyMember, TowerPosition position, int bonus, Resources resources) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, NotEmptyPositionException, PositionIsEmptyException {
        if(position.getCard().getCardColor()==cardColor){
            if(discount == null)
                discount = new Resources();
            discount.addResources(resources);
            state.pickCard(familyMember,position, bonusDiceValue +bonus,discount);
        }
        else
            state.pickCard(familyMember,position,bonus,resources);
         }
}
