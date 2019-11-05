package model.cards.developmentcards;


import java.io.Serializable;

/**
 * Class used to enum the card colors
 */
public enum CardColor implements Serializable{
    GREEN("green"),
    BLUE("blue"),
    YELLOW("yellow"),
    PURPLE("purple"),
    ALL("all");

    private String color;
    CardColor(String color){
        this.color = color;
    }

    @Override
    public String toString(){
        return color;
    }

}
