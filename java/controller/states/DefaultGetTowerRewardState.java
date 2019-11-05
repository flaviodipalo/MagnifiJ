package controller.states;

import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * represent the Default state of the Player for the getTowerReward method.
 */
public class DefaultGetTowerRewardState implements GetTowerRewardState {
    Player player;

    public DefaultGetTowerRewardState(Player player) {
        this.player = player;
    }

    /**
     * is used to get the Reward from a Tower
     * @param resources the reward Player is supposed to get from tower.
     */
    public void getTowerReward(Resources resources){
        player.addResources(resources);
    }
}
