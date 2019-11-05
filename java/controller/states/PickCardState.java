package controller.states;

import controller.exceptions.*;
import model.cards.developmentcards.Resources;
import model.gameboard.TowerPosition;
import model.players.FamilyMember;

import java.io.Serializable;

/**
 * this interface represent the State for the method pickCard
 */
@FunctionalInterface

public interface PickCardState extends Serializable {
    void pickCard(FamilyMember familyMember, TowerPosition position, int i, Resources resources) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException, NotEmptyPositionException, PositionIsEmptyException;
}
