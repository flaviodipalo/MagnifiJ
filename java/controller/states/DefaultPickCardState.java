package controller.states;

import controller.exceptions.*;
import model.cards.developmentcards.BlueCard;
import model.cards.developmentcards.Cost;
import model.cards.developmentcards.InstantReward;
import model.cards.developmentcards.Resources;
import model.gameboard.TowerPosition;
import model.players.FamilyMember;
import model.players.Player;

import java.util.ArrayList;

/**
 * Created by flaviodipalo on 16/06/17.
 */
public class DefaultPickCardState implements PickCardState{
    private Player player;
    public DefaultPickCardState(Player player) {
        this.player = player;
    }

    public void pickCard(FamilyMember familyMember, TowerPosition position) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, PositionIsEmptyException, NotEmptyPositionException {
        pickCard(familyMember,position,0,new Resources());
    }

    public void pickCard(FamilyMember familyMember, TowerPosition position, int bonus,Resources discount) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException, PositionIsEmptyException, NotEmptyPositionException {
        ArrayList<Cost> cardCosts = new ArrayList<>();
        if(position==null||position.getCard()==null){
            throw new PositionIsEmptyException();
        }

        if(position.getCard().getCost()!=null) {
            cardCosts = new ArrayList<>(position.getCard().getCost());
        }
        Cost cardCost;
        int pickValue;
        if(familyMember!=null){
            pickValue = familyMember.getValue() + bonus;
        }else {
            pickValue = bonus;
        }
        if(cardCosts.size()>1){
            cardCost = player.getMaster().chooseCost(player,cardCosts);
            if(cardCost ==null){
                cardCost = cardCosts.get(0);
            }
        }
        else {
            if(cardCosts.size()>0){
            cardCost = cardCosts.get(0);}
            else cardCost = new Cost(new Resources(),new Resources());
        }
        Resources resourcesToPay= cardCost.getResourcesToPay();

        /**
         * the following line add to resourcesToPay the amount of resources you have
         * to pay if the tower you want to access is already occupied by a family member of
         * another player
         */
        resourcesToPay.addResources(player.towerOccupiedResources(position));
        resourcesToPay.subtractResources(discount);
        if(pickValue<position.getDiceValue()) {
            throw new NotEnoughValueException();
        }
        player.getTowerReward(position.getTowerReward());
        if(player.hasResources(cardCost.getResourcesNeeded())&&player.hasResources(resourcesToPay)){
                player.subtractResources(resourcesToPay);
                if(position.getCard().getInstantReward()!=null) {
                    for (InstantReward reward : position.getCard().getInstantReward()) {
                        reward.receiveInstantReward(player);
                    }
                }
                if((position.getCard() instanceof BlueCard)&&(((BlueCard) position.getCard()).getBlueReward()!=null) ){
                    BlueCard card = (BlueCard) position.getCard();
                    player.activateBlueCard(card.getBlueReward());
                }
                    position.getCard().giveCard(player);
                    position.setCard(null);
                    position.setFamilyMember(familyMember);
        }else throw new NotEnoughResourcesException();

    }
}
