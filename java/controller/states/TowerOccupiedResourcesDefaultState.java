package controller.states;

import model.cards.developmentcards.Resources;
import model.gameboard.TowerPosition;
import model.players.Player;

/**
 * this State defines the default method in towerOccupiedResources used for defining the resources player
 * has to pay if the tower is already occupied
 */
public class TowerOccupiedResourcesDefaultState implements TowerOccupiedResourcesState {

    private Player player;

    public TowerOccupiedResourcesDefaultState(Player player){
        this.player=player;
    }

    public Resources towerOccupiedResources(TowerPosition position){

        if(player.getMaster()!=null&&player.getMaster().getField().isTowerOccupied(position))
            return new Resources(0,3,0,0,0,0,0,0);
        else return new Resources();
    }

}
