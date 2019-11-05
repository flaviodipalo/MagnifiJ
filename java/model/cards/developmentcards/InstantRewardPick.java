package model.cards.developmentcards;

import controller.exceptions.*;
import model.gameboard.TowerPosition;
import model.players.Player;
import view.cli.Display;

/**
 * This class represent the Instant reward which gives
 * the user the power to choose a card with some constraints.
 */
public class InstantRewardPick extends InstantReward{

    private CardColor cardColor;
    private int value;
    private Resources discount;

    public InstantRewardPick(CardColor cardColor, int value, Resources discount) {
        this.cardColor = cardColor;
        this.value = value;
        this.discount = discount;
    }

    @Override
    public void receiveInstantReward(Player player) throws TimeOutException, NotEmptyPositionException, PositionIsEmptyException {
        TowerPosition position = player.getMaster().chooseInstantRewardPosition(player,cardColor);
        try {
            player.pickCard(position, value, discount);
        } catch (NotEnoughResourcesException |NotEnoughValueException e){
            Display.println(e);
        }
    }

    @Override
    public int numberOfCharacters() {
        return toString().length();
    }

    @Override
    public int emojiUsed() {
        if(discount != null)
            return discount.getEmojiCharacters();
        return 0;
    }

    public CardColor getCardColor() {
        return cardColor;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void printInstantReward(){
        Display.println("---InstantRewardPick---");
        Display.println("\t" + "cardColor: " + cardColor);
        Display.println("\t" + "value: " + value);

        if(discount != null){
            Display.println("\t" + "discount:");
            discount.printResources();
        }

    }

    @Override
    public String toString() {
        return ("InstantRewardPick: " + value + " " + cardColor.toString());
    }


}
