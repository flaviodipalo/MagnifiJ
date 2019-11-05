package controller.states;

import controller.exceptions.NotEnoughValueException;
import model.gameboard.HarvestPosition;
import model.players.FamilyMember;

import java.io.Serializable;

/**
 * this interface represent the State for the method HarvestState
 */
public interface HarvestState extends Serializable {
    void startHarvest(FamilyMember familyMember, HarvestPosition harvestPosition) throws NotEnoughValueException;
    void startHarvest(FamilyMember familyMember, HarvestPosition position, int bonus) throws NotEnoughValueException;
}