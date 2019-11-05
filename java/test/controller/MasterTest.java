package controller;

import model.gameboard.CouncilArea;
import model.gameboard.Field;
import model.gameboard.Position;
import model.players.FamilyMember;
import model.players.Player;
import model.players.PlayerColor;
import model.cards.leadercards.LeaderCard;
import model.cards.leadercards.LeaderCardPerTurn;
import network.server.Room;
import org.junit.Test;
import util.Parser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by flaviodipalo on 08/07/17.
 */
public class MasterTest {
    private Master master; // the class under test
    private Method m;
    private static String METHOD_NAME = "initializeResources";
    private Class[] parameterTypes;
    private Object[] parameters;
    private GameConfiguration config;
    Field field;
    Deck deck;
    Room room;


    public void setUp() {
        config = GameConfiguration.getInstance();
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(PlayerColor.BLUE));
        players.add(new Player(PlayerColor.GREEN));
        players.add(new Player(PlayerColor.YELLOW));
        Parser parser = new Parser();
        try {
            deck = parser.createDeck();
        } catch (IOException e) {
            e.printStackTrace();
        }
        room = new Room(1);
        room.setDeck(deck);
        master = new Master(3);
        field = new Field(3, deck, players);
    }

    @Test
    public void changeTurnOrder() throws Exception {
        setUp();
        CouncilArea councilArea = new CouncilArea();
        Position position = councilArea.getFreePosition();
        position.setFamilyMember(new FamilyMember(PlayerColor.YELLOW, null, 0));
        position = councilArea.getFreePosition();
        position.setFamilyMember(new FamilyMember(PlayerColor.BLUE, null, 0));
        position = councilArea.getFreePosition();
        position.setFamilyMember(new FamilyMember(PlayerColor.GREEN, null, 0));
        field.setCouncil(councilArea);
        master.setField(field);
        ArrayList<Player> playerArrayList = new ArrayList<>(field.getPlayersOrder());
        assertEquals(playerArrayList.get(0).getPlayerColor(), PlayerColor.BLUE);
        assertEquals(playerArrayList.get(1).getPlayerColor(), PlayerColor.GREEN);
        assertEquals(playerArrayList.get(2).getPlayerColor(), PlayerColor.YELLOW);
        master.changeTurnOrder();
        ArrayList<Player> playerArrayList2 = new ArrayList<>(field.getPlayersOrder());
        assertEquals(playerArrayList2.get(0).getPlayerColor(), PlayerColor.YELLOW);
        assertEquals(playerArrayList2.get(1).getPlayerColor(), PlayerColor.BLUE);
        assertEquals(playerArrayList2.get(2).getPlayerColor(), PlayerColor.GREEN);
    }

    @Test
    public void deactivatePerTurnLeaderCards() throws Exception {
        setUp();
        Player newPlayer = new Player(PlayerColor.BLUE);
        ArrayList<Player> players = new ArrayList<>();
        players.add(newPlayer);
        LeaderCard newLeaderCard = new LeaderCardPerTurn("prova", "1", null, null, null);
        newPlayer.addLeaderCard(newLeaderCard);
        newLeaderCard.setActivated(true);
        field = new Field(3, deck, players);
        master.setField(field);
        master.deactivatePerTurnLeaderCards();
        assertFalse(newPlayer.getLeaderCards().get(0).isActivated());
    }

}