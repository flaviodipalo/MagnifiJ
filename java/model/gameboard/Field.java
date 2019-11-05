package model.gameboard;

import controller.Deck;
import controller.GameConfiguration;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.dices.Dice;
import model.dices.DiceColor;
import model.players.FamilyMember;
import model.players.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static controller.Actions.MARKET_POSITIONS;
import static controller.Actions.TOWER_POSITIONS;


/**
 * The field class is the main part of our model
 * it handles various components necessary to game logic to work.
 */
public class Field implements Serializable {
    static final long serialVersionUID = 44L;
    private List<Player> playersOrder;
    private List<LeaderCard> activatedLeaderCard;
    private Period period;
    private Dice[] dices;
    private ExcommunicationCard[] excommunicationCards;
    private Tower greenTower;
    private Tower yellowTower;
    private Tower purpleTower;
    private Tower blueTower;
    private CouncilArea council;
    private MarketArea market;
    private HarvestArea harvest;
    private ProductionArea production;
    private int numPlayers;
    private Deck deck;
    private transient GameConfiguration config = GameConfiguration.getInstance();

    /**
     *
     * @param numPlayers number of players playing
     * @param deck deck that this field will use
     * @param playersOrder order of players in the game
     */
    public Field(int numPlayers, Deck deck, List<Player> playersOrder){
        this.playersOrder = new ArrayList<>(playersOrder);
        this.numPlayers = numPlayers;
        this.deck = deck;
        this.activatedLeaderCard = new ArrayList<>();
        init();
    }

    /**
     * this constructor is used for testing purposes only
     * @param numPlayers is the number of model.players playing the game
     */
    public Field(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * is used to initialize the field the first time
     */
    public void init(){
        initTowers();
        initCouncil();
        initMarket();
        initHarvest();
        initProduction();
        initDices();
        initExcommunication();
    }

    /**
     * is used to remove Family Members from field position when needed
     */
    public void refreshField(){
        refreshHarvest();
        refreshCouncil();
        refreshMarket();
        refreshProduction();
        refreshTowers();
    }

    /**
     * removes FamilyMembers from production
     */
    private void refreshProduction(){
        for (ProductionPosition position : getProduction().getProduction()){
            position.setFamilyMember(null);
        }
    }

    /**
     * removes FamilyMembers from council
     */
    private void refreshCouncil(){
        for (CouncilPosition position : getCouncil().getCouncilPositions()){
            position.setFamilyMember(null);
        }
    }

    /**
     * removes FamilyMembers from harvest
     */
    private void refreshHarvest(){
        for (HarvestPosition position : getHarvest().getHarvest()){
            position.setFamilyMember(null);
        }
    }

    /**
     * removes FamilyMembers from Market
     */
    private void refreshMarket(){
        for (MarketPosition position : getMarket().getMarket()){
            position.setFamilyMember(null);
        }
    }

    /**
     * removes FamilyMembers from Towers
     */
    private void refreshTowers(){
        for(TowerPosition position : getAllTowersPosition()){
            position.setFamilyMember(null);
            position.setCard(null);
        }
    }

    /**
     * is used to create towers
     */
    private void initTowers(){
        greenTower = new Tower(CardColor.GREEN);
        yellowTower = new Tower(CardColor.YELLOW);
        purpleTower = new Tower(CardColor.PURPLE);
        blueTower = new Tower(CardColor.BLUE);
    }

    /**
     * initializes the council position
     */
    private void initCouncil(){
        council = new CouncilArea();
    }

    /**
     * is used to create the Market Area of the field.
     */
    private void initMarket(){
        market = new MarketArea(numPlayers);
    }

    /**
     * this
     */
    private void initHarvest(){
        harvest = new HarvestArea(numPlayers);
    }
    private void initProduction(){
        production = new ProductionArea(numPlayers);
    }

    private void initDices(){
        dices = new Dice[config.getNumberOfDices()];
        dices[0] = new Dice(DiceColor.BLACK);
        dices[1] = new Dice(DiceColor.ORANGE);
        dices[2] = new Dice(DiceColor.WHITE);
    }

    /**
     * initialize an Array of Excommunication card that has size equal to number of game eras
     */
    private void initExcommunication(){
        excommunicationCards = new ExcommunicationCard[config.getNumberOfTurns()/2];
    }

    /**
     * gives new values to each dice
     */
    public void throwDices(){
        for (Dice dice : dices) {
            dice.throwDice();
        }
    }

    /**
     * @return a single list of all the towers available
     */
    public List<Tower> getAllTowers(){
        ArrayList<Tower> towers = new ArrayList<>();
        towers.add(getGreenTower());
        towers.add(getBlueTower());
        towers.add(getYellowTower());
        towers.add(getPurpleTower());
        return towers;
    }

    /**
     * @return a single list of all the tower positions avaliable
     */
    private List<TowerPosition> getAllTowersPosition(){
        List<TowerPosition> allTowers = new ArrayList<>();
        allTowers.addAll(greenTower.getTowerPositions());
        allTowers.addAll(blueTower.getTowerPositions());
        allTowers.addAll(yellowTower.getTowerPositions());
        allTowers.addAll(purpleTower.getTowerPositions());
        return allTowers;
    }

    /**
     * is used by the Master to choose what position player should occupy.
     * @return a map that associates a String to each position of the Towers
     */
    public Map<String, TowerPosition> generateTowerMap(){
        HashMap<String, TowerPosition> map = new HashMap<>();
        ArrayList<TowerPosition> tower = new ArrayList<>(getAllTowersPosition());
        for (int i = 0; i< TOWER_POSITIONS.length; i++){
            map.put(TOWER_POSITIONS[i],tower.get(i));
        }
    return map;
    }

    /**
     * is used to get only towers that
     * @param cardColor the color associated to the tower you are getting
     * @return a map that associates a String to each position of the Towers
     */
    public Map<String, TowerPosition> generateTowerMap(CardColor cardColor){
        HashMap<String, TowerPosition> map = new HashMap<>();
        List<TowerPosition> tower = getAllTowersPosition();

        int i=0;
        int j=0;
        if(cardColor == CardColor.ALL){
            i=0;
            j=16;
        }
        if(cardColor == CardColor.GREEN){
            i=0;
            j=4;
        }
        if(cardColor == CardColor.BLUE){
            i=4;
            j=8;
        }
        if(cardColor == CardColor.YELLOW){
            i=8;
            j=12;
        }
        if(cardColor == CardColor.PURPLE){
            i=12;
            j=16;
        }
        for (; i< j; i++){
            map.put(TOWER_POSITIONS[i],tower.get(i));
        }
        return map;
    }

    /**
     * is used to generate a Map of Strings and Market position
     * @return Map of Strings and MarketPosition.
     */
    public Map<String,MarketPosition> generateMarketMap(){
        HashMap<String,MarketPosition> map = new HashMap<>();
        MarketPosition[] marketArea;
        marketArea = market.getMarket();
        for (int i = 0; i< marketArea.length; i++){
            map.put(MARKET_POSITIONS[i], marketArea[i]);
        }
        return map;
    }

    /**
     * is used to place cards from the deck to the tower
     */
    public void placeCards(){
            for (GreenCard card : deck.getFourGreenCards()) {
                greenTower.setCard(card);
            }

            for (PurpleCard card : deck.getFourPurpleCards()) {
                purpleTower.setCard(card);
            }
            for (BlueCard card : deck.getFourBlueCards()) {
                blueTower.setCard(card);
            }
            for (YellowCard card : deck.getFourYellowCards()) {
                yellowTower.setCard(card);
            }
        }


    public ProductionArea getProduction() {
        return production;
    }

    public HarvestArea getHarvest() {
        return harvest;
    }

    public CouncilArea getCouncil() {
        return council;
    }

    public Deck getDeck() {
        return deck;
    }

    public MarketArea getMarket(){
        return market;
    }

    public Tower getGreenTower() {
        return greenTower;
    }

    public Tower getYellowTower() {
        return yellowTower;
    }

    public Tower getPurpleTower() {
        return purpleTower;
    }

    public Tower getBlueTower() {
        return blueTower;
    }

    public void setCouncil(CouncilArea council) {
        this.council = council;
    }

    public void setMarket(MarketArea market) {
        this.market = market;
    }

    public void setHarvest(HarvestArea harvest) {
        this.harvest = harvest;
    }

    public void setProduction(ProductionArea production) {
        this.production = production;
    }

    public void setTowers(List<Tower> towers){
        this.greenTower = towers.get(0);
        this.blueTower = towers.get(1);
        this.yellowTower = towers.get(2);
        this.purpleTower = towers.get(3);
    }

    public void setPlayersOrder(List<Player> playersOrder) {
        this.playersOrder = playersOrder;
    }

    /**
     * take a position ad return if the tower containing that position is occupied by at least one family member not.
     * @param position position of the tower
     * @return if the position is occupied or not
     */
    public boolean isTowerOccupied(TowerPosition position) {
        Tower testTower = null;
        if (greenTower.getTowerPositions().contains(position)){
            testTower = greenTower;
        }
        else if (blueTower.getTowerPositions().contains(position)){
            testTower = blueTower;
        }
        else if (yellowTower.getTowerPositions().contains(position)){
            testTower = yellowTower;
        }
        else if (purpleTower.getTowerPositions().contains(position)){
            testTower = purpleTower;
        }
        for (TowerPosition position1:testTower.getTowerPositions()){
            if (!position1.isEmpty()){
                return true;
            }
        }
        return false;
    }
    public ExcommunicationCard getExcommunicationCard(int era) {
        return excommunicationCards[era-1];
    }

    /**
     * this method is used to place the excommunication card on the field spaces.
     */
    public void placeExcommunicationCards() {
        excommunicationCards = deck.getExcommunicationCards();
    }
    public List<LeaderCard> getActivatedLeaderCard() {
        return activatedLeaderCard;
    }

    public void addActivatedLeaderCard(LeaderCard leaderCardChosen) {
        activatedLeaderCard.add(leaderCardChosen);
    }

    /**
     * given a position this method return if the player has placed a family member in a space of the same kind
     * @param position the position in which you want to add your family member
     * @param familyMember the family member you want to add
     * @return true if there is another family member
     */
    public boolean isThereAnotherSameFamilyMemberOnSimilarPosition(Position position, FamilyMember familyMember) {
        if(familyMember.getDiceColor()==DiceColor.NEUTRAL)
            return false;
        else if(position instanceof ProductionPosition){
            return production.hasFamilyMemberOfTheSameColor(familyMember);
        }
        else if(position instanceof HarvestPosition){
            return harvest.hasFamilyMemberOfTheSameColor(familyMember);
        }else if(position instanceof TowerPosition){
            if(greenTower.getTowerPositions().contains(position)){
               return greenTower.hasFamilyMemberOfTheSameColor(familyMember);
            }
            else if(blueTower.getTowerPositions().contains(position)){
                return blueTower.hasFamilyMemberOfTheSameColor(familyMember);
            }
            else if(yellowTower.getTowerPositions().contains(position)){
                return yellowTower.hasFamilyMemberOfTheSameColor(familyMember);
            }
            else if(purpleTower.getTowerPositions().contains(position)){
                return purpleTower.hasFamilyMemberOfTheSameColor(familyMember);
            }
        }
        return false;
    }
    public List<Player> getPlayersOrder() {
        return playersOrder;
    }
    public void setPeriod(Period period){
        this.period = period;
    }
    public Dice[] getDices() {
        return dices;
    }
    public Period getPeriod(){
        return this.period;
    }

}
