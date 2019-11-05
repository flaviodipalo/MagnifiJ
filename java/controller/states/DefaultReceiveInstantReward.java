package controller.states;

import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * represent the default state for the receiveInstantReward method.
 */
public class DefaultReceiveInstantReward implements ReceiveInstantRewardState {
    Player player;
    public DefaultReceiveInstantReward(Player player) {
        this.player = player;
    }

    @Override
    public void receiveInstantReward(Resources resources) {
        player.addResources(resources);
    }
}
