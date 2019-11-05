package controller.states;

import controller.exceptions.NotEnoughValueException;
import model.gameboard.HarvestPosition;
import model.players.FamilyMember;

/**
 * represent the permanent reward of a Blue card that has an impact on the harvest State of the player .
 */
public class BlueCardHarvestState implements HarvestState {
    int PRODUCTION_BONUS;
    HarvestState state;

    public BlueCardHarvestState(HarvestState state,int PRODUCTION_BONUS) {
        this.state = state;
        this.PRODUCTION_BONUS = PRODUCTION_BONUS;
    }

    /**
     * is used to start harvest taking in account also other possible states
     * @param familyMember the family member you want to start the harvest with
     * @param harvestPosition the position in which you want to start the harvest
     * @throws NotEnoughValueException if your family member has not the right value for starting the
     * Harvest.
     */
    @Override
    public void startHarvest(FamilyMember familyMember, HarvestPosition harvestPosition) throws NotEnoughValueException {
        state.startHarvest(familyMember, harvestPosition, PRODUCTION_BONUS);
    }

    @Override
    public void startHarvest(FamilyMember familyMember, HarvestPosition position, int bonus) throws NotEnoughValueException {
        state.startHarvest(familyMember, position, bonus + PRODUCTION_BONUS);
    }
}
