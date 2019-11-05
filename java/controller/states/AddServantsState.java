package controller.states;

import model.cards.developmentcards.Resources;
import model.players.FamilyMember;

import java.io.Serializable;

/**
 * this interface represent a state that is used to add Servants to Player
 */
@FunctionalInterface
public interface AddServantsState extends Serializable {
    FamilyMember addServants(FamilyMember familyMember, Resources resources);
}
