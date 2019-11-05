package model.field;

import model.players.FamilyMember;
import model.players.PlayerColor;
import model.gameboard.ProductionArea;
import model.gameboard.ProductionPosition;
import model.dices.DiceColor;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by flaviodipalo on 09/07/17.
 */
public class ProductionAreaTest {
    private ProductionArea productionArea;
    FamilyMember familyMember;
    public void setUp(){
        productionArea = new ProductionArea(4);
    }

    @Test
    public void getFreePosition() throws Exception {
        setUp();
        familyMember = new FamilyMember(PlayerColor.BLUE, DiceColor.BLACK,3);
        int initialLenght = productionArea.getProduction().size();
        ProductionPosition productionPosition = productionArea.getFreePosition();
        assertTrue(productionPosition.isEmpty());
        productionPosition.setFamilyMember(familyMember);
        productionPosition = productionArea.getFreePosition();
        assertTrue(productionPosition.isEmpty());
        int finalLenght = productionArea.getProduction().size();
        assertTrue(initialLenght+2==finalLenght);
    }

    @Test
    public void hasFamilyMemberOfTheSameColor() throws Exception {
        setUp();
        ProductionPosition harvestPosition = productionArea.getFreePosition();
        harvestPosition.setFamilyMember(new FamilyMember(PlayerColor.BLUE, DiceColor.BLACK,3));
        assertTrue(productionArea.hasFamilyMemberOfTheSameColor(new FamilyMember(PlayerColor.BLUE,DiceColor.BLACK,0)));
        assertTrue(productionArea.hasFamilyMemberOfTheSameColor(new FamilyMember(PlayerColor.GREEN,DiceColor.BLACK,0)));
    }



}