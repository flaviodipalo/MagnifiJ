package model.cards.developmentcards;

import java.io.Serializable;

/**
 * This class is used to enum the cards periods
 */
public enum  Period implements Serializable{
    FIRST(1) ,
    SECOND(2),
    THIRD(3);

    private int era;

    Period (int era){
        this.era = era;
    }

    public int toInt(){
        return era;
    }

}
