package model.cards.leadercards;

import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.players.Player;

import java.util.List;

/**
 * Created by alessio on 10/06/17.
 */
public class LeaderCardPerTurn extends LeaderCard {

    private LeaderReward leaderReward;

    public LeaderCardPerTurn(String name, String id,  List<LeaderRequirements> leaderRequirements,
                             LeaderReward leaderReward, String description){
        super(name,id,  leaderRequirements, "OncePerTurnEffect: " + description);
        this.leaderReward = leaderReward;
    }

    @Override
    public void activate(Player player) throws TimeOutException, NotEnoughValueException {
        player.activePerTurnLeaderCard(this);
    }

    @Override
    public void deactivate() {
        setActivated(false);
    }

    /**
     * Activate the leader card and get the associated reward
     */
    @Override
    public void activate() {

    }
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (LeaderRequirements leaderRequirements : getLeaderRequirements())
            stringBuilder.append(leaderRequirements.toString());
        String leaderRewardString = "";
        if (leaderReward != null)
            leaderRewardString = leaderReward.toString();
        return "Name: " + getName() + "\n" + leaderRewardString + "\n" + stringBuilder + "\n" + description;
    }

    @Override
    public String getEffect() {
        if(leaderReward != null)
            return leaderReward.toString();
        return "";
    }


    public void receiveLeaderReward(Player player) throws TimeOutException,NotEnoughValueException {
        leaderReward.receiveLeaderReward(player);
    }
}
