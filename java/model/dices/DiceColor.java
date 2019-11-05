package model.dices;

/**
 * This class represent the color of the dices
 */
public enum DiceColor {
    ORANGE("orange"),
    BLACK("black"),
    WHITE("white"),
    NEUTRAL("neutral");

    private String color;

    DiceColor(String color) {
        this.color = color;
    }
    @Override
    public String toString() {
        return color;
    }
}
