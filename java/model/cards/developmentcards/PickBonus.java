package model.cards.developmentcards;

import java.io.Serializable;
import java.util.List;

/**
 * This class represent the pick bonus given to the user
 * in order to let him choose a certain card
 */
public class PickBonus implements Serializable{
    private CardColor cardColor;
    private int value;
    private List<Resources> discount;


    public PickBonus(CardColor cardColor, int value, List<Resources> discount){
        this.cardColor = cardColor;
        this.value = value;
        this.discount = discount;

    }

    public CardColor getCardColor(){
        return this.cardColor;
    }
    public int getValue(){
        return this.value;
    }


    public List<Resources> getDiscount(){
        return this.discount;
    }


    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("");

        if(value > 0)
            stringBuilder.append("PickBonus: " + value + " on " + cardColor);
        if(discount != null) {
            stringBuilder.append(" with the discount of:");
            for(Resources r : discount)
                stringBuilder.append(" "+ r.toString());
        }
        return stringBuilder.toString();
    }
}
