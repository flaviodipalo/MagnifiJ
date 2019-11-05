package controller.states;

import model.dices.Dice;

/**
 * Overriding of the method setFamilyMember caused by Lucrezia Borgia
 */
public class LucreziaBorgiaFamilyMemberState implements FamilyMemberState {
    private FamilyMemberState state ;

    public LucreziaBorgiaFamilyMemberState(FamilyMemberState state) {
        this.state=state;
    }

    @Override
    public void setFamilyMembers(Dice[] dices,int precedentValue) {
        state.setFamilyMembers(dices,precedentValue + 2);
    }
    @Override
    public void setFamilyMembers(Dice[] dices) {
        state.setFamilyMembers(dices,2);
    }

}
