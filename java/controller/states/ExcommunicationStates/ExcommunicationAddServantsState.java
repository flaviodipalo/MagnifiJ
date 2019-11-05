package controller.states.ExcommunicationStates;

import controller.states.AddServantsState;
import model.cards.developmentcards.Resources;
import model.players.FamilyMember;
import model.players.Player;

/**
 * If the player has taken an excommunication which will give him a malus on the servatns received
 */
public class ExcommunicationAddServantsState extends ExcommunicationState implements AddServantsState {
    AddServantsState state;
    int servantsNeededToAddValue;

    public ExcommunicationAddServantsState(int servantsNeededToAddValue) {
        this.servantsNeededToAddValue = servantsNeededToAddValue;
    }

    @Override
    public void activate(Player player) {
        this.state = player.getAddServantsState();
        player.setAddServantsState(this);
    }

    public FamilyMember addServants(FamilyMember familyMember,Resources servants){
        int servantsGot = servants.getServants();
        int valueToAdd = servantsGot/servantsNeededToAddValue;
        familyMember.addValue(valueToAdd);
        return familyMember;
    }
}
