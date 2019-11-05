package model.players;

import model.dices.DiceColor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by flaviodipalo on 08/07/17.
 */
public class FamilyMemberTest {
    FamilyMember familyMember = new FamilyMember(PlayerColor.BLUE, DiceColor.BLACK,3);

    @Test
    public void equals() throws Exception {
        FamilyMember familyMember1 = new FamilyMember(familyMember);
        assertEquals(familyMember,familyMember1);
    }

    @Test
    public void addValue() throws Exception {
        int value = familyMember.getValue();
        familyMember.addValue(3);
        assertEquals(familyMember.getValue(),value+3);
    }

}