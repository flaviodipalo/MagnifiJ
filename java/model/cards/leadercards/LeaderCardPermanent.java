package model.cards.leadercards;

import model.players.Player;

import java.util.ArrayList;

/**
 * Permanent leaderCards
 */
public class LeaderCardPermanent extends LeaderCard {

    public LeaderCardPermanent(String name, String id,  ArrayList<LeaderRequirements> leaderRequirements, String description) {
        super(name, id,  leaderRequirements, "PermanentEffect: " + description);
    }

    @Override
    public void activate(Player player) {
        player.activatePermanentLeaderCard(this);
    }

    @Override
    public void deactivate() {
        setActivated(isActivated());
    }

    @Override
    public void activate() {

    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Name: " + getName() + "\n");
        for (LeaderRequirements leaderRequirements : getLeaderRequirements()) {
            stringBuilder.append(leaderRequirements.toString() + "\n");
        }
        stringBuilder.append(description);
        return stringBuilder.toString();
    }

    @Override
    public String getEffect() {
        return "";
    }
}
