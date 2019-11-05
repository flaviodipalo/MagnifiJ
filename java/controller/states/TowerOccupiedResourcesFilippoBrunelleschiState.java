package controller.states;

import model.cards.developmentcards.Resources;
import model.gameboard.TowerPosition;

/**
 * this State defines the State activate by Filippo Brunelleschi that overrides  towerOccupiedResources used for defining the resources player
 * has to pay if the tower is already occupied.
 */
public class TowerOccupiedResourcesFilippoBrunelleschiState implements TowerOccupiedResourcesState {
    private TowerOccupiedResourcesState state;

    public TowerOccupiedResourcesFilippoBrunelleschiState(TowerOccupiedResourcesState state){
        this.state=state;
    }

    public Resources towerOccupiedResources(TowerPosition position){
       return new Resources();
    }

}
