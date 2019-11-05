package model.gameboard;

import model.cards.developmentcards.Card;
import model.cards.developmentcards.Resources;

/**
 * Position of that forms the Towers
 */
public class TowerPosition extends Position {
    private Resources towerReward;
    private Card card;

    public TowerPosition(int position){
        super(position);

    }
    public TowerPosition(int position, Resources towerReward){
        super(position);
        this.towerReward = towerReward;
    }


    public void setCard(Card card){
        this.card = card;
    }

    public Card getCard(){
        return card;
    }


    public boolean hasCard(){
        return getCard() != null;
    }

    public Resources getTowerReward() {
        return towerReward;
    }
}


