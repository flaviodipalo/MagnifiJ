package util;

import javafx.scene.paint.Color;
import model.dices.DiceColor;
import model.players.PlayerColor;

/**
 * This class is used by the gui to convert the color strings contained in
 * other class into javafx colors
 */
public class ColorConverter {



    private ColorConverter(){

    }
    /**
     * Converts the Player color into a javafx color
     * @param playerColor to convert
     * @return the java fx color
     */
    public static Color toPaint(PlayerColor playerColor){
        switch (playerColor.toString()){
            case "BLUE" :
                return Color.BLUE;
            case "GREEN" :
                return Color.GREEN;
            case "RED" :
                return Color.RED;
            case "YELLOW" :
                return Color.YELLOW;
            default:
                return Color.GREY;

        }
    }
    /**
     * Converts the dice color into a javafx color
     * @param diceColor to convert
     * @return the java fx color
     */
    public static Color toPaint(DiceColor diceColor){
        switch (diceColor.toString()){
            case "black" :
                return Color.BLACK;
            case "orange" :
                return Color.ORANGE;
            case "white" :
                return Color.WHITE;
            default:
                return Color.GREY;
        }
    }
}
