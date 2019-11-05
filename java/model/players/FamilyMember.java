package model.players;

import model.dices.DiceColor;
import java.io.Serializable;

/**
 * The familyMember
 */
public class FamilyMember implements Serializable{
    private PlayerColor playerColor;
    private DiceColor diceColor;
    private int value;

    /**
     * Instantiate the familyMember
     * @param playerColor the playerColor
     * @param diceColor the diceColor
     * @param value his value
     */
    public FamilyMember(PlayerColor playerColor, DiceColor diceColor, int value){
        this.playerColor = playerColor;
        this.diceColor = diceColor;
        this.value = value;
    }

    public FamilyMember(FamilyMember familyMember) {
        this.playerColor = familyMember.playerColor;
        this.diceColor = familyMember.diceColor;
        this.value = familyMember.value;
    }

    /**
     * Set the value of the familyMember
     * @param value the new value
     */
    public void setValue(int value){
        this.value = value;
    }

    /**
     * To tell whether or not a familyMember equals another familyMember
     * @param obj the object to compare
     * @return true if the objects are equals, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && (this.diceColor == ((FamilyMember) obj).diceColor) && (this.value == ((FamilyMember) obj).value) && (this.playerColor == ((FamilyMember) obj).playerColor);
    }

    /**
     * Get the value
     * @return the value
     */
    public int getValue(){
        return this.value;
    }

    /**
     * Add a value to the actual value
     * @param valueToAdd the value to add
     */
    public void addValue(int valueToAdd){
        this.value = value +valueToAdd;
    }

    /**
     * Get the color of the player
     * @return the playerColor
     */
    public PlayerColor getPlayerColor(){
        return this.playerColor;
    }

    /**
     * Get the color of the dice
     * @return the diceColor
     */
    public DiceColor getDiceColor() {
        return diceColor;
    }

}
