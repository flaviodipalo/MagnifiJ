package model.gameboard;

import controller.GameConfiguration;
import model.players.FamilyMember;
import model.cards.developmentcards.Card;
import model.cards.developmentcards.CardColor;
import model.cards.developmentcards.Resources;
import model.dices.DiceColor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Tower represents the tower present on the field.
 */
public class Tower implements Serializable{
    private static final int NUMBER_POSITION = 4;
    private ArrayList<TowerPosition> towerPositions ;
    private static final int[] DICE_VALUE = new int[] {1,3,5,7};
    private CardColor cardColor;

    public Tower(CardColor cardColor){
        this.cardColor = cardColor;
        this.towerPositions = new ArrayList<>();
        GameConfiguration config = GameConfiguration.getInstance();
        Resources[] bonus = config.getFieldBonusMap().get(cardColor);
        for (int i = 0;i<NUMBER_POSITION;i++)
            towerPositions.add(new TowerPosition(DICE_VALUE[i],bonus[i]));
    }

    /**
     * Is used to set the card in sequence when the tower is completely free
     * @param card the card that has to be set on the tower
     */
    public void setCard(Card card){
        for(TowerPosition position : towerPositions){
            if(!position.hasCard()){
                position.setCard(card);
                break;
            }
        }
    }

    public CardColor getCardColor() {
        return cardColor;
    }

    public ArrayList<TowerPosition> getTowerPositions() {
        return towerPositions;
    }

    boolean hasFamilyMemberOfTheSameColor(FamilyMember familyMember) {
        for (TowerPosition position:towerPositions){
            if(position.getFamilyMember()!=null&&familyMember.getPlayerColor()==position.getFamilyMember().getPlayerColor() && !DiceColor.NEUTRAL.equals(position.getFamilyMember().getDiceColor()))
                return true;
        } return false;
    }
}
