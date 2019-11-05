package controller.states;

import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * This is the default state of the player adding resources.
 */
public class DefaultAddResourcesState implements AddResourcesState{
    Player player;

    public DefaultAddResourcesState(Player player) {
        this.player = player;
    }

    public void addResources(Resources resources){
        player.addResourcesToPlayerResources(resources);
    }

}
