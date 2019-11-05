package model.players;


import controller.GameConfiguration;
import controller.Master;
import controller.states.*;
import controller.states.ExcommunicationStates.ExcommunicationVictoryPointsToSubtractState;
import controller.states.ExcommunicationStates.ExcommunicationVictoryPointsToSubtractStateCards;
import controller.states.ExcommunicationStates.ExcommunicationVictoryPointsToSubtractStateDefault;
import controller.exceptions.*;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.*;
import model.gameboard.*;
import network.server.remotePlayer.RemotePlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * All the attributes that represent the gamePlayer and his status
 */
public class Player implements Serializable{
    private static final int NEUTRAL_DICE_VALUE = 0;
    private Position temporaryChosenPosition = null;
    private String username;
    private List<ExcommunicationCard> excommunicationCards=new ArrayList<>();
    private List<GreenCard> greenCards;
    private List<YellowCard> yellowCards;
    private List<BlueCard> blueCards;
    private List<PurpleCard> purpleCards;
    private List<FamilyMember> familyMembers;
    private List<LeaderCard> leaderCards;
    private Resources playerResources;
    private Resources temporaryResources = new Resources();
    private FamilyMember temporaryFamilyMember = null;
    private PlayerColor playerColor;
    private BonusTile bonusTile;
    private boolean turnExcommunicationState= false;
    private FamilyMemberState familyMemberState;
    private HarvestState harvestState;
    private ProductionState productionState;
    private PickCardState pickCardState;
    private MarketState marketState;
    private NeutralMemberState neutralMemberState;
    private AddResourcesState addResourcesState;
    private GetTowerRewardState getTowerRewardState;
    private TowerOccupiedResourcesState towerOccupiedResourcesState;
    private AddServantsState addServantsState;
    private HasMilitaryPointsForGreenCardsState hasMilitaryPointsForGreenCardsState;
    private IsPositionOccupiedState isPositionOccupiedState;
    private ReceiveInstantRewardState receiveInstantRewardState;
    private ExcommunicationVictoryPointsToSubtractState excommunicationVictoryPointsToSubtractState;
    private ShowSupportBonusState showSupportBonusState;
    private GameConfiguration config = GameConfiguration.getInstance();
    private Master master;
    private RemotePlayer remotePlayer;

    public Player(Master master,PlayerColor playerColor, RemotePlayer remotePlayer){
        this.playerColor = playerColor;
        this.greenCards = new ArrayList<>();
        this.yellowCards = new ArrayList<>();
        this.purpleCards = new ArrayList<>();
        this.blueCards = new ArrayList<>();
        this.leaderCards = new ArrayList<>();
        this.familyMembers = new ArrayList<>();
        this.remotePlayer = remotePlayer;
        this.familyMemberState = new DefaultFamilyMemberState(this);
        this.productionState = new DefaultProductionState(this);
        this.harvestState = new DefaultHarvestState(this);
        this.pickCardState = new DefaultPickCardState(this);
        this.marketState = new DefaultMarketState(this);
        this.neutralMemberState = new DefaultNeutralMemberState(this);
        this.addResourcesState = new DefaultAddResourcesState(this);
        this.getTowerRewardState = new DefaultGetTowerRewardState(this);
        this.towerOccupiedResourcesState = new TowerOccupiedResourcesDefaultState(this);
        this.addServantsState = new DefaultAddServantsState(this);
        this.hasMilitaryPointsForGreenCardsState = new DefaultHasMilitaryPointsForGreenCards(this);
        this.receiveInstantRewardState = new DefaultReceiveInstantReward(this);
        this.isPositionOccupiedState = new IsPositionOccupiedDefaultState();
        this.excommunicationVictoryPointsToSubtractState = new ExcommunicationVictoryPointsToSubtractStateDefault();
        this.showSupportBonusState = new DefaultShowSupportBonusState();
        this.playerResources = new Resources();
        this.username = remotePlayer.getUsername();
        this.master = master;
    }



    public Player(PlayerColor playerColor) {
        this.playerColor = playerColor;
        this.greenCards = new ArrayList<>();
        this.yellowCards = new ArrayList<>();
        this.purpleCards = new ArrayList<>();
        this.blueCards = new ArrayList<>();
        this.leaderCards = new ArrayList<>();
        this.familyMembers = new ArrayList<>();
        this.familyMemberState = new DefaultFamilyMemberState(this);
        this.productionState = new DefaultProductionState(this);
        this.harvestState = new DefaultHarvestState(this);
        this.pickCardState = new DefaultPickCardState(this);
        this.marketState = new DefaultMarketState(this);
        this.neutralMemberState = new DefaultNeutralMemberState(this);
        this.addResourcesState = new DefaultAddResourcesState(this);
        this.getTowerRewardState = new DefaultGetTowerRewardState(this);
        this.showSupportBonusState = new DefaultShowSupportBonusState();
        this.towerOccupiedResourcesState = new TowerOccupiedResourcesDefaultState(this);
        this.addServantsState = new DefaultAddServantsState(this);
        this.hasMilitaryPointsForGreenCardsState = new DefaultHasMilitaryPointsForGreenCards(this);
        this.receiveInstantRewardState = new DefaultReceiveInstantReward(this);
        this.isPositionOccupiedState = new IsPositionOccupiedDefaultState();
        this.excommunicationVictoryPointsToSubtractState = new ExcommunicationVictoryPointsToSubtractStateDefault();
        this.playerResources = new Resources();
    }


    /**
     * this method is used form addResource method to add Resources to Resource object of the player
     * @param resources are the resources you want to add to player resources
     */
    public void addResourcesToPlayerResources(Resources resources){
        playerResources.addResources(resources);
    }

    /**
     * this method is used to check conditions on Player's State and add resources to player
     * the difference respect the above method is that addReources takes account of the condition of the
     * Player's state.
     * @param resources are the resources you want to add to player resources
     */
    public void addResources(Resources resources){
        addResourcesState.addResources(resources);
    }

    public void subtractResources(Resources resources) throws NotEnoughResourcesException {
        Resources tryResources =new Resources(playerResources);
        tryResources.subtractResources(resources);
        if(tryResources.isPositive()) {
            playerResources.subtractResources(resources);
        }else throw new NotEnoughResourcesException();
    }

    /**
     * this method adds a leaderCard to player cards
     * @param leaderCard the Leader Card you want to add to player
     */
    public void addLeaderCard(LeaderCard leaderCard){
        this.leaderCards.add(leaderCard);
    }

    /**
     * returns the Number of cards that player owns for that specified color
     * @param color the color chosen
     * @return number of cards
     */
    public int getNumberOfCardsPerColor(CardColor color){
        if(color == CardColor.GREEN){
            return greenCards.size();
        }
        if(color == CardColor.BLUE){
            return blueCards.size();
        }
        if(color == CardColor.PURPLE){
            return purpleCards.size();
        }
        if(color == CardColor.YELLOW){
            return yellowCards.size();
        }
        return 0;
    }

    /**
     * this method return all the leader cards that are not been activated yet
     * @return leader cards that are not been activated yet.
     */
    public List<LeaderCard> getNonActivatedLeaderCards(){
        List<LeaderCard> listToReturn = new ArrayList<>();
        for(LeaderCard leaderCard : leaderCards)
            if(!leaderCard.isActivated())
                listToReturn.add(leaderCard);
        return listToReturn;
    }

    /**
     * this method adds a familyMember to player's familyMembers
     * @param familyMember is the familyMember you want to add
     */
    public void addFamilyMember(FamilyMember familyMember){
        familyMembers.add(familyMember);
    }

    /**
     * this method is used from master to perform a market action
     * market action can also have
     * @param familyMember is the Family Member with whom you intend start the market
     * @param marketPosition is the market position in which you want to get Resources
     * @return Resources you should get from that market position
     * @throws NotEnoughValueException if the family Member chosen has a lower value than needed
     */
    public Resources goToMarket(FamilyMember familyMember, MarketPosition marketPosition) throws NotEnoughValueException {
        return marketState.goToMarket(familyMember, marketPosition);
    }

    /**
     * is used for Start the production it takes account of the player's production state that is used to
     * consider all possible bonuses and excommunications
     * @param familyMember the family member you want to start Production with
     * @param position position used for starting production.
     * @throws NotEnoughValueException if the family member has not the right value
     * @throws NotEnoughResourcesException if player doesn't have resources to produce a particular card.
     */
    public void startProduction(FamilyMember familyMember, ProductionPosition position) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException {
        productionState.startProduction(familyMember,position);
    }
    public void startProduction(int value) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException {
        productionState.startProduction(null,null,value);
    }

    /**
     * uses startHarvest state of each player to perform the action of starting harvest.
     * @param familyMember is the familyMember you want to start production with
     * @param harvestPosition is the position in which you want to start harvest
     * @throws NotEnoughValueException if there is not an Harvest position free.
     */
    public void startHarvest(FamilyMember familyMember, HarvestPosition harvestPosition)throws  NotEnoughValueException {
        harvestState.startHarvest(familyMember,harvestPosition);
    }
    public void startHarvest(int istantDiceValue) throws NotEnoughValueException {
        harvestState.startHarvest(null,null,istantDiceValue);
    }

    /**
     * is used for pick a card from a Tower it considers pickCardState for taking account of possible bonuses or Excommunications.
     * @param familyMember the family member player wants to start production with
     * @param towerPosition the position of the tower in which you want to pick the card
     * @throws NotEnoughValueException if the family member has not the required value
     * @throws NotEnoughResourcesException if you have not enough resources to pick the chosen card
     * @throws TimeOutException if the timeout expires
     * @throws PositionIsEmptyException if that position has not a a card
     * @throws NotEmptyPositionException if that position is already occupied by a familyMember.
     */
    public void pickCard(FamilyMember familyMember, TowerPosition towerPosition) throws NotEnoughValueException, NotEnoughResourcesException, TimeOutException, PositionIsEmptyException, NotEmptyPositionException {
        pickCardState.pickCard(familyMember,towerPosition, 0, new Resources());
    }
    public void pickCard(TowerPosition position, int value, Resources discount) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, PositionIsEmptyException, NotEmptyPositionException {
        pickCardState.pickCard(null,position,value,discount);
    }

    /**
     * is used to decide the resources that player has to pay if the tower is already occupied
     * @param position that player wants to occupy
     * @return Resources player has to pay.
     */
    public Resources towerOccupiedResources(TowerPosition position){
        return towerOccupiedResourcesState.towerOccupiedResources(position);
    }
    public void getTowerReward(Resources resources){
        getTowerRewardState.getTowerReward(resources);
    }

    /**
     * is used to set the value of Neutral family member, it considers neutralMemberState to
     * take account of possible bonuses or excommunications
     */
    public void setNeutralMember(){
        neutralMemberState.setNeutralMember();
    }

    /**
     * is used to add Value to a particular family member
     * @param familyMember th Family Member you want to add servants to
     * @param resources contains the servants you want to add to your family Member.
     */
    public void addServants(FamilyMember familyMember,Resources resources){
        addServantsState.addServants(familyMember,resources);
    }

    public void addBlueCard(BlueCard card){
        blueCards.add(card);
    }
    public void addGreenCard(GreenCard card){
        greenCards.add(card);
    }
    public void addPurpleCard(PurpleCard card){
        purpleCards.add(card);
    }
    public void addYellowCard(YellowCard card){
        yellowCards.add(card);
    }
    public String getUsername(){
        return username;
    }

    public BonusTile getBonusTile() {
        return bonusTile;
    }
    public void setBonusTile(BonusTile bonusTile){
        this.bonusTile = bonusTile;
    }

    public Master getMaster() {
        return master;
    }

    /**
     * this method is used to check if the player has particular Resources
     * @param resourcesNeeded the resources that you want to check
     * @return true if player has the required Resources.
     */
    public boolean hasResources(Resources resourcesNeeded) {
       Resources tryResources = new Resources(playerResources);
       tryResources.subtractResources(resourcesNeeded);
        return tryResources.isPositive();
    }

    /**
     * @return the Turn Excommunication State of the player, used in case of Excommunication card that affects
     * the player's turn order.
     */
    public boolean isTurnExcommunicationState() {
        return turnExcommunicationState;
    }

    /**
     * is used for check if the has the condition to pick the card on the specified position
     * @param position is the position from which player wants to pick the card
     * @return true if player can pick card.
     */
    public boolean canPickCard(TowerPosition position) {
        CardColor cardColor = position.getCard().getCardColor();
        if(getNumberOfCardsPerColor(cardColor)<= config.getMaxNumberOfDevelopmentCards()) {
            return !CardColor.GREEN.equals(cardColor) || hasMilitaryPointsForGreenCards();
        }  else return false;
    }

    /**
     * is used to check if the player has Military Points needed to get a
     * greenCard, it uses hasMilitaryPointsForGreenCardsState for taking account of bonuses and
     * excommunications
     * @return true if the player has the military points needed.
     */
    private boolean hasMilitaryPointsForGreenCards() {
        return hasMilitaryPointsForGreenCardsState.hasMilitaryPointsForGreenCards();
    }

    /**
     * is used to check if a particular position is occupied, it uses isPositionOccupiedState
     * to take account of possible bonuses and excommunications.
     * @param position position you want to check
     * @return true if the position is occupied by another family Member
     */
    public boolean isPositionOccupied(Position position) {
       return isPositionOccupiedState.isPositionOccupied(position);
    }

    /**
     * is used to attribute the player of Resources got with instant reward,
     * it takes account of possible bonuses and excommunication using receiveInstantRewardState
     * @param resources Resources got from instant reward.
     */
    public void receiveInstantReward(Resources resources) {
        this.receiveInstantRewardState.receiveInstantReward(resources);
    }

    /**
     * is used to activate Reward provided by Characters Cards (BlueCard)
     * @param blueReward the reward player is getting
     */
    public void activateBlueCard(BlueReward blueReward){
        if(blueReward.getPickBonus()!=null){
            PickBonus pickBonus = blueReward.getPickBonus();
            PickCardState newState;
            if(pickBonus.getDiscount()!=null){
                 newState = new BlueCardPickCardState(pickCardState,pickBonus.getCardColor(),pickBonus.getValue(),pickBonus.getDiscount().get(0));
            }else {
                newState = new BlueCardPickCardState(pickCardState,pickBonus.getCardColor(),pickBonus.getValue());
            }
            this.pickCardState = newState;

        }

        if(blueReward.getHarvestBonus()!=null){
            HarvestBonus harvestBonus = blueReward.getHarvestBonus();
            this.harvestState = new BlueCardHarvestState(harvestState,harvestBonus.getValue());
        }

        if(blueReward.getProductionBonus()!=null){
            ProductionBonus productionBonus = blueReward.getProductionBonus();
            this.productionState = new BlueCardProductionState(productionState,productionBonus.getValue());
        }

        if(blueReward.isPreacher()){
            this.getTowerRewardState = new PreacherGetTowerRewardState(getTowerRewardState);
        }

    }

    public HarvestState getHarvestState() {
        return harvestState;
    }

    public ProductionState getProductionState() {
        return productionState;
    }

    public PickCardState getPickCardState() {
        return pickCardState;
    }

    public MarketState getMarketState() {
        return marketState;
    }

    public NeutralMemberState getNeutralMemberState() {
        return neutralMemberState;
    }

    public AddResourcesState getAddResourcesState() {
        return addResourcesState;
    }

    public GetTowerRewardState getGetTowerRewardState() {
        return getTowerRewardState;
    }

    public TowerOccupiedResourcesState getTowerOccupiedResourcesState() {
        return towerOccupiedResourcesState;
    }

    public AddServantsState getAddServantsState() {
        return addServantsState;
    }

    public HasMilitaryPointsForGreenCardsState getHasMilitaryPointsForGreenCardsState() {
        return hasMilitaryPointsForGreenCardsState;
    }

    public IsPositionOccupiedState getIsPositionOccupiedState() {
        return isPositionOccupiedState;
    }

    public List<GreenCard> getGreenCards() {
        return greenCards;
    }
    public List<YellowCard> getYellowCards() {
        return yellowCards;
    }
    public List<BlueCard> getBlueCards() {
        return blueCards;
    }
    public List<PurpleCard> getPurpleCards() {
        return purpleCards;
    }
    public Resources getPlayerResources() {
        return playerResources;
    }
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public RemotePlayer getRemotePlayer() {
        return remotePlayer;
    }

    public List<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void setRemotePlayer(RemotePlayer remotePlayer) {
        this.remotePlayer = remotePlayer;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGreenCards(List<GreenCard> greenCards) {
        this.greenCards = greenCards;
    }

    public void setYellowCards(List<YellowCard> yellowCards) {
        this.yellowCards = yellowCards;
    }

    public void setBlueCards(List<BlueCard> blueCards) {
        this.blueCards = blueCards;
    }

    public void setPurpleCards(List<PurpleCard> purpleCards) {
        this.purpleCards = purpleCards;
    }

    public void setFamilyMembers(List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public void setPlayerResources(Resources playerResources) {
        this.playerResources = playerResources;
    }

    public void setTurnExcommunicationState(boolean turnExcommunicationState) {
        this.turnExcommunicationState = turnExcommunicationState;
    }

    public void setFamilyMemberState(FamilyMemberState familyMemberState) {
        this.familyMemberState = familyMemberState;
    }

    public void setHarvestState(HarvestState harvestState) {
        this.harvestState = harvestState;
    }

    public void setProductionState(ProductionState productionState) {
        this.productionState = productionState;
    }

    public void setPickCardState(PickCardState pickCardState) {
        this.pickCardState = pickCardState;
    }

    public void setMarketState(MarketState marketState) {
        this.marketState = marketState;
    }

    public void setNeutralMemberState(NeutralMemberState neutralMemberState) {
        this.neutralMemberState = neutralMemberState;
    }

    public void setAddResourcesState(AddResourcesState addResourcesState) {
        this.addResourcesState = addResourcesState;
    }

    public void setGetTowerRewardState(GetTowerRewardState getTowerRewardState) {
        this.getTowerRewardState = getTowerRewardState;
    }

    public void setTowerOccupiedResourcesState(TowerOccupiedResourcesState towerOccupiedResourcesState) {
        this.towerOccupiedResourcesState = towerOccupiedResourcesState;
    }

    public void setAddServantsState(AddServantsState addServantsState) {
        this.addServantsState = addServantsState;
    }

    public void setHasMilitaryPointsForGreenCardsState(HasMilitaryPointsForGreenCardsState hasMilitaryPointsForGreenCardsState) {
        this.hasMilitaryPointsForGreenCardsState = hasMilitaryPointsForGreenCardsState;
    }

    public void setIsPositionOccupiedState(IsPositionOccupiedState isPositionOccupiedState) {
        this.isPositionOccupiedState = isPositionOccupiedState;
    }

    public ReceiveInstantRewardState getReceiveInstantRewardState() {
        return receiveInstantRewardState;
    }

    public void setReceiveInstantRewardState(ReceiveInstantRewardState receiveInstantRewardState) {
        this.receiveInstantRewardState = receiveInstantRewardState;
    }


    /**
     * is used to activate Leader Cards that have permanent effect
     * @param permanentLeaderCard is the Leader Card player want to activate
     */
    public void activatePermanentLeaderCard(LeaderCardPermanent permanentLeaderCard) {
        for(LeaderCard card:leaderCards){
            if(card.getName().equals(permanentLeaderCard.getName())){
                card.setActivated(true);
            }
        }
        LeaderCardHashMap.leaderCardHashMap.get(permanentLeaderCard.getName()).overriddenFunction(this);
    }

    /**
     * is used to activate Leader cards that has once per turn effects
     * @param perTurnLeaderCard is the Card player wants to activate
     * @throws TimeOutException if timeout is expired
     * @throws NotEnoughValueException if the choosen family member has not the required value
     */
    public void activePerTurnLeaderCard(LeaderCardPerTurn perTurnLeaderCard) throws TimeOutException,NotEnoughValueException {
               perTurnLeaderCard.receiveLeaderReward(this);
    }

    /**
     * is used to check if player has all Requirements defined by a Leader Card
     * @param leaderRequirements requirements of a particular Leader Card, it could be more than one.
     * @return true if the players has the required attributes.
     */
    public boolean hasLeaderRequirements(List<LeaderRequirements> leaderRequirements) {
        for(LeaderRequirements requirements:leaderRequirements){
            if(requirements.getResources()!=null&&!hasResources(requirements.getResources())){
                return false;
            }else if(requirements.getYellowCardNeeded()>0&&requirements.getYellowCardNeeded()>this.getNumberOfCardsPerColor(CardColor.YELLOW)){
                return false;
            }else if(requirements.getBlueCardNeeded()>0&&requirements.getBlueCardNeeded()>this.getNumberOfCardsPerColor(CardColor.BLUE)){
                return false;
            }else if(requirements.getPurpleCardNeeded()>0&&requirements.getPurpleCardNeeded()>this.getNumberOfCardsPerColor(CardColor.PURPLE)){
                return false;
            }else if(requirements.getGreenCardNeeded()>0&&requirements.getGreenCardNeeded()>this.getNumberOfCardsPerColor(CardColor.GREEN)){
                return false;
            }
        }
        return true;

    }

    public void setExcommunicationVictoryPointsToSubtractState(ExcommunicationVictoryPointsToSubtractState excommunicationVictoryPointsToSubtractState) {
        this.excommunicationVictoryPointsToSubtractState = excommunicationVictoryPointsToSubtractState;
    }

    public Resources getTemporaryResources() {
        return temporaryResources;
    }

    public void setTemporaryResources(Resources temporaryResources) {
        this.temporaryResources = temporaryResources;
    }

    public ShowSupportBonusState getShowSupportBonusState() {
        return showSupportBonusState;
    }

    public void setShowSupportBonusState(ShowSupportBonusState showSupportBonusState) {
        this.showSupportBonusState = showSupportBonusState;
    }

    public void setTemporaryFamilyMember(FamilyMember temporaryFamilyMember) {
        this.temporaryFamilyMember = temporaryFamilyMember;
    }

    /**
     * is used to remove the temporary family Member.
     * the temporary Family Member for keeping trace of possible actions
     */
    public void removeFamilyMember() {
        if(temporaryFamilyMember!=null){
            familyMembers.removeIf(familyMember -> familyMember.getDiceColor().toString().equals(temporaryFamilyMember.getDiceColor().toString()));
        }
    }

    /**
     * is used to remove a particular Leader Card from Player's card
     * @param leaderCard the Leader Card you are going to remove.
     */
    public void removeLeaderCard(LeaderCard leaderCard){
        if(leaderCard!=null)
        leaderCards.removeIf( leaderCard1 -> leaderCard.getName().equals(leaderCard1.getName()));
    }
    public void removeLeaderCard(String name){
        LeaderCard cardToRemove = null;
        for(LeaderCard card:leaderCards){
            if(card.getName().equals(name))
                cardToRemove = card;
        }
        leaderCards.remove(cardToRemove);
    }

    public int getShowSupportBonus(){
        return showSupportBonusState.getSupportBonus();
    }

    /**
     * is used to activate the Leader Card "Lorenzo De Medici".
     */
    public void activateLorenzoDeMedici() {
        master.activateLorenzoDeMedici(this);
        removeLeaderCard("Lorenzo De Medici");
    }


    public FamilyMemberState getFamilyMemberState() {
        return familyMemberState;
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * is used to set a particular value to a Family Member
     * @param familyMember the family member you want to add value to
     * @param value the value you want to add to family member
     */
    public void setValueToAFamilyMember(FamilyMember familyMember, int value) {
        familyMembers.remove(familyMember);
        familyMember.setValue(value);
        familyMembers.add(familyMember);
    }

    /**
     * is used to deactivate all Leader Card that has once per turn effects
     */
    public void deactivateLeaderCards(){
        for (LeaderCard card:leaderCards){
            card.deactivate();
        }
    }

    public void addExcommunication(ExcommunicationCard excommunicationCard) {
        excommunicationCards.add(excommunicationCard);
    }

    /**
     * is used at the end of the game in case the player has an excommunication that subtracts victory points
     */
    public void subtractExcommunicationVictoryPoints() {
        Resources resourcesToSubtract = excommunicationVictoryPointsToSubtractState.victoryPointsToSubtract(this);
        try {
            subtractResources(resourcesToSubtract);
        }catch (NotEnoughResourcesException e){
            Resources tryResources;
            tryResources = new Resources(playerResources);
            playerResources = new Resources(tryResources.getWood(),tryResources.getCoins(),tryResources.getServants(),tryResources.getStone(),0,tryResources.getFaithPoints(),tryResources.getMilitaryPoints(),tryResources.getCouncilPrivilege());
        }
    }

    public List<ExcommunicationCard> getExcommunicationCards() {
        return excommunicationCards;
    }

    /**
     * is used at the end of the game to calculate the Victory Points that player has got from
     * his cards
     * @param config game configuration passed by the master , is used to get the victory points related to
     *               number of cards of specific color
     * @return number of victory points that player got from cards
     */
    public int getFinalVictoryPointsForCards(GameConfiguration config) {
        int victoryPointsToAdd =0;
        CardColor cardColorToAvoid=null;
        if(excommunicationVictoryPointsToSubtractState instanceof ExcommunicationVictoryPointsToSubtractStateCards){
           cardColorToAvoid= ((ExcommunicationVictoryPointsToSubtractStateCards) excommunicationVictoryPointsToSubtractState).getCardColor();
        }
        if(cardColorToAvoid!=CardColor.GREEN) {
            int numberOfGreenCards = getNumberOfCardsPerColor(CardColor.GREEN);
            victoryPointsToAdd += config.getVictoryPointsGotForGreenCard()[numberOfGreenCards];
        }

        if(cardColorToAvoid!=CardColor.BLUE) {
            int numberOfBlueCards = getNumberOfCardsPerColor(CardColor.BLUE);
            victoryPointsToAdd += config.getVictoryPointsGotForBlueCard()[numberOfBlueCards];
        }
        if(cardColorToAvoid!=CardColor.PURPLE) {
            for (PurpleCard card : getPurpleCards()) {
                victoryPointsToAdd += card.getPurpleReward().getVictoryPoints();
            }
        }
        return victoryPointsToAdd;
    }
}

