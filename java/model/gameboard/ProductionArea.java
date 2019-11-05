package model.gameboard;

import model.players.FamilyMember;
import controller.exceptions.NotEnoughPlayersException;
import model.dices.DiceColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Area of the field used to perform Production Action.
 */
public class ProductionArea implements Serializable{
    private ArrayList<ProductionPosition> production;
    private static final int DICE_VALUE = 1;
    private static final int MALUS_DICE_VALUE = -3;
    private static final int MIN_PLAYERS_REQUIRED = 3;
    private int numPlayers;

    public ProductionArea(int numPlayers) {
        production = new ArrayList<>();
        this.numPlayers = numPlayers;
        production.add(new ProductionPosition(DICE_VALUE,0));
    }

    /**
     * adds new production position only if the number of player allows this
     */
    private void addNewProductionPosition(){
        if(numPlayers >= MIN_PLAYERS_REQUIRED)
            production.add(new ProductionPosition(DICE_VALUE, MALUS_DICE_VALUE));
    }

    public List<ProductionPosition> getProduction() {
        return production;
    }

    /**
     * is used to get free postion of the Production Area
     * @return the first free position of the field.
     * @throws NotEnoughPlayersException if there are not enough players to add a new production position.
     */
    public ProductionPosition getFreePosition() throws NotEnoughPlayersException {
        addNewProductionPosition();
        for (ProductionPosition position: production) {
            if (position.isEmpty()) return position;
        }
        throw new NotEnoughPlayersException();
    }

    public boolean hasFamilyMemberOfTheSameColor(FamilyMember familyMember) {
        for(Position productionPosition :production) {
            if (productionPosition.getFamilyMember()!=null&&productionPosition.getFamilyMember().getDiceColor() == familyMember.getDiceColor()  && !DiceColor.NEUTRAL.equals(productionPosition.getFamilyMember().getDiceColor()))
                return true;
        }return false;

    }
}

