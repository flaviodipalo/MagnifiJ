package model.cards.leadercards;

import controller.exceptions.NotEnoughResourcesException;
import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.players.Player;
import view.cli.Display;

import java.io.Serializable;

/**
 * This class represent the leader reward that gives
 * benefits to the production
 */
public class LeaderRewardProduction extends LeaderReward implements Serializable{
    private int value;

    public LeaderRewardProduction(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void receiveLeaderReward(Player player) throws TimeOutException {

        try {
            player.startProduction(value);
        } catch (NotEnoughResourcesException | NotEnoughValueException e) {
            Display.println(e);
        }
    }

    @Override
    public String toString() {
        return "Production Reward value: " + value;
    }





}
