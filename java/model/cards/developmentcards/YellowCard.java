package model.cards.developmentcards;

import model.players.Player;

import java.util.Iterator;
import java.util.List;

/**
 * This class represent the yellow card present in the game
 */
public class YellowCard extends Card{
    private List<YellowReward> yellowReward;

    public YellowCard(String id, String name, Period period, CardColor cardColor, List<Cost> cost, List<InstantReward> instantReward, List<YellowReward> yellowReward){
       super(id, name, period, cardColor, cost, instantReward);
       this.yellowReward = yellowReward;
    }

    public List<YellowReward> getYellowReward() {
        return yellowReward;
    }

    public int getRewardSize(){
        return yellowReward.size();

    }

    @Override
    public void giveCard(Player player) {
        player.addYellowCard(this);
    }

    @Override
    public String permanentRewardToString() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = yellowReward.iterator();
        stringBuilder.append("Production value: ");
        stringBuilder.append(yellowReward.get(0).getValue());
        stringBuilder.append("  ");
        while(iterator.hasNext()){
            stringBuilder.append(iterator.next().toString());
            if(iterator.hasNext())
                stringBuilder.append(" -  ");
        }
        return stringBuilder.toString();
    }

    @Override
    public int emojiiUsedForPermanentReward() {
        int emojiUsed = 0;
        for (YellowReward reward : yellowReward) {
            if(reward.getPerCardResources() != null)
                emojiUsed += reward.getPerCardResources().getEmojiCharacters();
            if(reward.getProductedResources() != null)
                emojiUsed += reward.getProductedResources().getEmojiCharacters();
            if(reward.getPerCardResources() != null)
                emojiUsed += reward.getPerCardResources().getEmojiCharacters();
        }
        return emojiUsed;
    }


}
