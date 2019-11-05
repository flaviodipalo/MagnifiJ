package model.dices;

import java.io.Serializable;
import java.util.Random;

/**
 * This class represent the dices in the game
 */
public class Dice implements Serializable{
    private DiceColor diceColor;
    private int diceValue;
    private Random rand;

    public Dice(DiceColor diceColor){
        this.diceColor = diceColor;
        rand = new Random();
    }

    public Dice(DiceColor diceColor,int value){
        this.diceColor = diceColor;
        diceValue = value;
    }


    public int getDiceValue(){
        return this.diceValue;
    }

    public DiceColor getDiceColor() {
        return diceColor;
    }

    /**
     * this function generate a random model.dice value between 1 and 6
     */
    public void throwDice(){
        this.diceValue = rand.nextInt(6) + 1;
    }
}
