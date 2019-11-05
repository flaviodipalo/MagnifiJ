package model.cards.developmentcards;

import java.io.Serializable;

/**
 * This class represent the bonus tile assigned to the player.
 * Its data are used for the states, the production and harvest area
 */
public class BonusTile implements Serializable{

    private String id;
    private Resources productionReward;
    private Resources harvestReward;
    private int productionDiceValue;
    private int harvestDiceValue;

    public BonusTile(String id, Resources productionReward, Resources harvestReward, int productionDiceValue, int harvestDiceValue) {
        this.productionReward = productionReward;
        this.harvestReward = harvestReward;
        this.productionDiceValue = productionDiceValue;
        this.harvestDiceValue = harvestDiceValue;
        this.id = id;
    }

    public Resources getProductionReward() {
        return productionReward;
    }

    public Resources getHarvestReward() {
        return harvestReward;
    }

    public int getProductionDiceValue() {
        return productionDiceValue;
    }

    public int getHarvestDiceValue() {
        return harvestDiceValue;
    }

    public String getId() {
        return id;
    }
}
