package model.cards.developmentcards;

import model.players.Player;

import java.util.List;

/**
 * This class represent a subclass of card.
 * is
 */
public class PurpleCard extends Card{
    private PurpleReward purpleReward;

    public PurpleCard(String id, String name, Period period, CardColor cardColor, List<Cost> cost, List<InstantReward> instantReward, PurpleReward purpleReward){
        super(id, name, period, cardColor, cost, instantReward);
        this.purpleReward = purpleReward;
    }
    @Override
    public void giveCard(Player player) {
        player.addPurpleCard(this);
    }

    @Override
    public String permanentRewardToString() {
        return purpleReward.toString();
    }

    @Override
    public int emojiiUsedForPermanentReward() {
        return 1;
    }

    public PurpleReward getPurpleReward() {
        return purpleReward;
    }

}
