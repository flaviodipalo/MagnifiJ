package controller;

import model.players.FamilyMember;
import model.players.Player;
import model.players.PlayerColor;
import controller.exceptions.*;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.dices.DiceColor;
import model.gameboard.*;
import network.protocol.ClientAction;
import network.server.Room;
import network.server.Server;
import network.server.database.Database;
import network.server.remotePlayer.RemotePlayer;
import view.cli.Display;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;


/**
 * The class master is responsible for the game. Is created by the room and receive from it
 * the players in the room itself. The master manage also the interaction between the players
 * and the field and set a player in an offline status if he disconnects
 */
public class Master implements Serializable{
    private static final long serialVersionUID = 43L;
    private boolean turnEnded = false;
    private Field field;
    private int turn;
    private Room room;
    private Player currentPlayer;
    private HashMap<String, ActionsHandler> actionsHandlerHashMap;
    private HashMap<String, Wrapper> positionsHandlerHashMap;
    private String[] actionList = {"Sell leader card", "Activate leader card", "Place family member", "Pass turn"};
    private LinkedList<Player> turnExcommunicatedPlayers;
    private int generalTimeOut;
    private int privateTimeOut;
    private transient Timer timer;
    private boolean currentPlayerFamilyMemberPlaced;
    private PersistenceHandler persistenceHandler;
    private boolean recoveryMode;
    private int currentTurn;
    private int currentPlayerTurn;
    private GameConfiguration config = GameConfiguration.getInstance();
    private transient Database dataBase;
    private ClientAction clientAction;

    /**
     *Initialize the master and his parameters
     * @param room the room which create the master
     */
    public Master(Room room){
        this.room = room;
        this.turn = 0;
        field = new Field(room.getNumberOfPlayers(),room.getDeck(), createPlayers());
        generalTimeOut = config.getGeneralTimeOut();
        dataBase = new Database();
    }

    /**
     * another constructor used for test cases only
     * @param numPlayers you want to construct master for.
     */
    public Master(int numPlayers){
        this.turn = 0;
        field = new Field(numPlayers);
        field.init();
    }

    /**
     *Initialize the position handler HashMap. This map is made by a String and a Wrapper
     */
    private void initializePositionHandlerMap() {
        Map<String, TowerPosition> towerPositionHashMap = field.generateTowerMap();
        Map<String, MarketPosition> marketPositionHashMap = field.generateMarketMap();
        positionsHandlerHashMap = new HashMap<>();
        for (Object o1 : towerPositionHashMap.entrySet()) {
            Map.Entry pairs = (Map.Entry) o1;
            Wrapper wrapper = new Wrapper((Position) pairs.getValue(), this::handleTowerPosition);
            String stringPosition = (String) pairs.getKey();
            positionsHandlerHashMap.put(stringPosition, wrapper);
        }
        for (Object o : marketPositionHashMap.entrySet()) {
            Map.Entry pairs = (Map.Entry) o;
            Wrapper wrapper = new Wrapper((Position) pairs.getValue(), this::handleMarketPosition);
            String stringPosition = (String) pairs.getKey();
            positionsHandlerHashMap.put(stringPosition, wrapper);
        }
        Wrapper councilWrapper = new Wrapper(field.getCouncil().getCouncilPositions().get(0), this :: onStartCouncil);
        Wrapper productionWrapper = new Wrapper(field.getProduction().getProduction().get(0), this :: onStartProduction);
        Wrapper harvestWrapper = new Wrapper(field.getHarvest().getHarvest().get(0), this ::onStartHarvest);
        positionsHandlerHashMap.put("START COUNCIL", councilWrapper);
        positionsHandlerHashMap.put("START PRODUCTION", productionWrapper);
        positionsHandlerHashMap.put("START HARVEST", harvestWrapper);
    }

    /**
     * Start the player turn
     * @param player the player who is going to play this turn
     */
    private void startPlayerTurn(Player player){
        turnEnded = false;
        sendTurnNotification(player);
        currentPlayer = player;
        sendFieldToAll();
        startTimer();
        if(!recoveryMode)
            currentPlayerFamilyMemberPlaced = false;
        try {
            while (!turnEnded) {
                try {
                    sendPlayerStatusToAll();
                    checkCouncilPrivilege();
                    this.persistenceHandler.saveGameStatus(room, recoveryMode);
                    currentPlayer.getRemotePlayer().sendPositions(positionsHandlerHashMap.keySet());
                    String[] actionListToSend = updateActionList();
                    clientAction = player.getRemotePlayer().whatAction(actionListToSend, privateTimeOut);
                    actionsHandlerHashMap.get(clientAction.getCode()).handleAction();
                    currentPlayer.subtractResources(currentPlayer.getTemporaryResources());
                    currentPlayer.removeFamilyMember();

                } catch (NotEnoughValueException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] Your family member has not the required value to perform this action");
                } catch(YouCannotPickCardException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] You cannot pick card, you have too many cards or you don't have required military points");
                } catch (NotEnoughPlayersException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] You cannot perform this action because there are not Enough players");
                } catch (NotEnoughResourcesException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] You don't have enought resources to perform this action");
                } catch (NotEmptyPositionException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] This position is occupied");
                } catch (LeaderCardIsActivatedException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] This leader card is Already Active");
                } catch (NotHaveLeaderRequirementsException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] you don't have leader requirements");
                } catch (ThereIsAnotherFamilyMemberOnSimilarPosition e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] There is a FamilyMember of the same color in a similar position");
                } catch (TimeOutException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage(" time out has expired...");
                    turnEnded = true;
                } catch (FamilyMemberAlreadyPlacedException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] You already have placed a family member!");
                } catch (PositionIsEmptyException e) {
                    Display.println(e);
                    player.getRemotePlayer().sendMessage("[Error] This position is empty you cannot pick this card");
                }
                currentPlayer.setTemporaryResources(new Resources());
                currentPlayer.setTemporaryFamilyMember(null);
            }
        } catch (ServerException e) {
            Display.println(e);
            playerOffline(currentPlayer);
        }
    }

    private String[] updateActionList() {
        int length;
        if(currentPlayerFamilyMemberPlaced)
            length = actionList.length - 1;
        else
            length = actionList.length;
        String[] actionListToReturn = new String[length];
        int index = 0;
        for (String anActionList : actionList)
            if (!anActionList.equals(Actions.PLACE_FAMILY_MEMBERS) || !currentPlayerFamilyMemberPlaced) {
                actionListToReturn[index] = anActionList;
                index++;
            }
        return actionListToReturn;
    }

    private void startTimer(){
        privateTimeOut = generalTimeOut;
        int delay = 0;
        int period = 1000;
        if(this.timer != null)
            timer.cancel();
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                privateTimeOut = setInterval(timer, privateTimeOut);
            }
        }, delay, period);
    }

    private int setInterval(Timer timer, int timeOut){
        if(privateTimeOut < 1) {
            timer.cancel();
            timer.purge();
        }
        int newTimeOut = timeOut;
        return --newTimeOut;
    }

    /**
     * Start the harvest
     * @param familyMember the family member
     * @param position the position is null and is created afterwards
     * @throws ServerException if the player is disconnected
     */
    private void onStartHarvest(FamilyMember familyMember, Position position) throws ServerException, NotEnoughPlayersException, NotEnoughValueException {
        currentPlayer.startHarvest(familyMember,(HarvestPosition) position);
        currentPlayerFamilyMemberPlaced = true;
        sendHarvestAreaUpdate();
    }

    private void sendHarvestAreaUpdate(){
        for(Player p : field.getPlayersOrder()) {
            try {
                p.getRemotePlayer().sendHarvestAreaUpdate(field.getHarvest());
            } catch (ServerException e) {
                Display.println(e);
                playerOffline(p);
            }
        }
    }

    /**
     * Start the production
     * @param familyMember the family member that is going to occupy the position
     * @param position the position is null and is created afterwards
     * @throws NotEnoughPlayersException if there aren't enough players to create a new production position
     * @throws NotEnoughValueException if the value of the family member is not high enough to occupy the position
     * @throws ServerException if the player is disconnected
     */
    private void onStartProduction(FamilyMember familyMember, Position position) throws NotEnoughPlayersException, NotEnoughResourcesException, NotEnoughValueException, ServerException, TimeOutException {
        currentPlayer.startProduction(familyMember, (ProductionPosition) position);
        currentPlayer.getRemotePlayer().sendMessage("Production Ended");
        currentPlayerFamilyMemberPlaced = true;
        sendProductionAreaUpdate();
    }

    private void sendProductionAreaUpdate(){
        for(Player p : field.getPlayersOrder()) {
            try {
                p.getRemotePlayer().sendProductionArea(field.getProduction());
            } catch (ServerException e) {
                Display.println(e);
                playerOffline(p);
            }
        }
    }

    /**
     * Start the council
     * @param familyMember the family member that is going to occupy the position
     * @param position the position is null and is created afterwards
     * @throws NotEnoughValueException if the value of the family member is not high enough to occupy the position
     * @throws ServerException if the player is disconnected
     */
    private void onStartCouncil(FamilyMember familyMember, Position position) throws NotEnoughValueException, ServerException {
        Resources resourcesEarned = field.getCouncil().startCouncil((CouncilPosition) position, familyMember);
        currentPlayer.addResources(resourcesEarned);
        currentPlayer.getRemotePlayer().sendMessage("[Council] Family Member Placed in the council place: You Got"+resourcesEarned.toString());
        currentPlayerFamilyMemberPlaced = true;
        sendCouncilUpdate();
    }

    private void sendCouncilUpdate(){
        for(Player p : field.getPlayersOrder()) {
            try {
                if(!p.getRemotePlayer().isOffline())
                    p.getRemotePlayer().sendCouncilAreaUpdate(field.getCouncil());
            } catch (ServerException e) {
                Display.println(e);
                playerOffline(p);
            }
        }
    }

    /**
     * Put the family member in a market position if possible
     * @param familyMember the family member that is going to occupy the position
     * @param position the position chosen by the player
     * @throws NotEnoughValueException if the value of the family member is not high enough to occupy the position
     */
    private void handleMarketPosition(FamilyMember familyMember, Position position) throws NotEnoughValueException, ServerException {
        Resources resourcesEarned = currentPlayer.goToMarket(familyMember, (MarketPosition) position);
        currentPlayer.getRemotePlayer().sendMessage("[Market] Family Member placed in the market: You Got"+resourcesEarned.toString());
        currentPlayer.addResources(resourcesEarned);
        currentPlayerFamilyMemberPlaced = true;
        sendMarketAreaUpdate();
    }

    private void sendMarketAreaUpdate(){
        for(Player p : field.getPlayersOrder()) {
            try {
                if(!p.getRemotePlayer().isOffline())
                    p.getRemotePlayer().sendMarketAreaUpdate(field.getMarket());
            } catch (ServerException e) {
                Display.println(e);
                playerOffline(p);
            }
        }
    }

    /**
     * Put the family member in a tower position if possible
     * @param familyMember the family member that is going to occupy the position
     * @param position the position chosen by the player
     * @throws NotEnoughValueException if the value of the family member is not high enough to occupy the position
     * @throws NotEnoughResourcesException if the player has not enough resources to pick the card
     * @throws TimeOutException if the timeout has expired
     */
    private void handleTowerPosition(FamilyMember familyMember, Position position) throws NotEnoughResourcesException, NotEnoughValueException, TimeOutException, YouCannotPickCardException, NotEmptyPositionException, PositionIsEmptyException {
        //the following line checks if player has less than maximum number of cards allowed and if it has enough
        //military points to get a green card.
        if(!currentPlayer.canPickCard((TowerPosition) position)){
            throw new YouCannotPickCardException();
        }
        currentPlayer.pickCard(familyMember, ((TowerPosition) position));
        currentPlayerFamilyMemberPlaced = true;
        sendTowersUpdate();
        sendPlayerStatusToAll();
    }

    private void sendTowersUpdate(){
        for(Player p : field.getPlayersOrder()) {
            try {
                if(!p.getRemotePlayer().isOffline())
                    p.getRemotePlayer().sendTowersUpdate(field.getAllTowers());
            } catch (ServerException e) {
                Display.println(e);
                playerOffline(p);
            }
        }
    }

    /**
     * Create the hash map of all initial possible actions
     */
    private void createActionsHashMap() {
        actionsHandlerHashMap = new HashMap<>();
        actionsHandlerHashMap.put(Actions.SELL_LEADER_CARDS, this :: onSellLeaderCard);
        actionsHandlerHashMap.put(Actions.ACTIVATE_LEADER_CARDS, this :: onActivateLeaderCard);
        actionsHandlerHashMap.put(Actions.PLACE_FAMILY_MEMBERS, this :: onPlaceFamilyMember);
        actionsHandlerHashMap.put(Actions.PASS_TURN, this :: onPassTurn);
    }

    /**
     *  Set the turnEnded variable to true to terminate the currentPlayer turn
     */
    private void onPassTurn() {
        turnEnded = true;
    }

    /**
     * If the player choose to placeDevCard a family member on the board
     * @throws ServerException if the player is disconnected
     * @throws NotEnoughPlayersException if there aren't enough players to create a new production position
     * @throws NotEnoughValueException if the value of the family member is not high enough to occupy the position
     * @throws ServerException if the player is disconnected
     */
    private void onPlaceFamilyMember() throws ServerException, NotEnoughValueException, NotEnoughPlayersException, NotEnoughResourcesException, NotEmptyPositionException, TimeOutException, ThereIsAnotherFamilyMemberOnSimilarPosition, FamilyMemberAlreadyPlacedException, YouCannotPickCardException, PositionIsEmptyException {
        if(currentPlayerFamilyMemberPlaced)
            throw new FamilyMemberAlreadyPlacedException();
        String chosenPosition = clientAction.getPosition();
        Position position = positionsHandlerHashMap.get(chosenPosition).getPosition();
        if(currentPlayer.isPositionOccupied(position) && !(position instanceof CouncilPosition)) {
            if (position instanceof ProductionPosition) {
                position = field.getProduction().getFreePosition();
            } else if (position instanceof HarvestPosition) {
                position = field.getHarvest().getFreePosition();
            } else {
                throw new NotEmptyPositionException();
            }
        }
        else if(position instanceof CouncilPosition) {
            position = field.getCouncil().getFreePosition();
        }
        FamilyMember familyMember = clientAction.getFamilyMember();
        int servantsToAdd = clientAction.getServants();
        familyMember.addValue(servantsToAdd);
        Resources newTemporaryResources = currentPlayer.getTemporaryResources();
        newTemporaryResources.addResources(new Resources(0,0,servantsToAdd,0,0,0,0,0));
        currentPlayer.setTemporaryResources(newTemporaryResources);
        currentPlayer.setTemporaryFamilyMember(familyMember);
        if(field.isThereAnotherSameFamilyMemberOnSimilarPosition(position,familyMember))
            throw new ThereIsAnotherFamilyMemberOnSimilarPosition();
        positionsHandlerHashMap.get(chosenPosition).getPositionsHandler().handlePosition(familyMember, position);
    }

    /**
     * If the player choose to activate one of his leader cards
     */
    private void onActivateLeaderCard() throws LeaderCardIsActivatedException, NotHaveLeaderRequirementsException, ServerException, TimeOutException, NotEnoughValueException {
        LeaderCard leaderCardChosen = clientAction.getLeaderCard();
        if(leaderCardChosen.isActivated()){
            throw new LeaderCardIsActivatedException();
        }else{
            if(currentPlayer.hasLeaderRequirements(leaderCardChosen.getLeaderRequirements())){
                leaderCardChosen.activate(currentPlayer);
                field.addActivatedLeaderCard(leaderCardChosen);
            }
            else {throw new NotHaveLeaderRequirementsException();}
        }
    }

    /**
     * If the player choose to sell one of his leader cards
     */
    private void onSellLeaderCard(){
        LeaderCard leaderCardChosen = clientAction.getLeaderCard();
        currentPlayer.removeLeaderCard(leaderCardChosen);
        currentPlayer.addResources(new Resources(0, 0, 0, 0, 0 , 0, 0, 1));
    }

    private ArrayList<Player> createPlayers() {
        ArrayList<Player> playersOrder = new ArrayList<>();
        for (int i = 0; i < room.getNumberOfPlayers(); i++) {
            playersOrder.add(new Player(this, PlayerColor.values()[i], room.getPlayers().get(i)));
        }
        return playersOrder;
    }

    /**
     * Initialize the resources for each player
     */
    private void initializeResources() {
        int coins = 0;
        for (Player player : field.getPlayersOrder()) {
            player.addResources(new Resources(config.getStartingWood(),
                    config.getStartingCoins() + coins,
                    config.getStartingServants(),
                    config.getStartingStones(),
                    config.getStartingVictoryPoints(),
                    config.getStartingFaithPoints(),
                    config.getStartingMilitaryPoints(),
                    config.getStartingCouncilPrivilege()));
            coins += 1;
        }
    }

    /**
     * Send the field to all the players
     */
    private void sendFieldToAll(){
        for (Player player : field.getPlayersOrder())
            try {
                if(!player.getRemotePlayer().isOffline())
                    player.getRemotePlayer().sendField(this.field);
            } catch (ServerException e) {
                Display.println(e);
                playerOffline(player);
            }

    }

    /**
     * Send the player status to all the players
     */
    private void sendPlayerStatusToAll(){
        for (Player player : field.getPlayersOrder())
            try {
                if(!player.getRemotePlayer().isOffline())
                    player.getRemotePlayer().sendPlayerStatus(field.getPlayersOrder());
            } catch (ServerException e) {
                Display.println(e);
                playerOffline(player);
            }
    }

    /**
     * Set the family members of the players to the dices value
     */
    private void setAllFamilyMembers() {
        for (Player p : field.getPlayersOrder()) {
            p.setFamilyMembers(new ArrayList<>());
            p.getFamilyMemberState().setFamilyMembers(field.getDices());
        }
    }

    /**
     * Allow the players to choose their bonus tile at the beginning of the game
     */
    private void chooseBonusTile() {

        ArrayList<BonusTile> bonusTiles = new ArrayList<>(field.getDeck().getBonusTiles());

        for(Player player : field.getPlayersOrder()){
            try {
                startTimer();
                sendTurnNotification(player);
                BonusTile chosen = player.getRemotePlayer().chooseBonusTile(bonusTiles, privateTimeOut);
                player.setBonusTile(chosen);
                bonusTiles = updateBonusTiles(bonusTiles, chosen);
            }catch (ServerException e){
                //player disconnected during the choice.
                //assign the first bonus tile in the list to the player
                player.setBonusTile(bonusTiles.get(0));
                bonusTiles.remove(bonusTiles.get(0));
                playerOffline(player);
                Display.println(e);
            } catch (TimeOutException e) {
                Display.println(e);
                player.setBonusTile(bonusTiles.get(0));
                bonusTiles.remove(bonusTiles.get(0));
                try {
                    player.getRemotePlayer().sendMessage("Too late, the first bonus tile was chosen for you");
                } catch (ServerException e1) {
                    Display.println(e1);
                    playerOffline(player);
                }
            }

        }
    }

    private ArrayList<BonusTile> updateBonusTiles(ArrayList<BonusTile> bonusTiles, BonusTile chosen){
        for(Iterator<BonusTile> iterator = bonusTiles.iterator(); iterator.hasNext(); ){
            if(iterator.next().getId().equals(chosen.getId())){
                iterator.remove();
                break;
            }
        }
        return bonusTiles;
    }

    /**
     * Allow the players to choose their leader cards at the beginning of the game
     */
    private void chooseLeaderCards(){
        ArrayList<List<LeaderCard>> playersLeaderCard = new ArrayList<>();
        for(int i = 0; i < field.getPlayersOrder().size(); i++) {
            List<LeaderCard> playerLeaderCard;
            playerLeaderCard = field.getDeck().getFourLeaderCard();
            playersLeaderCard.add(playerLeaderCard);
        }
        List<Player> players = new ArrayList<>(field.getPlayersOrder());
        for(int i = 0; i < config.getLeaderCardsToChoose(); i++){
            for(int j = 0; j < playersLeaderCard.size(); j++){
                LeaderCard leaderCardChosen;
                try {
                    startTimer();
                    sendTurnNotification(players.get(j));
                    leaderCardChosen = players.get(j).getRemotePlayer().draftLeaderCard(playersLeaderCard.get(j), privateTimeOut);
                    players.get(j).addLeaderCard(leaderCardChosen);
                    LeaderCard finalLeaderCardChosen = leaderCardChosen;
                    playersLeaderCard.get(j).removeIf(leaderCard -> leaderCard.getId().equals(finalLeaderCardChosen.getId()));
                } catch (ServerException e) {
                    Display.println(e);
                    if(players.get(j).getRemotePlayer().isOffline())
                        playerOffline(players.get(j));
                    leaderCardChosen = playersLeaderCard.get(j).get(0);
                    players.get(j).addLeaderCard(leaderCardChosen);
                    LeaderCard finalLeaderCardChosen1 = leaderCardChosen;
                    playersLeaderCard.get(j).removeIf(leaderCard -> leaderCard.getId().equals(finalLeaderCardChosen1.getId()));
                } catch (TimeOutException e) {
                    Display.println(e);
                    leaderCardChosen = playersLeaderCard.get(j).get(0);
                    players.get(j).addLeaderCard(leaderCardChosen);
                    LeaderCard finalLeaderCardChosen1 = leaderCardChosen;
                    playersLeaderCard.get(j).removeIf(leaderCard -> leaderCard.getId().equals(finalLeaderCardChosen1.getId()));
                    try {
                        players.get(j).getRemotePlayer().sendMessage("Too late, the first leader card was chosen for you");
                    } catch (ServerException e1) {
                        Display.println(e1);
                        playerOffline(players.get(j));
                    }
                }


            }
            swapLeaderCards(playersLeaderCard);
        }
    }

    /**
     * Used for swapping the leader cards between all players during the initial drafting
     * @param playersLeaderCard the array of all the leader cards of all the players
     */
    private void swapLeaderCards(ArrayList<List<LeaderCard>> playersLeaderCard){
        for(int i = 1; i < playersLeaderCard.size(); i++)
            Collections.swap(playersLeaderCard, 0, i);
    }

    /**
     * Send to the players whether is their turn or not
     * @param player is the player who is going to play this turn
     */
    private void sendTurnNotification(Player player){
        for (Player p : field.getPlayersOrder()) {
            try {
                if (player.equals(p) && !player.getRemotePlayer().isOffline())
                    p.getRemotePlayer().isYourTurn();
                else {
                    if (!player.equals(p) && !player.getRemotePlayer().isOffline())
                        p.getRemotePlayer().isNotYourTurn();
                }
            } catch (ServerException e) {
                Display.println(e);
                playerOffline(p);
            }
        }
    }

    /**
     * Start the game
     */
    public void startGame(){
        createFileName();
        gameInit();
        room.startGame();
        room.broadcast("THE GAME HAS STARTED");
        startTheGame(0, 0, field.getPlayersOrder().get(0));
    }


    private void startTheGame(int turn, int turnOfCurrentPlayer, Player playerPlaying){
        for(currentTurn = turn; currentTurn < config.getNumberOfTurns(); currentTurn ++) {
            room.broadcast("turn "+ (currentTurn + 1) + " of " + config.getNumberOfTurns() );
            if(!recoveryMode && turn == 0) {
                room.broadcast("Refreshing map");
                startTurn();
            }
            for (currentPlayerTurn = 0; currentPlayerTurn < config.getNumberOfPlayerTurn(); currentPlayerTurn++) {
                if(recoveryMode)
                    currentPlayerTurn = turnOfCurrentPlayer;
                turnExcommunicatedPlayers = new LinkedList<>();
                for (int i = 0; i < field.getPlayersOrder().size(); i++) {
                    if(recoveryMode){
                        while(!field.getPlayersOrder().get(i).equals(playerPlaying))
                            i++;
                    }
                    if(field.getPlayersOrder().get(i).isTurnExcommunicationState()&& currentPlayerTurn==0){
                        turnExcommunicatedPlayers.add(field.getPlayersOrder().get(i));
                    }else {
                        if(!field.getPlayersOrder().get(i).getRemotePlayer().isOffline())
                            startPlayerTurn(field.getPlayersOrder().get(i));
                    }
                }
            }
            for (Player player: turnExcommunicatedPlayers){
                if(!player.getRemotePlayer().isOffline())
                    startPlayerTurn(player);
            }
            checkExcommunication();
        }
        gameOver();
    }
    private void createFileName() {
        this.persistenceHandler = new PersistenceHandler(this, this.room.getIdRoom());
        this.persistenceHandler.createFile();
    }

    /**
     * This function is used to choose the winner of the game
     */
    private void gameOver() {

        int victoryPointsToAdd;
        int maxMilitaryPoints;
        int secondMilitaryPoints;
        int allResources;
        Resources playerResources;
        List<Player> playersOrder = field.getPlayersOrder();
        for (Player player : playersOrder) {
            victoryPointsToAdd = 0;
            victoryPointsToAdd += player.getFinalVictoryPointsForCards(config);
            //giocatore più in alto sul tracciato dei punti militari.
            playersOrder = new ArrayList<>(field.getPlayersOrder());
            playersOrder.sort(Comparator.comparingInt(o -> o.getPlayerResources().getMilitaryPoints()));
            Collections.reverse(playersOrder);
            maxMilitaryPoints = playersOrder.get(0).getPlayerResources().getMilitaryPoints();
            secondMilitaryPoints = 0;
            if (player.getPlayerResources().getMilitaryPoints() == maxMilitaryPoints) {
                victoryPointsToAdd += 5;
            }
            for (Player player2 : playersOrder) {
                if (player2.getPlayerResources().getMilitaryPoints() < maxMilitaryPoints) {
                    secondMilitaryPoints = player2.getPlayerResources().getMilitaryPoints();
                    break;
                }
            }
            if (player.getPlayerResources().getMilitaryPoints() == secondMilitaryPoints) {
                victoryPointsToAdd += 2;
            }
            // add victory points for resources.
            playerResources = player.getPlayerResources();
            allResources = playerResources.getCoins() + playerResources.getStone() + playerResources.getWood() + playerResources.getServants();
            victoryPointsToAdd += allResources / 5;
            //add victory Points for faith points.
            victoryPointsToAdd += config.getFaithPathPoints()[playerResources.getFaithPoints()];
            player.addResourcesToPlayerResources(new Resources(0, 0, 0, 0, victoryPointsToAdd, 0, 0, 0));
            player.subtractExcommunicationVictoryPoints();
        }
        chooseWinner(playersOrder);
        sendRankingToAll();
        Server.eliminateRoom(room.getIdRoom());
    }

    private void chooseWinner(List<Player> playersOrder){
        playersOrder.sort(Comparator.comparingInt(o -> o.getPlayerResources().getVictoryPoints()));
        Collections.reverse(playersOrder);
        int victoryPointsToWin = playersOrder.get(0).getPlayerResources().getVictoryPoints();
        for(Player player:playersOrder){
            try {
                if (player.getPlayerResources().getVictoryPoints() == victoryPointsToWin) {
                    Display.println("[Winner]player of color"+player.getPlayerColor()+"victory Points: "+player.getPlayerResources().getVictoryPoints());
                   player.getRemotePlayer().youWon();
                    dataBase.insertMatch(player.getUsername(), player.getPlayerResources().getVictoryPoints(), true);
                } else {
                    Display.println("[Loser]player of color"+player.getPlayerColor()+"victory Points: "+player.getPlayerResources().getVictoryPoints());
                    player.getRemotePlayer().youLost();
                    dataBase.insertMatch(player.getUsername(), player.getPlayerResources().getVictoryPoints(), false);
                }
            }catch(ServerException e){
                Display.println(e);
                playerOffline(player);
            }catch (SQLException e){
                Display.println("SQL exception" , e);
            }
        }
    }

    private void sendRankingToAll() {
        for (Player player : field.getPlayersOrder()){
            try {
                RemotePlayer p = player.getRemotePlayer();
                p.sendRanking(dataBase.getRanking());
            }catch (SQLException | ServerException e){
                Display.println(e);
            }

        }
        if(!this.persistenceHandler.deleteGameFile())
            Display.println("\nFILE OF ROOM " + this.room.getIdRoom() + " HAS NOT BEEN ELIMINATED\n");
    }

    /**
     * Check if is time for the player to show their support to the Vatican
     */
    private void checkExcommunication(){
        if(turn%2==0){
            int era = turn/2;
            ExcommunicationCard excommunicationCard = field.getExcommunicationCard(era);
            for(Player player:field.getPlayersOrder()){
                if(player.getPlayerResources().getFaithPoints() >= config.getFaithPointsNeededToAvoidExcommunication()[era-1]){
                    boolean showSupport;
                    try {
                        sendTurnNotification(player);
                        showSupport = player.getRemotePlayer().showSupport(excommunicationCard, privateTimeOut);
                    } catch (TimeOutException e) {
                        Display.println(e);
                        showSupport = false;
                    } catch (ServerException e){
                        Display.println(e);
                        showSupport = false;
                        playerOffline(player);
                    }
                    if (showSupport) {
                        int playerFaithPoints = player.getPlayerResources().getFaithPoints();
                        try {
                            player.subtractResources(new Resources(0, 0, 0, 0, 0, playerFaithPoints, 0, 0));
                        } catch (NotEnoughResourcesException e) {
                            Display.println(e);
                        }
                        player.addResourcesToPlayerResources(new Resources(0, 0, 0, 0, config.getFaithPathPoints()[playerFaithPoints], 0, 0, 0));
                        int showSupportBonus = player.getShowSupportBonus();
                        player.addResourcesToPlayerResources(new Resources(0, 0, 0, 0, showSupportBonus, 0, 0, 0));
                    } else {
                        excommunicationCard.activate(player);
                        player.addExcommunication(excommunicationCard);
                        try {
                            player.getRemotePlayer().sendMessage("[Excommunication] You got an Excommunication");
                        } catch (ServerException e) {
                            Display.println(e);
                            playerOffline(player);
                        }
                    }
                } else {
                    excommunicationCard.activate(player);
                    player.addExcommunication(excommunicationCard);
                    try {
                        player.getRemotePlayer().sendMessage("[Excommunication] You got an Excommunication");
                    } catch (ServerException e) {
                        Display.println(e);
                        playerOffline(player);
                    }
                }
            }
        }
    }

    /**
     * Initialize the game
     */
    private void gameInit() {
        createPlayers();
        initializeResources();
        field.placeExcommunicationCards();
        createActionsHashMap();
        initializePositionHandlerMap();
        field.getDeck().shuffleDeck();
        chooseBonusTile();
        chooseLeaderCards();
    }

    /**
     * Start a new turn: there are 2 turns in each Period
     */
    private void startTurn() {
        changeTurnOrder();
        turn = turn + 1;
        field.throwDices();
        field.refreshField();
        field.placeCards();
        setAllFamilyMembers();
        deactivatePerTurnLeaderCards();
    }

    /**
     * this method is used to deactivate all per turn leader cards
     */
    public void deactivatePerTurnLeaderCards() {
        for(Player player: field.getPlayersOrder()){
            player.deactivateLeaderCards();
        }
    }

    /**
     * Change the game order according to the order of the council positions
     */
    public void changeTurnOrder() {
        ArrayList<CouncilPosition> councilPositions = new ArrayList<>(field.getCouncil().getCouncilPositions());
        ArrayList<Player> newTurnOrder = new ArrayList<>();
        for (CouncilPosition councilPosition : councilPositions) {
            if (councilPosition.getFamilyMember() != null) {
                PlayerColor playerColor = councilPosition.getFamilyMember().getPlayerColor();
                for (Player player : field.getPlayersOrder())
                    if (player.getPlayerColor().equals(playerColor) && !newTurnOrder.contains(player)){
                        newTurnOrder.add(player);
                        break;
                    }
            }
        }
        for(Player player : field.getPlayersOrder()){
            if(!newTurnOrder.contains(player))
                newTurnOrder.add(player);
        }
        field.setPlayersOrder(newTurnOrder);
    }

    /**
     * Set a disconnected player offline and inform all the other players
     * @param player the player who is going to be set offline
     */
    private void playerOffline(Player player) {
        player.getRemotePlayer().setOffline(true);
        room.broadcast("The player " + player.getRemotePlayer().getUsername() + " has left the game");
    }

    /**
     * Check if the current player has a council privilege to convert into a Resources, if so asks
     * him which type of resource, according to the game config, he wants and then add it to the player
     * @throws ServerException if the current player is disconnected
     */
    private void checkCouncilPrivilege() throws ServerException, NotEnoughResourcesException, TimeOutException {
        int councilPrivilege = currentPlayer.getPlayerResources().getCouncilPrivilege();
        if (councilPrivilege > 0) {
            List<Resources> resourcesToAdd = currentPlayer.getRemotePlayer().convertCouncilPrivilege(councilPrivilege, config.getCouncilPrivilegeToResources(), privateTimeOut);
            for (Resources aResourcesToAdd : resourcesToAdd) {
                currentPlayer.addResources(aResourcesToAdd);
            }
            currentPlayer.getPlayerResources().resetCouncilPrivilege();
        }
        sendPlayerStatusToAll();
    }

    /**
     * this method is used to allow player to choose the cost of a card if that card has more than one cost
     * @param player  the player that has to choose
     * @param costs the possible costs that can be chose
     * @return chosen cost
     * @throws TimeOutException if timeout expires
     */
    public Cost chooseCost(Player player, ArrayList<Cost> costs) throws TimeOutException {
        try {
            return player.getRemotePlayer().chooseCost(costs, privateTimeOut);
        }catch (ServerException e){
            Display.println(e);
            playerOffline(player);
            return costs.get(0);
        }
    }


    public YellowReward chooseRewardToProduce(YellowCard yellowCard, Player player) {
        try {
            return player.getRemotePlayer().chooseRewardToProduce(yellowCard, privateTimeOut);
        } catch (ServerException e) {
            Display.println(e);
            playerOffline(player);
            return (yellowCard.getYellowReward().get(0));
        } catch (TimeOutException e) {
            Display.println(e);
            return (yellowCard.getYellowReward().get(0));
        }
    }

    /**
     * this method is used to chose an Instant Reward Pick thrown by some particular cards.
     * @param player is the player that have to receive the reward
     * @param cardColor is the color of the  tower player wants to receive reward
     * @return TowerPosition in which player wants to place card.
     */
    public TowerPosition chooseInstantRewardPosition(Player player, CardColor cardColor){
        HashMap<String,TowerPosition> map = new HashMap<>(field.generateTowerMap(cardColor));
        TowerPosition realPosition;
        try {
            String position = player.getRemotePlayer().chooseInstantRewardPosition(map.keySet(), privateTimeOut);
            realPosition = map.get(position);
        }catch (ServerException e){
            Display.println(e);
            playerOffline(player);
            realPosition = map.get(map.keySet().toArray()[0]);
        } catch (TimeOutException e) {
            Display.println(e);
            realPosition = map.get(map.keySet().toArray()[0]);
        }
        if(realPosition.getCard()!=null){
            return realPosition;
        }
        return null;
    }

    public  Field getField() {
        return field;
    }

    /**
     * this method is used to allow the player to chose the productionCard
     * @param cards possible production card to be chosen
     * @param player the player that have to chose the cards
     * @return cards that have been chosen by the user
     */
    public  List<YellowCard> chooseProductionCards(List<YellowCard> cards, Player player){
        try {
            return player.getRemotePlayer().chooseProductionCards(cards, privateTimeOut);
        } catch (ServerException e) {
            Display.println(e);
            playerOffline(player);
            List<YellowCard> yellowCards = new LinkedList<>();
            yellowCards.add(cards.get(0));
            return yellowCards;
        } catch (TimeOutException e) {
            Display.println(e);
            List<YellowCard> yellowCards = new LinkedList<>();
            yellowCards.add(cards.get(0));
            return yellowCards;
        }
    }

    /**
     * this method is used to handle the very particular interaction given by Lorenzo De Medici
     * @param player the player that activates the card
     */
    public void activateLorenzoDeMedici(Player player) {
        List<LeaderCard> activatedLeaderCard = field.getActivatedLeaderCard();
        LeaderCard leaderCard;
        try {
            leaderCard = player.getRemotePlayer().chooseLeaderCardToCopy(activatedLeaderCard, privateTimeOut);
        } catch (ServerException e) {
            Display.println(e);
            playerOffline(player);
            leaderCard = activatedLeaderCard.get(0);
        } catch (TimeOutException e) {
            Display.println(e);
            leaderCard = activatedLeaderCard.get(0);
        }
        player.addLeaderCard(leaderCard);
    }

    void setRecoveryMode(boolean recoveryMode){
        this.recoveryMode = recoveryMode;
    }

    /**
     * this method is used to handle the interaction
     * @param player the player that wants to activate the leader card
     */
    public void activateFedericoDaMontefeltro(Player player) {
        List<FamilyMember> playerFamilyMembers = player.getFamilyMembers();
        FamilyMember familyMember;
        try {
            familyMember = player.getRemotePlayer().chooseAFamilyMemberToAddValue(playerFamilyMembers, privateTimeOut);
        } catch (ServerException e) {
            Display.println(e);
            familyMember = new FamilyMember(player.getPlayerColor(), DiceColor.NEUTRAL, 0);
            playerOffline(player);
        } catch (TimeOutException e) {
            Display.println(e);
            familyMember = new FamilyMember(player.getPlayerColor(), DiceColor.NEUTRAL, 0);
        }
        player.setValueToAFamilyMember(familyMember,6);
    }

    public void startAfterRecovery() {
        setRecoveryMode(true);
        this.dataBase = new Database();
        room.broadcast("THE GAME HAS STARTED");
        startTheGame(this.currentTurn, this.currentPlayerTurn, this.currentPlayer);
    }

    /**
     * A private interface to handle the action the player choose
     */
    private interface ActionsHandler extends Serializable{
        void handleAction() throws ServerException, NotEnoughValueException, NotEnoughPlayersException, NotEnoughResourcesException, NotEmptyPositionException, LeaderCardIsActivatedException, NotHaveLeaderRequirementsException, TimeOutException, ThereIsAnotherFamilyMemberOnSimilarPosition, FamilyMemberAlreadyPlacedException, YouCannotPickCardException, PositionIsEmptyException;
    }

    /**
     * A private interface to handle the position the player choose if he wants to placeDevCard a family member on the board
     */
    private interface PositionsHandler extends Serializable{
        void handlePosition(FamilyMember familyMember, Position position) throws ServerException, NotEnoughValueException, NotEnoughPlayersException, NotEnoughResourcesException, TimeOutException, YouCannotPickCardException, NotEmptyPositionException, PositionIsEmptyException;
    }

    /**
     * A private class to be able to construct the positionHandlerHashMap with two values, the position
     * and the method linked with that position
     */
    private class Wrapper implements Serializable{
        private static final long serialVersionUID = 50L;
        private Position position;
        private PositionsHandler positionsHandler;

        Wrapper (Position position, PositionsHandler positionsHandler){
            this.position = position;
            this.positionsHandler = positionsHandler;
        }

        Position getPosition(){
            return position;
        }
        PositionsHandler getPositionsHandler() {
            return positionsHandler;
        }
    }

    /**
     * this method is used for testing purposes only.
     * @param field is the field set in the master
     */
    public void setField(Field field) {
        this.field = field;
    }
}
