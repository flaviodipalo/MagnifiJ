package model.field;

import controller.Deck;
import model.gameboard.Field;
import model.gameboard.TowerPosition;
import model.cards.developmentcards.GreenCard;
import model.cards.developmentcards.Period;
import model.cards.leadercards.LeaderCard;
import model.cards.leadercards.LeaderCardPermanent;
import model.dices.DiceColor;
import org.junit.Test;
import model.players.FamilyMember;
import model.players.Player;
import model.players.PlayerColor;
import util.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by flaviodipalo on 08/07/17.
 */
public class FieldTest {
    private Field field;
    private Deck deck;
    private HashMap<String, TowerPosition> towerMap;
    private FamilyMember aFamilyMember = new FamilyMember(PlayerColor.BLUE, DiceColor.BLACK, 10);
    private List<Player> players = new ArrayList<>();

    public void setUp() {
        players.add(new Player(PlayerColor.BLUE));
        players.add(new Player(PlayerColor.BLUE));
        Parser parser = new Parser();
        try {
            deck = parser.createDeck();
        } catch (IOException e) {
            e.printStackTrace();
        }
        field = new Field(4,deck,players);
        towerMap = new HashMap<>(field.generateTowerMap());
    }

    @Test
    public void refreshField() throws Exception {
        setUp();
        TowerPosition position = towerMap.get("GreenTower1");
        position.setFamilyMember(aFamilyMember);
        towerMap = new HashMap<>(field.generateTowerMap());
        position = towerMap.get("GreenTower1");
        assertFalse(position.isEmpty());
        field.refreshField();
        assertTrue(position.isEmpty());
    }


    @Test
    public void placeCards() throws Exception {
        setUp();
        field.placeCards();
        TowerPosition position = towerMap.get("GreenTower1");
        GreenCard greenCard1 = (GreenCard)position.getCard();
        field.refreshField();
        field.placeCards();
        towerMap = new HashMap<>(field.generateTowerMap());
        position = towerMap.get("GreenTower1");
        GreenCard greenCard2 = (GreenCard)position.getCard();
        assertFalse(greenCard1==null);
        assertFalse(greenCard2==null);
        assertFalse(greenCard1.getName().equals(greenCard2.getName()));
    }

    @Test
    public void isTowerOccupied() throws Exception {
        setUp();
        TowerPosition position = towerMap.get("GreenTower1");
        assertFalse(field.isTowerOccupied(position));
        position.setFamilyMember(aFamilyMember);
        TowerPosition position2 = towerMap.get("GreenTower2");
        assertTrue(field.isTowerOccupied(position2));
    }

    @Test
    public void placeExcommunicationCards() throws Exception {
        setUp();
        assertTrue(field.getExcommunicationCard(2) == null);
        field.placeExcommunicationCards();
        assertFalse(field.getExcommunicationCard(2)==null);
    }

    @Test
    public void addActivatedLeaderCard() throws Exception {
        setUp();
        LeaderCard leaderCard = new LeaderCardPermanent("ciao",null,null,null);
        field.addActivatedLeaderCard(leaderCard);
        assertEquals(field.getActivatedLeaderCard().get(0),leaderCard);
    }

    @Test
    public void isThereAnotherSameFamilyMemberOnSimilarPosition() throws Exception {
        setUp();
        TowerPosition position = towerMap.get("GreenTower1");
        assertFalse(field.isThereAnotherSameFamilyMemberOnSimilarPosition(position,aFamilyMember));
        position.setFamilyMember(aFamilyMember);
        TowerPosition position2 = towerMap.get("GreenTower2");
        assertTrue(field.isThereAnotherSameFamilyMemberOnSimilarPosition(position2,aFamilyMember));
    }

    @Test
    public void cardEra() throws Exception {
        setUp();
        deck.shuffleTowerCards();
        ArrayList<GreenCard> greenCards = new ArrayList<>(deck.getFourGreenCards());
        greenCards.addAll(deck.getFourGreenCards());
        for (GreenCard card:greenCards){
            assertTrue(card.getPeriod().equals(Period.FIRST));
        }
        greenCards = new ArrayList<>(deck.getFourGreenCards());
        greenCards.addAll(deck.getFourGreenCards());
        for (GreenCard card:greenCards){
            assertTrue(card.getPeriod().equals(Period.SECOND));
        }
        greenCards = new ArrayList<>(deck.getFourGreenCards());
        greenCards.addAll(deck.getFourGreenCards());
        for (GreenCard card:greenCards){
            assertTrue(card.getPeriod().equals(Period.THIRD));
        }

    }




}