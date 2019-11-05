package model.cards.leadercards;

import model.cards.developmentcards.Resources;
import view.cli.Display;

import java.io.Serializable;

/**
 * This class represent the requirements to activate a leader card
 */
public class LeaderRequirements implements Serializable{

    private Resources resources;
    private int greenCardNeeded;
    private int blueCardNeeded;
    private int purpleCardNeeded;
    private int yellowCardNeeded;

    public LeaderRequirements(Resources resources, int greenCardNeeded, int blueCardNeeded, int purpleCardNeeded, int yellowCardNeeded) {
        this.resources = resources;
        this.greenCardNeeded = greenCardNeeded;
        this.blueCardNeeded = blueCardNeeded;
        this.purpleCardNeeded = purpleCardNeeded;
        this.yellowCardNeeded = yellowCardNeeded;
    }

    public Resources getResources() {
        return resources;
    }

    public int getGreenCardNeeded() {
        return greenCardNeeded;
    }

    public int getBlueCardNeeded() {
        return blueCardNeeded;
    }

    public int getPurpleCardNeeded() {
        return purpleCardNeeded;
    }

    public int getYellowCardNeeded() {
        return yellowCardNeeded;
    }

    public void printRequirements(){
        if(resources!= null){
            resources.printResources();
        }
        if (greenCardNeeded!= 0){
            Display.println("greenCardNeeded: "+ greenCardNeeded);
        }
        if (blueCardNeeded != 0){
            Display.println("blueCardNeeded: " + blueCardNeeded);
        }
        if (purpleCardNeeded != 0){
            Display.println("purpleCardNeeded: " + purpleCardNeeded);
        }
        if (yellowCardNeeded != 0){
            Display.println("yellowCardNeeded: " + yellowCardNeeded);
        }

    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if (resources != null){
            stringBuilder.append(resources.toString());
            stringBuilder.append("\n");
        }

        if(greenCardNeeded > 0){
            stringBuilder.append("Green cards needed: ");
            stringBuilder.append(greenCardNeeded);
            stringBuilder.append("\n");
        }

        if(blueCardNeeded > 0){
            stringBuilder.append("Blue cards needed: ");
            stringBuilder.append(blueCardNeeded);
            stringBuilder.append("\n");
        }

        if(yellowCardNeeded > 0){
            stringBuilder.append("Yellow cards needed: ");
            stringBuilder.append(yellowCardNeeded);
            stringBuilder.append("\n");
        }

        if(purpleCardNeeded > 0){
            stringBuilder.append("Purple cards needed: ");
            stringBuilder.append(purpleCardNeeded);
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
