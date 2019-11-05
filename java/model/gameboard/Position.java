package model.gameboard;

import model.players.FamilyMember;

import java.io.Serializable;

/**
 * Position in which player can place Family Members
 */

public abstract class Position implements Serializable{
    private int diceValue;
    private FamilyMember familyMember;


    public Position (int diceValue){
        this.diceValue = diceValue;
        this.familyMember = null;
    }

    public void setFamilyMember(FamilyMember member){
        this.familyMember = member;
    }
    public FamilyMember getFamilyMember(){return familyMember;}
    public int getDiceValue() {
        return diceValue;
    }

    /**
     * this method is used to check if the position is empty
     * @return true if the position is empty
     */
    public boolean isEmpty(){
        return familyMember ==null;
    }

    /**
     * is used to covert a position to String
     * @return the converted String.
     */
    public String familyMemberToString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(familyMember == null)
            return stringBuilder.append("position empty").toString();
        return stringBuilder.append("of player " + familyMember.getPlayerColor()).toString();

    }

}
