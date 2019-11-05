package model.gameboard;

import model.players.FamilyMember;

/**
 * Position used for Production
 */
public class ProductionPosition extends Position {
    private int productionMalus;

    public ProductionPosition(int diceValue,int productionMalus){
        super(diceValue);
        this.productionMalus = productionMalus;
    }

    public ProductionPosition(int diceValue,int productionMalus, FamilyMember familyMember){
        super(diceValue);
        setFamilyMember(familyMember);
        this.productionMalus=productionMalus;
    }

    public int getProductionMalus() {
        return productionMalus;
    }
}
