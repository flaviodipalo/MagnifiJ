package model.gameboard;

import controller.GameConfiguration;

import java.io.Serializable;

/**
 * Area of the field that represents Market
 */
public class MarketArea implements Serializable{
    private MarketPosition[] market;
    private static final int DICE_VALUE = 1;
    private static final int NUMBER_OF_PLAYERS_REQUIRED = 4;
    private static final int MINIMUM_MARKET_POSITIONS = 2;
    private static final int MAXIMUM_MARKET_POSITIONS = 5;
    private static final int FOUR_POSITIONS = 4;

    /**
     * Constructs the position in the market area
     * @param numPlayers number of players that are playing the game.
     */
    public MarketArea(int numPlayers){
        if (numPlayers<NUMBER_OF_PLAYERS_REQUIRED) {
            market = new MarketPosition[MINIMUM_MARKET_POSITIONS];
        } else if(numPlayers < 5) {
            market = new MarketPosition[FOUR_POSITIONS];
        } else {
            market = new MarketPosition[MAXIMUM_MARKET_POSITIONS];
        }

        for(int i = 0; i < market.length; i++ ){
            GameConfiguration config = GameConfiguration.getInstance();
            market[i] = new MarketPosition(DICE_VALUE, config.getBonusResourcesMarketPositions()[i]);
       }
    }

    public MarketPosition[] getMarket() {
        return market;
    }

}
