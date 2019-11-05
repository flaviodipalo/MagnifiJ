package controller.states;

import model.dices.Dice;

import java.io.Serializable;

/**
 * this interface represents the State relative to setFamilyMembers .
 */
public interface FamilyMemberState extends Serializable{

    void setFamilyMembers(Dice[] dices);
    void setFamilyMembers(Dice[] dices, int bonus);
}
