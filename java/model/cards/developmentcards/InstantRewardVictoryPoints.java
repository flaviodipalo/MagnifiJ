package model.cards.developmentcards;

import model.players.Player;
import view.cli.Display;

/**
 * This class represent the instant reward which gaves
 * to the user a certain amount of victory points.
 */
public class InstantRewardVictoryPoints extends InstantReward {
    private CardColor perCardColor;
    private Resources perResources;
    private Resources reward;

    public InstantRewardVictoryPoints(CardColor perCardColor, Resources perResources, Resources reward){
        this.perCardColor = perCardColor;
        this.perResources = perResources;
        this.reward = reward;
    }

    @Override
    public void receiveInstantReward(Player player) {
        if(perCardColor!=null) {
            int cardNum = player.getNumberOfCardsPerColor(perCardColor);
            for (int i = 0; i < cardNum; i++) {
                player.addResources(reward);
            }
        }
        else if(perResources!=null){
            Resources tryResources = player.getPlayerResources();
            int iterations =0;
            while (tryResources.isPositive()){
                iterations++;
                tryResources.subtractResources(perResources);
            }
            for(int i=0;i<iterations;i++){
                player.addResources(reward);
            }
        }

    }

    @Override
    public int numberOfCharacters() {
        return toString().length();
    }

    @Override
    public int emojiUsed() {
        return perResources.getEmojiCharacters() + reward.getEmojiCharacters();
    }

    @Override
    public void printInstantReward(){
        Display.println("InstantRewardVictoryPoints");
        if(perCardColor!=null){
            Display.println("\tperCardColor: " + perCardColor);
        }
        if(perResources != null){
            Display.println("\tperResources: ");
            perResources.printResources();
        }
        Display.println("\treward: ");
        reward.printResources();

    }

    @Override
    public String toString() {
        return "InstantRewardVictory: " + perCardColor.toString()
                + "  " + perResources.toString() + " --> " + reward.toString();
    }


    public Resources getReward() {
        return reward;
    }
}
