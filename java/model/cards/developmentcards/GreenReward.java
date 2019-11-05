package model.cards.developmentcards;

import java.io.Serializable;

/**
 * This class represent the permanent reward of a green card
 * used in the harvesting process;
 */
public class GreenReward implements Serializable{
    private int value;
    private Resources harvest;

    public GreenReward(int value, Resources harvest) {
        this.harvest = harvest;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public Resources getHarvest() {
        return harvest;
    }

    @Override
    public String toString(){
        return "Harvest: " + value + " ---> " + harvest.toString();
    }
}
