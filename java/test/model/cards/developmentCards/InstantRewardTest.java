package model.cards.developmentcards;

import controller.Deck;
import model.gameboard.Field;
import model.gameboard.Position;
import model.gameboard.TowerPosition;
import model.players.FamilyMember;
import model.players.Player;
import model.players.PlayerColor;
import model.dices.DiceColor;
import org.junit.Test;
import util.Parser;

import java.io.IOException;
import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by flaviodipalo on 09/07/17.
 */
public class InstantRewardTest {
    Player player = new Player(PlayerColor.BLUE);
    Resources aResource = new Resources(1, 1, 1, 1, 1, 1, 1, 1);
    FamilyMember aFamilyMember = new FamilyMember(PlayerColor.BLUE, DiceColor.BLACK, 10);
    Position aPosition = new TowerPosition(1, aResource);
    Field field;
    Deck deck;

    public void setUp() {
        player.setPlayerResources(new Resources());
        player.addResources(aResource);
        Parser parser = new Parser();
        try {
            deck = parser.createDeck();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InstantRewardHarvestTest() throws Exception {
        setUp();
        GreenCard greenCard = deck.getGreenCards().get(0);
        BonusTile bonusTile = deck.getBonusTiles().get(0);
        player.setBonusTile(bonusTile);
        player.addGreenCard(greenCard);
        InstantRewardHarvest instantRewardHarvest = new InstantRewardHarvest(10);
        instantRewardHarvest.receiveInstantReward(player);
        Resources greenResources = greenCard.getGreenReward().getHarvest();
        Resources tryResources = new Resources(greenResources);
        tryResources.addResources(player.getBonusTile().getHarvestReward());
        tryResources.addResources(aResource);
        assertEquals(player.getPlayerResources(), tryResources);
       }

    @Test
    public void InstantRewardProductionTest() throws Exception {
        setUp();
        YellowCard aYellowCard = null;
        for (YellowCard card : new ArrayList<>(deck.getYellowCards())) {
            if (card.getName().equals("Stonemason's Shop")) {
                aYellowCard = card;
            }
        }
        assertTrue(aYellowCard.getName().equals("Stonemason's Shop"));
        assertTrue(aYellowCard.getYellowReward().size() == 2);
        player.addYellowCard(aYellowCard);
        player.setPlayerResources(new Resources(0, 0, 0, 1, 0, 0, 0, 0));
        player.setBonusTile(new BonusTile(null, new Resources(0, 0, 0, 0, 0, 0, 1, 0), null, 1, 0));
        InstantRewardProduction instantRewardProduction = new InstantRewardProduction(10);
        instantRewardProduction.receiveInstantReward(player);
        assertEquals(player.getPlayerResources(), new Resources(0, 3, 0, 0, 0, 0, 1, 0));
    }

    @Test
    public void InstantRewardResourcesTest() throws Exception {
        player.setPlayerResources(new Resources());
        InstantRewardResources instantRewardReources = new InstantRewardResources(aResource);
        instantRewardReources.receiveInstantReward(player);
        instantRewardReources.printInstantReward();
        assertEquals(player.getPlayerResources(),aResource);
    }

    @Test
    public void InstantRewardVictoryPointsTest() throws Exception {
        setUp();
        player.setPlayerResources(new Resources());
        InstantRewardVictoryPoints instantRewardReources = new InstantRewardVictoryPoints(CardColor.BLUE,null,new Resources(0,0,0,0,1,0,0,0));
        player.setBlueCards(new ArrayList<>(deck.getFourBlueCards()));
        instantRewardReources.receiveInstantReward(player);
        instantRewardReources.printInstantReward();
        assertEquals(player.getPlayerResources(),new Resources(0,0,0,0,4,0,0,0));
    }

    @Test
    public void InstantRewardVictoryPointsTest2() throws Exception {
        setUp();
        Resources newResource = new Resources(0,0,0,0,0,0,4,0);
        player.setPlayerResources(newResource);
        InstantRewardVictoryPoints instantRewardReources = new InstantRewardVictoryPoints(null,new Resources(0,0,0,0,0,0,2,0),new Resources(0,0,0,0,1,0,0,0));
        player.setBlueCards(new ArrayList<>(deck.getFourBlueCards()));
        instantRewardReources.receiveInstantReward(player);
        instantRewardReources.printInstantReward();
        newResource.addResources(new Resources(0,0,0,0,2,0,0,0));
        assertEquals(player.getPlayerResources(),newResource);
    }



}