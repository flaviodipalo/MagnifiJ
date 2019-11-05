package model.gameboard;


import model.cards.developmentcards.Resources;

/**
 * Position used to Perform a market action.
 */
public class MarketPosition extends Position {
    private Resources reward;

    public MarketPosition(int diceValue, Resources reward){
        super(diceValue);
        this.reward = reward;
    }

    public Resources getReward() {
        return reward;
    }

    

}
