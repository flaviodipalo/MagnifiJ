package model.cards.developmentcards;

import java.io.Serializable;

/**
 * Production bonus is used to give a value
 * in advance to the player dice
 */
public class ProductionBonus implements Serializable{

    private int value;

    public ProductionBonus(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }


    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("");
        if(value > 0){
            stringBuilder.append("ProductionBonus: " );
            stringBuilder.append(value);
        }

        return stringBuilder.toString();
    }
}
