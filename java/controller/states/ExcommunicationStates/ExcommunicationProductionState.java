package controller.states.ExcommunicationStates;

import controller.exceptions.NotEnoughResourcesException;
import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.gameboard.ProductionPosition;
import model.players.FamilyMember;
import model.players.Player;
import controller.states.ProductionState;

/**
 * If the player has taken an excommunication which will give him a malus on the production
 */
public class ExcommunicationProductionState extends ExcommunicationState implements ProductionState {
    private int productionBonus;
    private ProductionState state;

    public ExcommunicationProductionState(int productionBonus) {
        this.productionBonus = productionBonus;
    }

    @Override
    public void activate(Player player) {
        this.state = player.getProductionState();
        player.setProductionState(this);
    }

    @Override
    public void startProduction(FamilyMember familyMember, ProductionPosition productionPosition) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException {
        state.startProduction(familyMember, productionPosition, productionBonus);
    }

    @Override
    public void startProduction(FamilyMember familyMember, ProductionPosition position, int bonus) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException {
       state.startProduction(familyMember,position,bonus+ productionBonus);
    }

}
