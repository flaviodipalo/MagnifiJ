package controller.states;

import model.cards.developmentcards.Resources;
import model.players.Player;

/**
 * State caused by the activation of Santa Rita overrides the method receiveInstantReward.
 */
public class SantaRitaReceiveInstantRewardState implements ReceiveInstantRewardState {
    Player player;
    public SantaRitaReceiveInstantRewardState(Player player) {
        this.player = player;
    }
    @Override
    public void receiveInstantReward(Resources resources) {
        int thisWood  =resources.getWood();
        int thisStone = resources.getStone();
        int thisServants = resources.getServants();
        int thisCoins = resources.getCoins();
        Resources newResources = new Resources(thisWood*2,thisCoins*2,thisServants*2,thisStone*2,resources.getVictoryPoints(),resources.getFaithPoints(),resources.getMilitaryPoints(),resources.getCouncilPrivilege());
        player.addResources(newResources);
    }
}
