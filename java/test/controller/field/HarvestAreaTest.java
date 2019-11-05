package model.field;

import model.players.FamilyMember;
import model.players.PlayerColor;
import model.gameboard.HarvestArea;
import model.gameboard.HarvestPosition;
import model.dices.DiceColor;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by flaviodipalo on 08/07/17.
 */
public class HarvestAreaTest {
    private HarvestArea harvestArea;
    FamilyMember familyMember;
    public void setUp(){
        harvestArea = new HarvestArea(4);
    }

    @Test
    public void getFreePosition() throws Exception {
        setUp();
        familyMember = new FamilyMember(PlayerColor.BLUE, DiceColor.BLACK,3);
        int initialLenght = harvestArea.getHarvest().size();
        HarvestPosition harvestPosition = harvestArea.getFreePosition();
        assertTrue(harvestPosition.isEmpty());
        harvestPosition.setFamilyMember(familyMember);
        harvestPosition = harvestArea.getFreePosition();
        assertTrue(harvestPosition.isEmpty());
        int finalLenght = harvestArea.getHarvest().size();
        assertTrue(initialLenght+2==finalLenght);
    }

    @Test
    public void hasFamilyMemberOfTheSameColor() throws Exception {
        setUp();
        HarvestPosition harvestPosition = harvestArea.getFreePosition();
        harvestPosition.setFamilyMember(new FamilyMember(PlayerColor.BLUE, DiceColor.BLACK,3));
        assertTrue(harvestArea.hasFamilyMemberOfTheSameColor(new FamilyMember(PlayerColor.BLUE,DiceColor.BLACK,0)));
        assertTrue(harvestArea.hasFamilyMemberOfTheSameColor(new FamilyMember(PlayerColor.GREEN,DiceColor.BLACK,0)));
    }




}