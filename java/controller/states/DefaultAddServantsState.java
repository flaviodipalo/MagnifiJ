package controller.states;

import model.cards.developmentcards.Resources;
import model.players.FamilyMember;
import model.players.Player;

/**
 * represent the Default state of the Player for the addServants method.
 * addServants allows the player to add servants to a Family Member
 */
public class DefaultAddServantsState implements AddServantsState {
    private Player player;

    public DefaultAddServantsState(Player player) {
        this.player = player;
    }

    public FamilyMember addServants(FamilyMember familyMember,Resources servants){
        familyMember.addValue(servants.getServants());
        return familyMember;
    }
}
