package model.gameboard;

import controller.GameConfiguration;
import model.players.FamilyMember;
import controller.exceptions.NotEnoughValueException;
import model.cards.developmentcards.Resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Council Area represents the Council Palace of the game.
 */
public class CouncilArea implements Serializable{
    private ArrayList<CouncilPosition> council;
    private static final int DICE_VALUE = 1;
    private Resources bonusCouncilPosition;

    public CouncilArea(){
        council = new ArrayList<>();
        GameConfiguration config = GameConfiguration.getInstance();
        this.bonusCouncilPosition = config.getBonusCouncilPosition();
        council.add(new CouncilPosition(DICE_VALUE));
    }

    /**
     * is used to Place a family member in the council Area
     * @param councilPosition position where family member will be placed
     * @param familyMember Family Member the player wants to place.
     * @return Resources got from the Council Palace
     * @throws NotEnoughValueException if the Family Member has not the required value.
     */
    public Resources startCouncil(CouncilPosition councilPosition, FamilyMember familyMember) throws NotEnoughValueException{
        if(familyMember.getValue() < DICE_VALUE)
            throw new NotEnoughValueException();
        councilPosition.setFamilyMember(familyMember);
        return bonusCouncilPosition;
    }

    /**
     * Get the next free position and creates the next one
     * @return the next position available
     */
    public CouncilPosition getFreePosition(){
        CouncilPosition newCouncilPosition = new CouncilPosition(DICE_VALUE);
        council.add(newCouncilPosition);
        for(CouncilPosition councilPosition : council){
            if(councilPosition.getFamilyMember() == null) {
               return councilPosition;
            }
        }
        return newCouncilPosition;
    }

    public Resources getBonusCouncilPosition() {
        return bonusCouncilPosition;
    }

    public List<CouncilPosition> getCouncilPositions() {
        return council;
    }
}

