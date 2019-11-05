package model.gameboard;

import model.players.FamilyMember;

/**
 * Position used for starting Harvest
 */
public class HarvestPosition extends Position {
private int harvestMalus;

    public HarvestPosition(int value, int harvestMalus){
        super(value);
        this.harvestMalus = harvestMalus;
    }

    public HarvestPosition(int diceValue, FamilyMember familyMember){
        super(diceValue);
        setFamilyMember(familyMember);
    }

    public int getHarvestMalus() {
        return harvestMalus;
    }

}
