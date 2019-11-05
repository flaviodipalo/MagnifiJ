package controller.states.ExcommunicationStates;

import controller.states.HarvestState;
import controller.exceptions.NotEnoughValueException;
import model.gameboard.HarvestPosition;
import model.players.FamilyMember;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on the harvest
 */
public class ExcommunicationHarvestState extends ExcommunicationState implements HarvestState {
    private int harvestBonus;
    private HarvestState state;

    @Override
    public void activate(Player player) {
        this.state = player.getHarvestState();
        player.setHarvestState(this);
    }

    public ExcommunicationHarvestState(int harvestBonus) {
        this.harvestBonus = harvestBonus;
    }


    @Override
    public void startHarvest(FamilyMember familyMember, HarvestPosition harvestPosition) throws NotEnoughValueException {
        state.startHarvest(familyMember, harvestPosition, harvestBonus);
    }

    @Override
    public void startHarvest(FamilyMember familyMember, HarvestPosition position, int bonus) throws NotEnoughValueException {
        state.startHarvest(familyMember, position, bonus + harvestBonus);
    }
}
