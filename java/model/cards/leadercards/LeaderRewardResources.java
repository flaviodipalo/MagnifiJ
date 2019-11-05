package model.cards.leadercards;

import controller.exceptions.TimeOutException;
import model.cards.developmentcards.Resources;
import model.players.Player;

import java.io.Serializable;

/**
 * This class represent a reward of a leader card that
 * gives to the user a benefit of resources
 */
public class LeaderRewardResources extends LeaderReward implements Serializable{
    private Resources resources;
    public LeaderRewardResources(Resources resources){
        this.resources = resources;
    }
    public Resources getResources() {
        return resources;
    }

    @Override
    public void receiveLeaderReward(Player player) throws TimeOutException {
        player.addResources(resources);
    }



    @Override
    public String toString() {
        return resources.toString();
    }



}
