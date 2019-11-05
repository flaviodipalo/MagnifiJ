package controller.states;

import model.dices.DiceColor;
import model.players.FamilyMember;
import model.players.Player;

/**
 * State caused by the activation of Sigismondo Malatesta overrides the method setNeutralMember.
 */
public class SigismondoNeutralMemberState implements NeutralMemberState {
    private Player player;

    public SigismondoNeutralMemberState(Player player) {
        this.player = player;
    }
    @Override
    public void setNeutralMember() {
        player.addFamilyMember(new FamilyMember(player.getPlayerColor(), DiceColor.NEUTRAL,3));
    }
}
