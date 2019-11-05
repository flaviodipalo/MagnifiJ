package controller.states;

import model.cards.developmentcards.Resources;
import model.gameboard.TowerPosition;

import java.io.Serializable;
//
/**
 * this interface represent the State for the method TowerOccupiedResources
 */
@FunctionalInterface
public interface TowerOccupiedResourcesState extends Serializable {
     Resources towerOccupiedResources(TowerPosition position);
}
