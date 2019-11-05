package network.client.socket;


import model.players.FamilyMember;
import model.players.Player;
import controller.exceptions.ServerException;
import controller.exceptions.TimeOutException;
import model.cards.developmentcards.BonusTile;
import model.cards.developmentcards.Cost;
import model.cards.developmentcards.Resources;
import model.cards.developmentcards.YellowCard;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.gameboard.*;
import network.protocol.ServerAction;
import network.protocol.NetworkProtocol;
import view.ControllerUI;
import view.cli.Display;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A thread lunched by the SocketClient which is responsible for the communication between
 * the server and the player
 */
public class TurnHandler implements Runnable {
    private SocketClient socketClient;
    private Map<String, ChooseTheAction> actions;
    private ControllerUI controllerUI;
    private boolean gameFinished = false;

    /**
     * Instantiate the TurnHandler
     * @param socketClient the socket of the client
     */
    TurnHandler(SocketClient socketClient) {
        this.socketClient = socketClient;
        this.actions = new HashMap<>();
        createHashMap();
        this.controllerUI = socketClient.getControllerUI();
    }

    /**
     * Create the HashMap with the string of the NetworkProtocol class and the lambda
     * function associated with the string
     */
    private void createHashMap() {
        actions.put(NetworkProtocol.ACTION_LIST, this :: onActionList);
        actions.put(NetworkProtocol.MESSAGE, this :: onMessage);
        actions.put(NetworkProtocol.FIELD, this :: onField);
        actions.put(NetworkProtocol.PLAYERS_STATUS, this :: onPlayersStatus);
        actions.put(NetworkProtocol.POSITIONS, this :: onPositions);
        actions.put(NetworkProtocol.YOUR_TURN, this :: onYourTurn);
        actions.put(NetworkProtocol.NOT_YOUR_TURN, this :: onNotYourTurn);
        actions.put(NetworkProtocol.CONVERT_COUNCIL_PRIVILEGE, this :: onConvertCouncilPrivilege);
        actions.put(NetworkProtocol.COUNCIL_UPDATE, this :: onCouncilUpdate);
        actions.put(NetworkProtocol.MARKET_UPDATE, this :: onMarketUpdate);
        actions.put(NetworkProtocol.HARVEST_UPDATE, this :: onHarvestUpdate);
        actions.put(NetworkProtocol.PRODUCTION_UPDATE, this :: onProductionUpdate);
        actions.put(NetworkProtocol.TOWER_UPDATE, this :: onTowersUpdate);
        actions.put(NetworkProtocol.CHOOSE_BONUS_TILE, this :: onBonusTile);
        actions.put(NetworkProtocol.CHOOSE_COST, this :: onChooseCost);
        actions.put(NetworkProtocol.DRAFT_LEADER_CARD, this :: onDraftLeaderCard);
        actions.put(NetworkProtocol.CHOOSE_PRODUCTION_CARDS, this :: onChooseProductionCards);
        actions.put(NetworkProtocol.RECONNECT, this :: onReconnect);
        actions.put(NetworkProtocol.GAME_STARTED, this :: onStartGame);
        actions.put(NetworkProtocol.SHOW_SUPPORT, this :: onShowSupport);
        actions.put(NetworkProtocol.CHOOSE_LEADER_CARD_TO_COPY, this :: onChooseLeaderCardToCopy);
        actions.put(NetworkProtocol.ALREADY_ONLINE, this :: onAlreadyOnline);
        actions.put(NetworkProtocol.CHOOSE_FAMILY_TO_ADD_VALUE, this :: onChooseFamilyMemberToAddValue);
        actions.put(NetworkProtocol.CHOOSE_INSTANT_REWARD_POSITION, this :: onChooseInstantRewardPosition);
        actions.put(NetworkProtocol.CHOOSE_REWARD_TO_PRODUCE, this :: onChooseRewardToProduce);
        actions.put(NetworkProtocol.YOU_WON, this :: onYouWon);
        actions.put(NetworkProtocol.YOU_LOST, this :: onYouLost);
        actions.put(NetworkProtocol.RANKING, this :: onRanking);
    }

    /**
     * Notify to the player he has lost
     * @param serverAction the object sent by the server
     */
    private void onYouLost(ServerAction serverAction) {
        controllerUI.youLost(serverAction.getCode());
    }

    /**
     * Notify to the player he has won
     * @param serverAction the object sent by the server
     */
    private void onYouWon(ServerAction serverAction) {
        controllerUI.youWon(serverAction.getCode());
    }

    /**
     * Ask the player which rewards he wants to produce after he activated a yellowCard
     * with more than one reward during the production phase
     * @param serverAction the object sent by the server
     */
    private void onChooseRewardToProduce(ServerAction serverAction) throws TimeOutException {
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.chooseRewardToProduce((YellowCard) serverAction.getFirstObject());
    }

    /**
     * Ask the player to choose a position to go after he received an instantRewardPick
     * @param serverAction the object sent by the server
     */
    private void onChooseInstantRewardPosition(ServerAction serverAction) throws ServerException, TimeOutException {
        controllerUI.setTimeLeft( serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.chooseInstantRewardPosition(serverAction.getPositions());
    }

    /**
     * If an user is trying to connect with an username associated with an account
     * already online
     * @param socketObject the object sent by the server
     */
    private void onAlreadyOnline(ServerAction socketObject) {
        controllerUI.alreadyOnlineWarning((String) socketObject.getFirstObject());
    }

    /**
     * After the player has chosen to activate the "Federico da Montefeltro" leader card,
     * ask him which familyMember he wants to have a permanent value of 6
     * @param serverAction the object sent by the server
     */
    @SuppressWarnings("unchecked")
    private void onChooseFamilyMemberToAddValue(ServerAction serverAction) throws ServerException, TimeOutException {
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.chooseFamilyMemberToAddValue((List<FamilyMember>) serverAction.getFirstObject());
    }

    /**
     * Ask the player if he wants to support the Vatican
     * @param serverAction the object sent by the server
     */
    private void onShowSupport(ServerAction serverAction) throws ServerException, TimeOutException {
        ExcommunicationCard card = (ExcommunicationCard) serverAction.getFirstObject();
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.onShowSupport(card);
    }

    /**
     * Notify to the player that the game has started
     * @param serverAction the object sent by the user
     */
    private void onStartGame(ServerAction serverAction){
        controllerUI.startGame(serverAction.getFirstObject());
    }

    /**
     * If the player is reconnected after a crash
     * @param serverAction the object sent by the server
     */
    private void onReconnect(ServerAction serverAction){
        Field field = (Field) serverAction.getFirstObject();
        controllerUI.reconnect(field);
    }

    /**
     * Ask the player which cards he wants to activate after the starting of a production
     * @param serverAction the object sent by the server
     */
    @SuppressWarnings("unchecked")
    private void onChooseProductionCards(ServerAction serverAction) throws TimeOutException {
        ArrayList<YellowCard> yellowCards = (ArrayList<YellowCard>) serverAction.getFirstObject();
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.chooseProductionCards(yellowCards);
    }

    /**
     * Ask the player to choose a leader card during the drafting phase
     * @param serverAction the object sent by the server
     */
    @SuppressWarnings("unchecked")
    private void onDraftLeaderCard(ServerAction serverAction) throws TimeOutException {
        List<LeaderCard> leaderCards = (List<LeaderCard>) serverAction.getFirstObject();
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.draftLeaderCards(leaderCards);
    }

    /**
     * When the player is asked to choose a bonus tile at the beginning of the game
     * @param serverAction the object sent by the server
     * @throws ServerException if there are problems with the connection between server and client
     */
    @SuppressWarnings("unchecked")
    private void onBonusTile(ServerAction serverAction) throws ServerException, TimeOutException {
        ArrayList<BonusTile> bonusTiles = (ArrayList<BonusTile>) serverAction.getFirstObject();
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.chooseBonusTile(bonusTiles);
    }

    /**
     * Ask the player which cost he wants to pay after taking a card with more than
     * @param serverAction the object sent by the server
     */
    @SuppressWarnings("unchecked")
    private void onChooseCost(ServerAction serverAction) throws TimeOutException {
        ArrayList<Cost> costs = (ArrayList<Cost>) serverAction.getFirstObject();
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.chooseCost(costs);
    }

    /**
     * Send to the player the update of the towers' area
     * @param serverAction the object sent by the server
     */
    @SuppressWarnings("unchecked")
    private void onTowersUpdate(ServerAction serverAction) {
        ArrayList<Tower> towers = (ArrayList<Tower>) serverAction.getFirstObject();
        controllerUI.updateTowers(towers);
    }

    /**
     * Send to the player the update of the production's area
     * @param serverAction the object sent by the server
     */
    private void onProductionUpdate(ServerAction serverAction) {
        ProductionArea productionArea = (ProductionArea) serverAction.getFirstObject();
        controllerUI.updateProductionArea(productionArea);
    }

    /**
     * Send to the player the update of the harvest's area
     * @param serverAction the object sent by the server
     */
    private void onHarvestUpdate(ServerAction serverAction) {
        HarvestArea harvestArea = (HarvestArea) serverAction.getFirstObject();
        controllerUI.updateHarvestArea(harvestArea);
    }

    /**
     * Send to the player the update of the market's area
     * @param serverAction the object sent by the server
     */
    private void onMarketUpdate(ServerAction serverAction) {
        MarketArea marketArea = (MarketArea) serverAction.getFirstObject();
        controllerUI.updateMarketArea(marketArea);
    }

    /**
     * Send to the player the update of the council's area
     * @param serverAction the object sent by the server
     */
    private void onCouncilUpdate(ServerAction serverAction) {
        CouncilArea councilArea = (CouncilArea) serverAction.getFirstObject();
        controllerUI.updateCouncilArea(councilArea);
    }

    /**
     * If the player is reconnected after a crash
     * @param serverAction the object sent by the server
     */
    private void onField(ServerAction serverAction) {
        controllerUI.setField((Field) serverAction.getFirstObject());
    }

    /**
     * If the player is asked on how to convert his council privileges
     * @param serverAction the object sent by the server
     */
    @SuppressWarnings("unchecked")
    private void onConvertCouncilPrivilege(ServerAction serverAction) throws TimeOutException {
        int numberOfPrivileges = (Integer) serverAction.getFirstObject();
        ArrayList<Resources> resources = (ArrayList<Resources>) serverAction.getSecondObject();
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.convertCouncilPrivileges(numberOfPrivileges, resources);
    }

    /**
     * Notify to the player that it is not his turn
     * @param serverAction the object sent by the server
     */
    private void onNotYourTurn(ServerAction serverAction){
        controllerUI.notYourTurn(serverAction.getCode());
    }

    /**
     * Notify to the player that it is his turn
     * @param serverAction the object sent by the server
     * @throws ServerException if there are problems with the connection between server and client
     */
    private void onYourTurn(ServerAction serverAction) throws ServerException{
        controllerUI.yourTurn(serverAction.getCode());
    }

    /**
     * Send to the players all the position available
     * @param serverAction the object sent by the server
     */
    private void onPositions(ServerAction serverAction){
        String[] positions = (String[]) serverAction.getFirstObject();
        controllerUI.setPositions(positions);
    }

    /**
     * When receiving the update on the status of the players
     * @param serverAction the object sent by the server
     */
    @SuppressWarnings("unchecked")
    private void onPlayersStatus(ServerAction serverAction) {
        ArrayList<Player> players = (ArrayList<Player>) serverAction.getFirstObject();
        controllerUI.setPlayerStatus(players);

    }

    /**
     * When receiving a message from the server
     * @param serverAction the object sent by the server
     */
    private void onMessage(ServerAction serverAction) {
        controllerUI.showMessage((String) serverAction.getFirstObject());
    }

    /**
     * When receiving the possible actions from the server
     * @param serverAction the object sent by the server
     */
    private void onActionList(ServerAction serverAction) throws TimeOutException {
        String[] actionList = (String[]) serverAction.getFirstObject();
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.setActions(actionList);
    }

    /**
     * After the player has chosen to activate the "Lorenzo de Medici" leader card, ask him
     * what other leader's ability he wants to copy
     * @param serverAction the object sent by the server
     */
    @SuppressWarnings("unchecked")
    private void onChooseLeaderCardToCopy(ServerAction serverAction) throws TimeOutException {
        List<LeaderCard> card = (List<LeaderCard>) serverAction.getFirstObject();
        controllerUI.setTimeLeft(serverAction.getTimeout());
        controllerUI.startTimer();
        controllerUI.chooseLeaderCardToCopy(card);
    }

    /**
     * Send to the player the statistics of all the players in the room at the end
     * of the game
     * @param serverAction the object sent by the server
     */
    private void onRanking(ServerAction serverAction){
        String ranking = (String) serverAction.getFirstObject();
        controllerUI.showRanking(ranking);
        gameFinished = true;
    }

    /**
     * Starts the turn handler which waits for a serverAction from the server and call the
     * lambda function associated with the code read in the serverAction object
     */
    @Override
    public void run() {
        while (!gameFinished) {
            try{
                ServerAction serverAction = (ServerAction) socketClient.getObjectInputStream().readObject();
                String code = serverAction.getCode();
                actions.get(code).chooseTheAction(serverAction);
            } catch (ClassNotFoundException | IOException e) {
                Display.println(e);
                socketClient.closeIO();
                return;
            } catch (TimeOutException e) {
                Display.println(e);
            }
        }
    }

    /**
     * A private interface to handle the HashMap with the lambda functions
     */
    private interface ChooseTheAction{

        /**
         * The method which is overridden by the lambda functions
         * @param serverAction the object sent by the server
         * @throws IOException if there are problems with the connection between the user
         * and the server
         */
        void chooseTheAction(ServerAction serverAction) throws IOException, TimeOutException;
    }
}
