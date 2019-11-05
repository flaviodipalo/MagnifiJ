package model.cards.leadercards;

import controller.exceptions.TimeOutException;
import model.players.Player;

import java.io.Serializable;

/**
 * This class represent the leader card reward
 */
public abstract class LeaderReward implements Serializable {
    LeaderReward() {
    }



    public void receiveLeaderReward(Player player) throws TimeOutException {
    }

    public abstract String toString();

}
