package controller.states;

import controller.exceptions.NotEnoughResourcesException;
import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.gameboard.ProductionPosition;
import model.players.FamilyMember;

/**
 * represent the permanent reward of a Blue Card that has an impact on the Production State of the player.
 */
public class BlueCardProductionState implements ProductionState {
    private int productionBonus;
    ProductionState state;

    public BlueCardProductionState(ProductionState state, int productionBonus) {
        this.productionBonus = productionBonus;
        this.state = state;
    }

    /**
     * is used to start production taking account of other possible excommunications or bonuses
     * @param familyMember Family Member you want to start the position with.
     * @param productionPosition production position in which you want to start the production
     * @throws NotEnoughValueException if Family Member has not the required Value
     * @throws NotEnoughResourcesException if Family Member has not enough resources
     * @throws TimeOutException if timeout expires
     */
    @Override
    public void startProduction(FamilyMember familyMember, ProductionPosition productionPosition) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException {
        state.startProduction(familyMember, productionPosition, productionBonus);
    }

    @Override
    public void startProduction(FamilyMember familyMember, ProductionPosition position, int bonus) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException {
       state.startProduction(familyMember,position,bonus+ productionBonus);
    }

}
