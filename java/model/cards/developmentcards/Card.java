package model.cards.developmentcards;
import model.players.Player;
import java.io.Serializable;
import java.util.List;

/**
 * This class represent the "Lorenzo il Magnifico"  development card
 */
public abstract class Card implements Serializable{
    private CardColor cardColor;
    private Period period;
    private String name;
    private List<InstantReward> instantReward;
    private List<Cost> cost;
    private String id;


    public Card(String id, String name, Period period, CardColor cardColor, List<Cost> cost, List<InstantReward> instantReward){
        this.name = name;
        this.period = period;
        this.cardColor = cardColor;
        this.instantReward = instantReward;
        this.cost = cost;
        this.id = id;
    }

    public String getName(){
        return this.name;
    }
    public Period getPeriod(){
        return  this.period;
    }
    public CardColor getCardColor(){
        return this.cardColor;
    }
    public List<InstantReward> getInstantReward(){
        return this.instantReward;
    }
    public List<Cost> getCost(){
        return this.cost;
    }



    public void giveCard(Player player) {
    }

    public String getId() {
        return id;
    }

    public abstract String permanentRewardToString();

    public abstract int emojiiUsedForPermanentReward();

    public int emojiiUsedForInstantReward(){
        int emojiUsed = 0;
        for(InstantReward reward : instantReward)
            emojiUsed += reward.emojiUsed();
        return emojiUsed;
    }

    public int emojiiUsedForCost(){
        int emojiiUsed = 0;
        for(Cost currentCost : this.getCost()){
            emojiiUsed += currentCost.getResourcesNeeded().getEmojiCharacters();
            emojiiUsed += currentCost.getResourcesToPay().getEmojiCharacters();
        }
        return emojiiUsed;
    }
}
