package model.cards.developmentcards;

import java.io.Serializable;

/**
 * This class is used for the harvesting process
 */
public class HarvestBonus implements Serializable{
    private int value;

    public HarvestBonus(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("");
        if(value > 0)
            stringBuilder.append("HarvestBonus: ").append(value);
        return stringBuilder.toString();
    }
}
