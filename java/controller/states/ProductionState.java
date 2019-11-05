package controller.states;

import controller.exceptions.NotEnoughResourcesException;
import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.gameboard.ProductionPosition;
import model.players.FamilyMember;

import java.io.Serializable;

/**
 * this interface represent the State for the method startProduction
 */

public interface ProductionState extends Serializable {

    void startProduction(FamilyMember familyMember, ProductionPosition position) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException;

    void startProduction(FamilyMember familyMember, ProductionPosition position, int bonus) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException;
}
