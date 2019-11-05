package model.cards.developmentcards;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.*;

/**
 * Created by flaviodipalo on 04/07/17.
 */
public class ResourcesTest {
    Resources aResource = new Resources(1,1,1,1,1,1,1,1);
    Resources aNegativeResource = new Resources(-1,-1,-1,-1,0,0,0,0);
    Resources emptyResource = new Resources();

    @Test
    public void addResources() throws Exception {
        Resources copiedResource = new Resources(aResource);
        aResource.addResources(copiedResource);
        assertEquals(aResource,new Resources(2,2,2,2,2,2,2,2));
    }

    @Test
    public void subtractResources() throws Exception {
        aResource.subtractResources(emptyResource);
    }

    @Test
    public void isPositive() throws Exception {
        assertTrue(aResource.isPositive());
        assertTrue(emptyResource.isPositive());
        assertFalse(aNegativeResource.isPositive());
    }

    @Test
    public void multiplyResources() throws Exception {
        aResource.multiplyResources(5);
        assertEquals(aResource,new Resources(5,5,5,5,5,5,5,5));
    }

    @Test
    public void isEmpty() throws Exception {
        assertTrue(emptyResource.isEmpty());
        assertFalse(aResource.isEmpty());
    }

    @Test
    public void isWood() throws Exception {
        Resources wood = new Resources(1,0,0,0,0,0,0,0);
        assertTrue(wood.isWood());
        assertFalse(aResource.isWood());
    }

    @Test
    public void isCoins() throws Exception {
        Resources coins = new Resources(0,1,0,0,0,0,0,0);
        assertTrue(coins.isCoins());
        assertFalse(aResource.isCoins());
    }

    @Test
    public void isStone() throws Exception {
        Resources stone = new Resources(0,0,0,1,0,0,0,0);
        assertTrue(stone.isStone());
        assertFalse(aResource.isStone());
    }

    @Test
    public void isServants() throws Exception {
        Resources servants = new Resources(0,0,1,0,0,0,0,0);
        assertTrue(servants.isServants());
        assertFalse(aResource.isServants());
    }

    @Test
    public void isMilitaryPoints() throws Exception {
        Resources militaryPoint = new Resources(0,0,0,0,0,0,1,0);
        assertTrue(militaryPoint.isMilitaryPoints());
        assertFalse(aResource.isMilitaryPoints());
    }

    @Test
    public void isFaithPoints() throws Exception {
        Resources faithPoints = new Resources(0,0,0,0,0,1,0,0);
        assertTrue(faithPoints.isFaithPoints());
        assertFalse(aResource.isFaithPoints());
    }

    @Test
    public void isVictoryPoints() throws Exception {
        Resources victoryPoints = new Resources(0,0,0,0,1,0,0,0);
        assertTrue(victoryPoints.isVictoryPoints());
        assertFalse(aResource.isVictoryPoints());
    }

    @Test
    public void isCouncilPrivilege() throws Exception {
        Resources councilPrivilege = new Resources(0,0,0,0,0,0,0,1);
        assertTrue(councilPrivilege.isCouncilPrivilege());
        assertFalse(aResource.isCouncilPrivilege());
    }

}