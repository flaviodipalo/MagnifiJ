package model.cards.developmentcards;

import java.io.Serializable;

/**
 * This class is used to represent various resources costs of development and leader cards
 * as well as used by the bonus tile and all the game board area
 */
public class Cost implements Serializable{
    private Resources resourcesNeeded;
    private Resources resourcesToPay;

    public Cost(Resources resourcesNeeded, Resources resourcesToPay){
        this.resourcesToPay = resourcesToPay;
        this.resourcesNeeded = resourcesNeeded;
    }

    public Resources getResourcesNeeded(){
        if(resourcesNeeded == null){
            resourcesNeeded = new Resources(0,0,0,0,0,0,0,0);
        }
        return resourcesNeeded;
    }
    public Resources getResourcesToPay(){
        if(resourcesToPay == null){
            resourcesToPay = new Resources(0,0,0,0,0,0,0,0);
        }
        return resourcesToPay;
    }

    @Override
    public String toString(){
        String cost = resourcesToPay.toString();
        if(resourcesNeeded != null && !resourcesNeeded.isEmpty())
            cost += " --> " + resourcesNeeded.toString();
        return cost;
    }

    /**
     * used by the cli
     * @return number of characters
     */
    public int numberOfCharacters(){
        int characters = toString().length() + resourcesToPay.getEmojiCharacters();
        if(resourcesNeeded != null) {
            characters += resourcesNeeded.getEmojiCharacters();
        }
        return characters;
    }

}
