package controller.states;

import model.dices.Dice;
import model.players.FamilyMember;
import model.players.Player;

/**
 * represent the Default state of the Player for the setFamilyMember method.
 */
public class DefaultFamilyMemberState implements FamilyMemberState {
    private Player player;

    public DefaultFamilyMemberState(Player player) {
        this.player = player;
    }

    /**
     * is used to set the value of Player's Family Members.
     * @param dices is the value of the dices from which Family Members will get value
     */
    @Override
    public void setFamilyMembers(Dice[] dices) {
        setFamilyMembers(dices,0);
    }

    @Override
    public void setFamilyMembers(Dice[] dices,int bonus) {
        for(Dice dice : dices)
            player.addFamilyMember(new FamilyMember(player.getPlayerColor(), dice.getDiceColor(),dice.getDiceValue() + bonus));
        player.setNeutralMember();
    }
}
