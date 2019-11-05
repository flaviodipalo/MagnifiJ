package controller.states.ExcommunicationStates;

import controller.states.FamilyMemberState;
import model.dices.Dice;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on his familyMembers
 */
public class ExcommunicationFamilyMemberState extends ExcommunicationState implements FamilyMemberState {
    private FamilyMemberState state;
    private int valueToSubtract;
    public ExcommunicationFamilyMemberState(int valueToSubtract) {
        this.valueToSubtract = valueToSubtract;
    }

    @Override
    public void activate(Player player) {
        this.state = player.getFamilyMemberState();
        player.setFamilyMemberState(this);
    }

    @Override
    public void setFamilyMembers(Dice[] dices,int precedentValue) {
        state.setFamilyMembers(dices,precedentValue - valueToSubtract);;
    }
    @Override
    public void setFamilyMembers(Dice[] dices) {
        state.setFamilyMembers(dices,valueToSubtract);
    }
}
