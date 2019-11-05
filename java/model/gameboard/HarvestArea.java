package model.gameboard;

import model.players.FamilyMember;
import controller.exceptions.NotEnoughPlayersException;
import model.dices.DiceColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Harvest Area is the Area of the field that is used for harvesting.
 */
public class HarvestArea implements Serializable{
    private ArrayList<HarvestPosition> harvest;
    private static final int DICE_VALUE = 1;
    private static final int MALUS_DICE_VALUE = 3;
    private static final int MIN_PLAYERS_REQUIRED = 3;
    private int numPlayers;


    public HarvestArea(int numPlayers) {
        harvest = new ArrayList<>();
        this.numPlayers = numPlayers;
        harvest.add(new HarvestPosition(DICE_VALUE,0));
    }

    /**
     * adds a new harvest position only if the number of player permits to add positions
     */
    private void addNewHarvestPosition() {
        if(numPlayers >= MIN_PLAYERS_REQUIRED)
            harvest.add(new HarvestPosition(DICE_VALUE, MALUS_DICE_VALUE));
    }

    /**
     * is used to get the first free position of the HarvestAres
     * @return the free position of the HarvestArea
     * @throws NotEnoughPlayersException if there are not enough player to create a new position
     */
    public HarvestPosition getFreePosition() throws NotEnoughPlayersException {
        addNewHarvestPosition();
        for (HarvestPosition position: harvest) {
            if (position.isEmpty()) {
                return position;
            }
        }
        throw new NotEnoughPlayersException();
    }

    public List<HarvestPosition> getHarvest() {
        return harvest;
    }

    /**
     * checks if the HarvestArea has family Member of specified color
     * @param familyMember the family member player wants to place
     * @return true if there are family Members of the same color
     */
    public boolean hasFamilyMemberOfTheSameColor(FamilyMember familyMember) {
        for(Position harvestPosition :harvest) {
            if (harvestPosition.getFamilyMember()!=null&&harvestPosition.getFamilyMember().getDiceColor() == familyMember.getDiceColor() && !DiceColor.NEUTRAL.equals(harvestPosition.getFamilyMember().getDiceColor()))
                return true;
        }
        return false;
    }
}
