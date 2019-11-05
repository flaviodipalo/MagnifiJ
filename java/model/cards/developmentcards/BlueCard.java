package model.cards.developmentcards;

import model.players.Player;

import java.util.List;

/**
 * This class represent the subclass blue card of "lorenzo il magnifico"
 */
public class BlueCard extends Card {
    private BlueReward blueReward;

    public BlueCard(String id, String name, Period period, CardColor cardColor, List<Cost> cost, List<InstantReward> instantReward, BlueReward blueReward){
      super(id, name, period, cardColor, cost, instantReward);
      this.blueReward = blueReward;
    }

    /**
     * Assign the card to the player
     * @param player that get the card
     */
    public void giveCard(Player player) {
        player.addBlueCard(this);
    }

    /**
     * This method parse the permanent reward to string
     * @return string
     */
    @Override
    public String permanentRewardToString() {
        StringBuilder stringBuilder = new StringBuilder("");
        if (blueReward != null)
            stringBuilder.append(blueReward.toString());
        return stringBuilder.toString();
    }

    /**
     * @return emojiiUsedForPermanentReward
     */
    @Override
    public int emojiiUsedForPermanentReward() {
        int emojiUsed = 0;
        for(Cost cost : this.getCost()){
            emojiUsed += cost.getResourcesNeeded().getEmojiCharacters();
            emojiUsed += cost.getResourcesToPay().getEmojiCharacters();
        }
        return emojiUsed;
    }

    public BlueReward getBlueReward(){
        return this.blueReward;
    }



}
