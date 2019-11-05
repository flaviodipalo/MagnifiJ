package model.cards.developmentcards;

import model.players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a subclass of card.
 * This class contains the method used for the harvesting process.
 */
public class GreenCard extends Card{
    private static ArrayList<Cost> costs;
    private GreenReward greenReward;

    public GreenCard(String id, String name, Period period, CardColor cardColor, List<InstantReward> instantReward, GreenReward greenReward){
        super(id, name, period, cardColor, costs, instantReward);
        costs = new ArrayList<>();
        costs.add(new Cost(new Resources(), new Resources()));
        this.greenReward = greenReward;
    }
    @Override
    public void giveCard(Player player) {
        player.addGreenCard(this);
    }

    @Override
    public String permanentRewardToString() {
        return greenReward.toString();
    }

    @Override
    public int emojiiUsedForPermanentReward() {
        return greenReward.getHarvest().getEmojiCharacters();
    }

    public GreenReward getGreenReward() {
        return greenReward;
    }
}
