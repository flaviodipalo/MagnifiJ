package model.cards.leadercards;

import controller.exceptions.TimeOutException;
import model.players.Player;

import java.io.Serializable;

/**
 * This class is used to represent a specific reward of the federico da montefeltro leader card
 */
public class LeaderRewardFedericoDaMontefeltro extends LeaderReward implements Serializable{

    public LeaderRewardFedericoDaMontefeltro() {
        //empty but needed for the creation by the parser
    }

    @Override
    public void receiveLeaderReward(Player player) throws TimeOutException {
        player.getMaster().activateFedericoDaMontefeltro(player);
    }

    @Override
    public String toString() {
        return "Federico Da Montefeltro";
    }


}
