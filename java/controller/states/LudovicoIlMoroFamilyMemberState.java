package controller.states;

import model.dices.Dice;
import model.players.FamilyMember;
import model.players.Player;

/**
 * Overriding of the method setFamilyMember caused by Ludovico il Moro
 */
public class LudovicoIlMoroFamilyMemberState implements FamilyMemberState{
    private Player player;

    public LudovicoIlMoroFamilyMemberState(Player player) {
        this.player = player;
    }

    @Override
    public void setFamilyMembers(Dice[] dices) {
        setFamilyMembers(dices,0);
    }
    @Override
    public void setFamilyMembers(Dice[] dices,int bonus) {
        for(Dice dice:dices)
            player.addFamilyMember(new FamilyMember(player.getPlayerColor(),dice.getDiceColor(),5+bonus));
        player.setNeutralMember();
    }


}
