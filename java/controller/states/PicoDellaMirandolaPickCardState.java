package controller.states;

import controller.exceptions.*;
import model.cards.developmentcards.Resources;
import model.gameboard.TowerPosition;
import model.players.FamilyMember;

/**
 * State caused by the activation of Pico Della Mirandola pickCard
 */
public class PicoDellaMirandolaPickCardState implements PickCardState {
    private PickCardState state;
    private Resources discount = new Resources(0,3,0,0,0,0,0,0);

    public PicoDellaMirandolaPickCardState(PickCardState state) {
        this.state = state;
    }

    public void pickCard(FamilyMember familyMember, TowerPosition position) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, NotEmptyPositionException, PositionIsEmptyException {
        pickCard(familyMember,position,0,discount);
    }
    public void pickCard(FamilyMember familyMember, TowerPosition position, int bonus, Resources resources) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, NotEmptyPositionException, PositionIsEmptyException {
        resources.addResources(discount);
        state.pickCard(familyMember, position, bonus, resources);
    }
}
