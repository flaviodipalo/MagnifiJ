package controller.states;

import controller.exceptions.*;
import model.cards.developmentcards.CardColor;
import model.cards.developmentcards.Resources;
import model.gameboard.TowerPosition;
import model.players.FamilyMember;

/**
 * represent the permanent reward of a Blue Card that has an impact on the Pick State of the player.
 */

public class BlueCardPickCardState implements PickCardState {
    private PickCardState state;
    private CardColor cardColor;
    private int BONUS_DICE_VALUE;
    private Resources discount;

    public BlueCardPickCardState(PickCardState state,CardColor cardColor,int BONUS_DICE_VALUE,Resources discount) {
        this.state = state;
        this.cardColor = cardColor;
        this.BONUS_DICE_VALUE = BONUS_DICE_VALUE;
        this.discount = discount;
    }
    public BlueCardPickCardState(PickCardState state,CardColor cardColor,int BONUS_DICE_VALUE) {
        this.state = state;
        this.cardColor = cardColor;
        this.BONUS_DICE_VALUE = BONUS_DICE_VALUE;
        this.discount = new Resources();
    }

    /**
     * is used to pick a card from the tower taking in account also player Status
     * @param familyMember the Family Member you want to start the production with
     * @param position the position in which you want to pick the card
     * @throws NotEnoughResourcesException if familyMember has not the required resources
     * @throws NotEnoughValueException if familyMember has not the required value
     * @throws TimeOutException if timeout expires
     * @throws NotEmptyPositionException if the position is not empty
     * @throws PositionIsEmptyException if the position is empty
     */
    public void pickCard(FamilyMember familyMember, TowerPosition position) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, NotEmptyPositionException, PositionIsEmptyException {
        pickCard(familyMember,position,0,new Resources());
    }
    @Override
    public void pickCard(FamilyMember familyMember, TowerPosition position, int bonus, Resources resources) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, NotEmptyPositionException, PositionIsEmptyException {
        if(position.getCard().getCardColor()==cardColor){
            discount.addResources(resources);
            state.pickCard(familyMember,position,BONUS_DICE_VALUE+bonus,discount);
        }
        else
            state.pickCard(familyMember,position,bonus,resources);
    }
}
