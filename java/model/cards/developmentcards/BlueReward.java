package model.cards.developmentcards;

import java.io.Serializable;

/**
 * This class represent the permanent reward of a blue card
 */
public class BlueReward implements Serializable{
    private PickBonus pickBonus;
    private ProductionBonus productionBonus;
    private HarvestBonus harvestBonus;
    private boolean isPreacher;

    public BlueReward(PickBonus pickBonus, ProductionBonus productionBonus, HarvestBonus harvestBonus,boolean isPreacher){
        this.pickBonus = pickBonus;
        this.harvestBonus = harvestBonus;
        this.productionBonus = productionBonus;
        this.isPreacher = isPreacher;
    }


    /**
     * @return string used by the cli
     */
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(pickBonus != null)
            stringBuilder.append(pickBonus.toString());
        if(harvestBonus != null)
            stringBuilder.append(harvestBonus.toString());
        if(productionBonus != null)
            stringBuilder.append(productionBonus.toString());
        return stringBuilder.toString();
    }


    public PickBonus getPickBonus(){
        return this.pickBonus;
    }
    public ProductionBonus getProductionBonus(){
        return this.productionBonus;
    }
    public HarvestBonus getHarvestBonus(){
        return this.harvestBonus;
    }
    public boolean isPreacher() {
        return isPreacher;
    }

}
