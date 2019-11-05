package controller.states;

import model.cards.developmentcards.Resources;

import java.io.Serializable;

/**
 * this interface represents a state that is used to add resources to Player taking in account many different
 * condition such as bonuses, Leader Cards, Excommunication.
 */
public interface AddResourcesState extends Serializable {
    void addResources(Resources resources);
}
