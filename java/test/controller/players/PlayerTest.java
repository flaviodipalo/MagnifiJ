package model.players;

import controller.Deck;
import controller.states.*;
import controller.states.ExcommunicationStates.*;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.cards.leadercards.LeaderCardPerTurn;
import model.cards.leadercards.LeaderCardPermanent;
import model.cards.leadercards.LeaderRequirements;
import model.dices.DiceColor;
import model.gameboard.*;
import org.junit.Test;
import util.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by flaviodipalo on 05/07/17.
 */
public class PlayerTest {

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
    public void addResourcesToPlayerResources() throws Exception {
        setUp();
        Resources resources = player.getPlayerResources();
        assertEquals(resources, aResource);
    }

    @Test
    public void addResources() throws Exception {
        setUp();
        Resources resources = player.getPlayerResources();
        assertEquals(resources, aResource);
    }

    @Test
    public void addResources1() throws Exception {
        setUp();
        //ArrayList<ExcommunicationCard> excommunicationCards = new ArrayList<ExcommunicationCard>(deck.getExcommunicationCards());
        //System.out.println(excommunicationCards.size());
        Resources resources = player.getPlayerResources();
        assertEquals(resources, aResource);
    }

    @Test
    public void subtractResources() throws Exception {
        setUp();
        player.subtractResources(aResource);
        assertEquals(player.getPlayerResources(), new Resources());
    }


    @Test
    public void getNumberOfCardsPerColor() throws Exception {
        setUp();
        List<BlueCard> card = deck.getFourBlueCards();
        ArrayList<BlueCard> cardArray = new ArrayList<>(card);
        player.setBlueCards(cardArray);
        assertEquals(player.getNumberOfCardsPerColor(CardColor.BLUE), 4);
    }

    @Test
    public void addFamilyMember() throws Exception {
        setUp();
        player.addFamilyMember(aFamilyMember);
        assertEquals(player.getFamilyMembers().get(0), aFamilyMember);
    }

    @Test
    public void goToMarket() throws Exception {
        player.setPlayerResources(new Resources());
        MarketPosition aMarketPosition = new MarketPosition(1, aResource);
        Resources resources = player.goToMarket(aFamilyMember, aMarketPosition);
        player.addResourcesToPlayerResources(resources);
        assertEquals(player.getPlayerResources(), (aMarketPosition).getReward());
        ExcommunicationCard excommunicationCard = new ExcommunicationCard(null, null, new ExcommunicationMarketState(), null);
        excommunicationCard.activate(player);
        resources = player.goToMarket(aFamilyMember, aMarketPosition);
        player.addResourcesToPlayerResources(resources);
        assertEquals(player.getPlayerResources(), (aMarketPosition).getReward());
    }

    @Test
    public void startProduction() throws Exception {
        setUp();
        ExcommunicationCard excommunicationCard = null;
        ProductionPosition position = new ProductionPosition(1, 0);
        YellowCard aYellowCard = null;
        for (YellowCard card : new ArrayList<>(deck.getYellowCards())) {
            if (card.getName().equals("Tax Office")) {
                aYellowCard = card;
            }
        }
        assertTrue(aYellowCard.getName().equals("Tax Office"));
        assertEquals(aYellowCard.getYellowReward().get(0).getPerCardColor(), CardColor.GREEN);
        player.addYellowCard(aYellowCard);
        player.setPlayerResources(new Resources());
        player.setGreenCards(deck.getFourGreenCards());
        player.setBonusTile(new BonusTile(null, new Resources(1,0,0,0,0,0,0,0), null, 1, 0));
        CardColor cardColor = aYellowCard.getYellowReward().get(0).getPerCardColor();
        int numCards = player.getNumberOfCardsPerColor(cardColor);
        assertEquals(numCards, 4);
        FamilyMember anotherFamilyMember = new FamilyMember(PlayerColor.BLUE,DiceColor.BLACK,5);
        player.startProduction(anotherFamilyMember, position);
        assertEquals(player.getPlayerResources(), new Resources(1, 4, 0, 0, 0, 0, 0, 0));
    }

    @Test
    public void startProduction1() throws Exception {
        setUp();
        ProductionPosition position = new ProductionPosition(1, 0);
        YellowCard aYellowCard = null;
        for (YellowCard card : new ArrayList<>(deck.getYellowCards())) {
            if (card.getName().equals("Tax Office")) {
                aYellowCard = card;
            }
        }
        assertTrue(aYellowCard.getName().equals("Tax Office"));
        assertEquals(aYellowCard.getYellowReward().get(0).getPerCardColor(), CardColor.GREEN);
        player.addYellowCard(aYellowCard);
        player.setPlayerResources(new Resources());
        player.setGreenCards(deck.getFourGreenCards());
        player.setBonusTile(new BonusTile(null, new Resources(), null, 100, 0));
        CardColor cardColor = aYellowCard.getYellowReward().get(0).getPerCardColor();
        int numCards = player.getNumberOfCardsPerColor(cardColor);
        assertEquals(numCards, 4);
        player.startProduction(aFamilyMember, position);
        assertEquals(player.getPlayerResources(), new Resources(0, 4, 0, 0, 0, 0, 0, 0));
    }

    @Test
    public void startProduction6() throws Exception {
        setUp();
        YellowCard aYellowCard = null;
        for (YellowCard card : new ArrayList<>(deck.getYellowCards())) {
            if (card.getName().equals("Tax Office")) {
                aYellowCard = card;
            }
        }

        assertTrue(aYellowCard.getName().equals("Tax Office"));
        assertEquals(aYellowCard.getYellowReward().get(0).getPerCardColor(), CardColor.GREEN);
        assertEquals(aYellowCard.getYellowReward().get(0).getRequiredResources(), null);
        assertEquals(aYellowCard.getYellowReward().get(0).getProductedResources(), null);
        player.addYellowCard(aYellowCard);
        player.setPlayerResources(new Resources());
        player.setGreenCards(deck.getFourGreenCards());
        player.setBonusTile(new BonusTile(null, new Resources(), null, 100, 0));
        CardColor cardColor = aYellowCard.getYellowReward().get(0).getPerCardColor();
        int numCards = player.getNumberOfCardsPerColor(cardColor);
        assertEquals(numCards, 4);
        player.startProduction(10);
        assertEquals(player.getPlayerResources(), new Resources(0, 4, 0, 0, 0, 0, 0, 0));
    }

    @Test
    public void startProduction2() throws Exception {
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
        player.startProduction(10);
        assertEquals(player.getPlayerResources(), new Resources(0, 3, 0, 0, 0, 0, 1, 0));
    }

    @Test
    public void startProduction3() throws Exception {
        setUp();

        YellowCard aYellowCard = null;
        for (YellowCard card : new ArrayList<>(deck.getYellowCards())) {
            if (card.getName().equals("Garden")) {
                aYellowCard = card;
            }
        }
        assertTrue(aYellowCard.getName().equals("Garden"));
        assertTrue(aYellowCard.getYellowReward().size() == 1);
        player.addYellowCard(aYellowCard);
        player.setPlayerResources(new Resources(0, 0, 0, 0, 0, 0, 0, 0));
        player.setBonusTile(new BonusTile(null, new Resources(0, 0, 0, 0, 0, 0, 1, 0), null, 1, 0));
        player.startProduction(10);
        assertEquals(player.getPlayerResources(), new Resources(0, 0, 0, 0, 5, 0, 1, 0));
    }

    @Test
    public void startHarvest() throws Exception {
        setUp();
        HarvestPosition harvestPosition = new HarvestPosition(1, 0);
        GreenCard greenCard = deck.getGreenCards().get(0);
        BonusTile bonusTile = deck.getBonusTiles().get(0);
        aFamilyMember = new FamilyMember(PlayerColor.BLUE, null, 10);
        player.setBonusTile(bonusTile);
        player.addGreenCard(greenCard);
        player.startHarvest(aFamilyMember, harvestPosition);
        Resources greenResources = greenCard.getGreenReward().getHarvest();
        Resources tryResources = new Resources(greenResources);
        tryResources.addResources(player.getBonusTile().getHarvestReward());
        tryResources.addResources(aResource);
        assertEquals(player.getPlayerResources(), tryResources);
    }

    @Test
    public void startHarvest1() throws Exception {
        setUp();
        HarvestPosition harvestPosition = new HarvestPosition(1, 0);
        GreenCard greenCard = deck.getGreenCards().get(0);
        BonusTile bonusTile = deck.getBonusTiles().get(0);
        player.setBonusTile(bonusTile);
        player.addGreenCard(greenCard);
        aFamilyMember = new FamilyMember(PlayerColor.BLUE, null, 2);
        assertTrue(player.getHarvestState() instanceof DefaultHarvestState);
        player.setHarvestState(new BlueCardHarvestState(player.getHarvestState(), 2));
        player.setHarvestState(new BlueCardHarvestState(player.getHarvestState(), 6));
        ExcommunicationCard excommunicationCard = new ExcommunicationCard("2", null, new ExcommunicationHarvestState(-3), "");
        excommunicationCard.activate(player);
        assertTrue(player.getHarvestState() instanceof ExcommunicationHarvestState);
        player.startHarvest(aFamilyMember, harvestPosition);
        Resources greenResources = greenCard.getGreenReward().getHarvest();
        Resources tryResources = new Resources(greenResources);
        tryResources.addResources(player.getBonusTile().getHarvestReward());
        tryResources.addResources(aResource);
        assertEquals(player.getPlayerResources(), tryResources);
    }

    @Test
    public void startHarvest2() throws Exception {
        setUp();
        HarvestPosition harvestPosition = new HarvestPosition(1, 0);
        Resources tryResources;
        BonusTile bonusTile = deck.getBonusTiles().get(0);
        player.setBonusTile(bonusTile);
        ArrayList<GreenCard> greenCards = new ArrayList<>(deck.getFourGreenCards());
        player.setGreenCards(greenCards);
        tryResources = new Resources(aResource);
        for (GreenCard card : greenCards) {
            tryResources.addResources(card.getGreenReward().getHarvest());
        }
        tryResources.addResources(bonusTile.getHarvestReward());
        player.setPlayerResources(aResource);
        player.startHarvest(aFamilyMember, harvestPosition);
        assertEquals(player.getPlayerResources(), tryResources);
    }

    @Test
    public void startHarvest3() throws Exception {
        setUp();
        GreenCard greenCard = deck.getGreenCards().get(0);
        BonusTile bonusTile = deck.getBonusTiles().get(0);
        player.setBonusTile(bonusTile);
        player.addGreenCard(greenCard);
        player.startHarvest(10);
        Resources greenResources = greenCard.getGreenReward().getHarvest();
        Resources tryResources = new Resources(greenResources);
        tryResources.addResources(player.getBonusTile().getHarvestReward());
        tryResources.addResources(aResource);
        assertEquals(player.getPlayerResources(), tryResources);
    }


    @Test
    public void pickCard() throws Exception {
        setUp();
        GreenCard aGreenCard = new GreenCard(null, "prova", null, CardColor.GREEN, null, null);
        assertTrue(player.getGreenCards().isEmpty());
        TowerPosition towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(aGreenCard);
        player.pickCard(aFamilyMember, towerPosition);
        assertFalse(player.getGreenCards().isEmpty());
        aResource.addResources(aResource);
        assertEquals(player.getPlayerResources(), aResource);
    }

    @Test
    public void pickCard1() throws Exception {
        setUp();
        aFamilyMember = new FamilyMember(null, null, 1);
        GreenCard aGreenCard = new GreenCard(null, "prova", null, CardColor.GREEN, null, null);
        ExcommunicationCard excommunicationCard = new ExcommunicationCard(null, null, new ExcommunicationPickCardState(CardColor.GREEN, -1), null);
        excommunicationCard.activate(player);
        assertTrue(player.getGreenCards().isEmpty());
        TowerPosition towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(aGreenCard);
        player.setPickCardState(new BlueCardPickCardState(player.getPickCardState(), CardColor.GREEN, 6));
        player.pickCard(aFamilyMember, towerPosition);
        assertFalse(player.getGreenCards().isEmpty());
        aResource.addResources(aResource);
        assertEquals(player.getPlayerResources(), aResource);
    }

    @Test
    public void pickCard2() throws Exception {
        setUp();
        GreenCard aGreenCard = new GreenCard(null, "prova", null, CardColor.GREEN, null, null);
        assertTrue(player.getGreenCards().isEmpty());
        TowerPosition towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(aGreenCard);
        player.pickCard(towerPosition, 9, new Resources());
        assertFalse(player.getGreenCards().isEmpty());
        aResource.addResources(aResource);
        assertEquals(player.getPlayerResources(), aResource);
    }

    @Test
    public void removeLeaderCard() throws Exception {
        player.addLeaderCard(new LeaderCardPermanent("aLeader", null, null, null));
        assertFalse(player.getLeaderCards().isEmpty());
        player.removeLeaderCard("aLeader");
        assertTrue(player.getLeaderCards().isEmpty());
    }

    @Test
    public void setNeutralMember() throws Exception {
        player.setNeutralMember();
        FamilyMember familyMember = player.getFamilyMembers().get(0);
        assertEquals(familyMember.getValue(), 0);
        player.setNeutralMemberState(new SigismondoNeutralMemberState(player));
        player.setNeutralMember();
        familyMember = player.getFamilyMembers().get(1);
        assertEquals(familyMember.getValue(), 3);
    }

    @Test
    public void addServants() throws Exception {
        FamilyMember familyMember = new FamilyMember(PlayerColor.BLUE, null, 0);
        player.addServants(familyMember, new Resources(0, 0, 2, 0, 0, 0, 0, 0));
        assertEquals(familyMember.getValue(), 2);
        familyMember = new FamilyMember(PlayerColor.BLUE, null, 0);
        player.setAddServantsState(new ExcommunicationAddServantsState(2));
        player.addServants(familyMember, new Resources(0, 0, 4, 0, 0, 0, 0, 0));
        assertEquals(familyMember.getValue(), 2);
        familyMember = new FamilyMember(PlayerColor.BLUE, null, 0);
        player.setAddServantsState(new ExcommunicationAddServantsState(2));
        player.addServants(familyMember, new Resources(0, 0, 5, 0, 0, 0, 0, 0));
        assertEquals(familyMember.getValue(), 2);

    }

    @Test
    public void hasResources() throws Exception {
        player.setPlayerResources(aResource);
        assertTrue(player.hasResources(aResource));
        Resources anotherResource = new Resources(10, 0, 0, 0, 0, 0, 0, 0);
        assertFalse(player.hasResources(anotherResource));
    }

    @Test
    public void isTurnExcommunicationState() throws Exception {
        assertFalse(player.isTurnExcommunicationState());
        ExcommunicationCard excommunicationCard = new ExcommunicationCard(null, null, new ExcommunicationSkipTurnState(), null);
        excommunicationCard.activate(player);
        assertTrue(player.isTurnExcommunicationState());
    }


    @Test
    public void canPickCard() throws Exception {
        setUp();
        GreenCard aGreenCard = new GreenCard(null, "prova", null, CardColor.GREEN, null, null);
        TowerPosition towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(aGreenCard);
        assertTrue(player.canPickCard(towerPosition));
        player.setGreenCards(new ArrayList<>(deck.getFourGreenCards()));
        player.setGreenCards(new ArrayList<>(deck.getFourGreenCards()));
        assertFalse(player.canPickCard(towerPosition));
    }

    @Test
    public void canPickCard1() throws Exception {
        setUp();
        GreenCard aGreenCard = new GreenCard(null, "prova", null, CardColor.GREEN, null, null);
        TowerPosition towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(aGreenCard);
        assertTrue(player.canPickCard(towerPosition));
        LeaderRequirements leaderRequirements = new LeaderRequirements(new Resources(), 0, 0, 0, 0);
        ArrayList<LeaderRequirements> leaderRequirementsArrayList = new ArrayList<>();
        leaderRequirementsArrayList.add(leaderRequirements);
        player.activatePermanentLeaderCard(new LeaderCardPermanent("Cesare Borgia", null, leaderRequirementsArrayList, null));
        player.setGreenCards(new ArrayList<>(deck.getFourGreenCards()));
        assertTrue(player.canPickCard(towerPosition));
    }


    @Test
    public void canPickCard2() throws Exception {
        setUp();
        GreenCard aGreenCard = new GreenCard(null, "prova", null, CardColor.GREEN, null, null);
        TowerPosition towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(aGreenCard);
        assertTrue(player.canPickCard(towerPosition));
        LeaderRequirements leaderRequirements = new LeaderRequirements(new Resources(), 0, 0, 0, 0);
        ArrayList<LeaderRequirements> leaderRequirementsArrayList = new ArrayList<>();
        leaderRequirementsArrayList.add(leaderRequirements);
        player.setGreenCards(new ArrayList<>(deck.getFourGreenCards()));
        assertFalse(player.canPickCard(towerPosition));
        player.addResourcesToPlayerResources(new Resources(0, 0, 0, 0, 0, 0, 100, 0));
        assertTrue(player.canPickCard(towerPosition));
    }

    @Test
    public void isPositionOccupied() throws Exception {
        Position position = new TowerPosition(1, aResource);
        position.setFamilyMember(aFamilyMember);
        assertTrue(player.isPositionOccupied(position));
        player.setIsPositionOccupiedState(new IsPositionOccupiedLudovicoAriostoState(player.getIsPositionOccupiedState()));
        assertFalse(player.isPositionOccupied(position));
    }

    @Test
    public void receiveInstantReward() throws Exception {
        player.setPlayerResources(new Resources());
        player.receiveInstantReward(aResource);
        assertEquals(player.getPlayerResources(), aResource);
        player.setPlayerResources(new Resources());
        player.setReceiveInstantRewardState(new SantaRitaReceiveInstantRewardState(player));
        player.receiveInstantReward(aResource);
        assertEquals(player.getPlayerResources(), new Resources(2, 2, 2, 2, 1, 1, 1, 1));
    }

    @Test
    public void activateBlueCard() throws Exception {
        setUp();
        player.setPlayerResources(new Resources());
        GreenCard aGreenCard = new GreenCard(null, "prova", null, CardColor.GREEN, null, null);
        TowerPosition towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(aGreenCard);
        player.pickCard(aFamilyMember, towerPosition);
        assertEquals(player.getPlayerResources(), aResource);

        BlueCard isPreacher = null;
        for (BlueCard card : new ArrayList<>(deck.getBlueCards())) {
            if (card.getId().equals("55")) {
                isPreacher = card;
            }
        }
        if (isPreacher != null) {
            player.activateBlueCard(isPreacher.getBlueReward());
            assertTrue(player.getGetTowerRewardState() instanceof PreacherGetTowerRewardState);
        }
        player.setGetTowerRewardState(new PreacherGetTowerRewardState(player.getGetTowerRewardState()));
        assertTrue(player.getGetTowerRewardState() instanceof PreacherGetTowerRewardState);
        player.setPlayerResources(new Resources());
        aGreenCard = new GreenCard(null, "prova", null, CardColor.GREEN, null, null);
        towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(aGreenCard);
        player.pickCard(aFamilyMember, towerPosition);
        assertEquals(player.getPlayerResources(), new Resources());

    }

    @Test
    public void activatePermanentLeaderCard() throws Exception {
        setUp();
        ArrayList<LeaderRequirements> leaderRequirementsArrayList = new ArrayList<>();
        LeaderCardPermanent found = new LeaderCardPermanent("Lucrezia Borgia", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getFamilyMemberState() instanceof LucreziaBorgiaFamilyMemberState);
        found = new LeaderCardPermanent("Ludovico il Moro", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getFamilyMemberState() instanceof LudovicoIlMoroFamilyMemberState);
        found = new LeaderCardPermanent("Sigismondo Malatesta", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getNeutralMemberState() instanceof SigismondoNeutralMemberState);
        found = new LeaderCardPermanent("Pico della Mirandola", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getPickCardState() instanceof PicoDellaMirandolaPickCardState);
        found = new LeaderCardPermanent("Ludovico Ariosto", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getIsPositionOccupiedState() instanceof IsPositionOccupiedLudovicoAriostoState);
        found = new LeaderCardPermanent("Cesare Borgia", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getHasMilitaryPointsForGreenCardsState() instanceof CesareBorgiaHasMilitaryPointsForGreenCards);
        found = new LeaderCardPermanent("Filippo Brunelleschi", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getTowerOccupiedResourcesState() instanceof TowerOccupiedResourcesFilippoBrunelleschiState);
        found = new LeaderCardPermanent("Santa Rita", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getReceiveInstantRewardState() instanceof SantaRitaReceiveInstantRewardState);
        found = new LeaderCardPermanent("Sisto IV", null, leaderRequirementsArrayList, "description");
        player.activatePermanentLeaderCard(found);
        assertTrue(player.getShowSupportBonusState() instanceof SistoIVShowSupportBonusState);
    }

    /**
     * testing for a generic leader card that returns Resources
     * @throws Exception
     */
    @Test
    public void activePerTurnLeaderCard() throws Exception {
        setUp();
        LeaderCard theLeaderCard = null;
        player.setPlayerResources(new Resources());
        ArrayList<LeaderCard> leaderCards = new ArrayList<>(deck.getLeaderCards());
        for (LeaderCard leaderCard : leaderCards) {
            if (leaderCard.getName().equals("Girolamo Savonarola")) {
                theLeaderCard = leaderCard;
            }
        }
        player.addResources(new Resources(0, 18, 0, 0, 0, 0, 0, 0));
        assertFalse(theLeaderCard.equals(null));
        theLeaderCard.activate(player);
        assertEquals(player.getPlayerResources(), new Resources(0, 18, 0, 0, 0, 1, 0, 0));
    }

    /**
     * testing for Leaonardo da Vinci
     * @throws Exception
     */
    @Test
    public void activePerTurnLeaderCard1() throws Exception {
        setUp();
        LeaderCard theLeaderCard = null;
        player.setPlayerResources(new Resources());
        ArrayList<LeaderCard> leaderCards = new ArrayList<>(deck.getLeaderCards());
        for (LeaderCard leaderCard : leaderCards) {
            if (leaderCard.getName().equals("Leonardo da Vinci")) {
                theLeaderCard = leaderCard;
            }
        }
        assertFalse(theLeaderCard.equals(null));
    }

    /**
     * testing for Francesco Sforza , his Value only allows to pick the harvest Bonus from harvest
     * @throws Exception
     */
    @Test
    public void activePerTurnLeaderCard2() throws Exception {
        setUp();
        LeaderCard theLeaderCard = null;
        player.setPlayerResources(new Resources());
        ArrayList<LeaderCard> leaderCards = new ArrayList<>(deck.getLeaderCards());
        for (LeaderCard leaderCard : leaderCards) {
            if (leaderCard.getName().equals("Francesco Sforza")) {
                theLeaderCard = leaderCard;
            }
        }
        assertFalse(theLeaderCard.equals(null));
        player.setBonusTile(new BonusTile(null, new Resources(), aResource, 100, 0));
        player.setPlayerResources(new Resources());
        theLeaderCard.activate(player);
        assertEquals(player.getPlayerResources(),aResource);
    }

    @Test
    public void removeFamilyMember() throws Exception{
        ArrayList<FamilyMember> familyMembers= new ArrayList<>();
        familyMembers.add(aFamilyMember);
        player.setFamilyMembers(familyMembers);
        assertTrue(!player.getFamilyMembers().isEmpty());
        player.setTemporaryFamilyMember(aFamilyMember);
        player.removeFamilyMember();
        assertTrue(player.getFamilyMembers().isEmpty());
    }

    @Test
    public void hasLeaderRequirements() throws Exception {
        setUp();
        LeaderCard leaderCard = deck.getFourLeaderCard().get(0);
        List<LeaderRequirements> leaderRequirementsChosen = new ArrayList<>(leaderCard.getLeaderRequirements());
        player.setPlayerResources(leaderRequirementsChosen.get(0).getResources());
        leaderRequirementsChosen.get(0).printRequirements();
        ArrayList<PurpleCard> purpleCards = new ArrayList<>(deck.getFourPurpleCards());
        purpleCards.addAll(new ArrayList<>(deck.getFourPurpleCards()));
        player.setPurpleCards(purpleCards);
        assertTrue(player.hasLeaderRequirements(leaderRequirementsChosen));
        player.setPurpleCards(new ArrayList<>());
        assertFalse(player.hasLeaderRequirements(leaderRequirementsChosen));
    }

    @Test
    public void addResources3() throws Exception {
        setUp();
        player.setPlayerResources(new Resources());
        ArrayList<ExcommunicationCard> excommunicationCards = new ArrayList<>(deck.getExcommunicationCards1());
        assertFalse(player.getAddResourcesState() instanceof ExcommunicationAddResourcesState);
        ExcommunicationCard excommunicationCard = excommunicationCards.get(0);
        ExcommunicationAddResourcesState excommunicationAddResourcesState = (ExcommunicationAddResourcesState)excommunicationCard.getState();
        System.out.println(excommunicationAddResourcesState.getResourcesSubtractedForEachResource());
        assertTrue(excommunicationAddResourcesState.getResourcesToSubtract().get(0).isMilitaryPoints());
        excommunicationCard.activate(player);
        assertTrue(player.getAddResourcesState() instanceof ExcommunicationAddResourcesState);
        Resources resources = new Resources(aResource);
        resources.subtractResources(new Resources(0,0,0,0,0,0,1,0));
        player.addResources(aResource);
        assertEquals(player.getPlayerResources(), resources);
    }

    @Test
    public void pickCard3() throws Exception {
        setUp();
        GreenCard monastry = null;
        for(GreenCard greenCard: deck.getGreenCards()){
            if(greenCard.getId().equals("6"))
                monastry = greenCard;
        }
        assertFalse(monastry.equals(null));
        assertTrue(player.getGreenCards().isEmpty());
        TowerPosition towerPosition = new TowerPosition(4, aResource);
        towerPosition.setCard(monastry);
        player.pickCard(aFamilyMember, towerPosition);
        assertFalse(player.getGreenCards().isEmpty());
    }


    //testcase for "Francesco Sforza".
    @Test
    public void perTurnLeaderCards() throws Exception {
        setUp();
        player.setPlayerResources(new Resources());
        ArrayList<LeaderCardPerTurn> leaderCardPerTurnArrayList = new ArrayList<>();
        for(LeaderCard leader : deck.getLeaderCards()){
            if(leader instanceof LeaderCardPerTurn)
                leaderCardPerTurnArrayList.add((LeaderCardPerTurn) leader);
        }
        LeaderCardPerTurn leaderCardPerTurn = leaderCardPerTurnArrayList.get(0);
        player.setBonusTile(new BonusTile(null, new Resources(), aResource, 1, 0));
        leaderCardPerTurn.activate(player);
        assertEquals(player.getPlayerResources(),aResource);
    }

    // testcase for "Giovanni dalle Bande Nere"
    @Test
    public void perTurnLeaderCards2() throws Exception {
        setUp();
        player.setPlayerResources(new Resources());
        ArrayList<LeaderCardPerTurn> leaderCardPerTurnArrayList = new ArrayList<>();
        LeaderCardPerTurn leaderCardPerTurn = null;
        for(LeaderCard leader : deck.getLeaderCards()){
            if(leader.getId().equals("06"))
                leaderCardPerTurn = (LeaderCardPerTurn) leader;
        }
        player.setBonusTile(new BonusTile(null, new Resources(), aResource, 1, 0));
        leaderCardPerTurn.activate(player);
        assertEquals(player.getPlayerResources(),new Resources(1,1,0,1,0,0,0,0));
    }



}