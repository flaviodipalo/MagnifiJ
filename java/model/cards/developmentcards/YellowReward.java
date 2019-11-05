package model.cards.developmentcards;

import java.io.Serializable;

/**
 * This class represent the permanent reward of the yellow card
 * which is used in the production process
 */
public class YellowReward implements Serializable{
    private int value;
    private Resources requiredResources;
    private Resources productedResources;
    private Resources perCardResources;
    private CardColor perCardColor;

    public YellowReward(int value, Resources requiredResources, Resources productedResources, Resources perCardResources, CardColor perCardColor){
        this.value = value;
        this.requiredResources = requiredResources;
        this.productedResources = productedResources;
        this.perCardResources = perCardResources;
        this.perCardColor = perCardColor;
    }

    public CardColor getPerCardColor() {
        return perCardColor;
    }

    public Resources getPerCardResources() {
        return perCardResources;
    }

    public Resources getProductedResources() {
        return productedResources;
    }

    public int getValue(){
        return value;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(requiredResources != null)
            stringBuilder.append(requiredResources.toString());
        if(productedResources != null){
            stringBuilder.append(" --> ");
            stringBuilder.append(productedResources.toString());
        }

        if(perCardResources != null){
            stringBuilder.append(" Get ");
            stringBuilder.append(perCardResources.toString());
            stringBuilder.append(" every ");
            stringBuilder.append(perCardColor);
            stringBuilder.append(" card you have");
        }
        return stringBuilder.toString();
    }

    public Resources getRequiredResources() {
        return requiredResources;
    }

}
