package model.cards.leadercards;

import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.players.Player;
import view.cli.Display;

import java.io.Serializable;

/**
 * This class represent the leader which gives
 * benefits to the harvest
 */
public class LeaderRewardHarvest extends LeaderReward implements Serializable{
    private int value;

    public LeaderRewardHarvest(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void receiveLeaderReward(Player player) throws TimeOutException {

        try {
            player.startHarvest(value);
        } catch (NotEnoughValueException e) {
            Display.println(e);
        }
    }
    @Override
    public String toString(){
        return "Harvest Reward value: " + value;
    }
}
