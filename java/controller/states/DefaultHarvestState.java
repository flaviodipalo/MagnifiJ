package controller.states;

import controller.exceptions.NotEnoughValueException;
import model.cards.developmentcards.BonusTile;
import model.cards.developmentcards.GreenCard;
import model.cards.developmentcards.Resources;
import model.gameboard.HarvestPosition;
import model.players.FamilyMember;
import model.players.Player;

import java.util.ArrayList;

/**
 * represent the Default state of the Player for the startHarvest method.
 */
public class DefaultHarvestState implements HarvestState {
    private Player player;
    public DefaultHarvestState(Player player) {
        this.player = player;
    }

    /**
     * startHarvest method is used from player for starting the Harvest
     * @param familyMember Family Member you want to start the harvest with
     * @param position Position in which you want to start harvest
     * @throws NotEnoughValueException if the chosen Family Member has not the required value.
     */
    @Override
    public void startHarvest(FamilyMember familyMember,HarvestPosition position) throws NotEnoughValueException {
        startHarvest(familyMember,position,0);
    }

    @Override
    public void startHarvest(FamilyMember familyMember, HarvestPosition position, int bonus) throws NotEnoughValueException {
        int harvestValue;
        Resources harvestResources = new Resources();
        if(familyMember!=null&&position!=null) {
             harvestValue = familyMember.getValue() + position.getHarvestMalus() + bonus;
        } else {harvestValue = bonus;}
        if (harvestValue<1) {
            throw new NotEnoughValueException();
        }else {
            if(position!=null) {
                position.setFamilyMember(familyMember);
            }
            BonusTile bonusTile = player.getBonusTile();

            if (harvestValue >= bonusTile.getHarvestDiceValue()) {
                harvestResources.addResources(bonusTile.getHarvestReward());
            }

            ArrayList<GreenCard> cards = new ArrayList<>(player.getGreenCards());
            for (GreenCard card : cards) {
                if (harvestValue >= card.getGreenReward().getValue()) {
                    harvestResources.addResources(card.getGreenReward().getHarvest());
                }
            }
            player.addResources(harvestResources);
        }
    }
}
