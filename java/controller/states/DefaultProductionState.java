package controller.states;

import controller.exceptions.NotEnoughResourcesException;
import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.cards.developmentcards.BonusTile;
import model.cards.developmentcards.Resources;
import model.cards.developmentcards.YellowCard;
import model.cards.developmentcards.YellowReward;
import model.gameboard.ProductionPosition;
import model.players.FamilyMember;
import model.players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * represent the Default state of the Player for the startProduction method.
 */
public class DefaultProductionState implements ProductionState {
    Player player;

    public DefaultProductionState(Player player) {
        this.player = player;
    }

    /**
     * starts the production
     * @param familyMember is the Family Member player is starting the production with
     * @param position the position in which player is starting the production
     * @throws NotEnoughValueException if the Family Member has not the required value.
     * @throws NotEnoughResourcesException if player has not enough resources.
     * @throws TimeOutException if timeout expires
     */
    @Override
    public void startProduction(FamilyMember familyMember, ProductionPosition position) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException {
        startProduction(familyMember, position,0);
    }
    @Override
    public void startProduction(FamilyMember familyMember, ProductionPosition position, int bonus) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException {
        int productionValue;
        boolean NotEnoughResourcesExceptionOccurred = false;
        boolean NotEnoughValueExceptionOccurred = false;
        //check if it has been called with a Family Member and a Position
        //this condition is important because some cards allows you to start a Production without
        //selecting this parameters
        if(familyMember!=null&&position!=null){
            productionValue = familyMember.getValue() + position.getProductionMalus()+bonus;
        }else {productionValue = bonus;}
        if (productionValue < 1)
            throw new NotEnoughValueException();
        else {
            if(position != null) {
                position.setFamilyMember(familyMember);
            }
            List<YellowCard> chosenCards;
            List<YellowCard> cards = new ArrayList<>(player.getYellowCards());

            //the Player can choose with which card he can start production.
            if(player.getMaster()!=null)
                chosenCards = player.getMaster().chooseProductionCards(cards,player);
            else{chosenCards = player.getYellowCards();}
            BonusTile bonusTile = player.getBonusTile();
            YellowReward rewardToProduce;
            Resources resourcesToGet;

            for (YellowCard card : chosenCards) {
                if (card.getRewardSize() > 1&&player.getMaster()!=null) {
                    rewardToProduce = player.getMaster().chooseRewardToProduce(card,player);
                } else {
                    rewardToProduce = card.getYellowReward().get(0);
                }

                if (rewardToProduce.getPerCardColor() != null) {
                    if(productionValue>=rewardToProduce.getValue()) {
                        int numCards = player.getNumberOfCardsPerColor(rewardToProduce.getPerCardColor());
                        resourcesToGet = rewardToProduce.getPerCardResources();
                        resourcesToGet.multiplyResources(numCards);
                        player.addResources(resourcesToGet);
                    }else NotEnoughResourcesExceptionOccurred = true;
                }

                if (rewardToProduce.getProductedResources() != null) {
                    if (productionValue >= rewardToProduce.getValue()) {
                        if (player.hasResources(rewardToProduce.getRequiredResources())) {
                            player.subtractResources(rewardToProduce.getRequiredResources());
                            player.addResources(rewardToProduce.getProductedResources());
                            System.out.println(player.getUsername() + "Produced, spent:");
                            rewardToProduce.getRequiredResources().printResources();
                            System.out.println("earned:");
                            rewardToProduce.getRequiredResources().printResources();
                        } else NotEnoughResourcesExceptionOccurred = true;
                    } else NotEnoughValueExceptionOccurred = true;
                }
            }
            //checks if Player can get Bonus from his Bonus Tile
            if (productionValue >= bonusTile.getProductionDiceValue()) {
                player.addResources(bonusTile.getProductionReward());
            }

            if(NotEnoughValueExceptionOccurred){
                throw new NotEnoughValueException();
            }
            if(NotEnoughResourcesExceptionOccurred){
                throw new NotEnoughResourcesException();
            }
        }
    }
}
