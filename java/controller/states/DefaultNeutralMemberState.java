package controller.states;

import model.dices.DiceColor;
import model.players.FamilyMember;
import model.players.Player;

/**
 * represent the Default state of the Player for the setNeutralMember method.
 */
public class DefaultNeutralMemberState implements NeutralMemberState {
    private Player player;

    public DefaultNeutralMemberState(Player player) {
        this.player = player;
    }
    @Override
    public void setNeutralMember() {
        player.addFamilyMember(new FamilyMember(player.getPlayerColor(), DiceColor.NEUTRAL,0));
    }
}
