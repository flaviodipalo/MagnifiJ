package model.cards.developmentcards;

import model.players.Player;
import view.cli.Display;

/**
 * This class represent the instant reward which gaves
 * to the user a certain amount of resources
 */
public class InstantRewardResources extends InstantReward {
    private Resources resources;
    public InstantRewardResources(Resources resources){
        this.resources = resources;
    }
    public Resources getResources() {
        return resources;
    }

    @Override
    public void receiveInstantReward(Player player)  {
        player.receiveInstantReward(resources);
    }

    @Override
    public int numberOfCharacters() {
        return toString().length() + resources.getEmojiCharacters();
    }

    @Override
    public int emojiUsed() {
        return resources.getEmojiCharacters();
    }
    @Override
    public void printInstantReward(){
        Display.println("---instantRewardResources---");
        resources.printResources();
    }

    @Override
    public String toString() {
        return "InstantRewardResources: " + resources.toString();
    }


}
