package network.client.rmi;

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
import network.protocol.ClientAction;
import network.protocol.ServerAction;
import network.protocol.NetworkProtocol;
import view.Client;
import view.ControllerUI;
import network.server.remotePlayer.RMINetworkInterface;
import network.server.rmi.RMIRemotePlayer;
import view.cli.Display;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * This class is created as soon as the user logged properly with the server. It uses rmi methods
 */
public class RMIClient extends Client implements RMIClientInterface {
    private RMINetworkInterface rmiNetworkInterface;
    private static final int PORT = 3050;
    private String ip;
    private RMIRemotePlayer rmiPlayer;
    private BlockingQueue<Object> queue;

    /**
     * Instantiate the RMIClient
     * @param ip ip of the server
     * @param ui the UI of the user (CLI or GUI)
     * @throws ServerException if there are problems with the connection of the server
     */
    public RMIClient(String ip, ControllerUI ui) throws ServerException{
        super(ui);
        this.ip = ip;
        try {
            Display.println("Trying to fetch client ip address.... ");
            System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
            Display.println("Exporting...");
            UnicastRemoteObject.exportObject(this, 0);
        }catch (RemoteException | UnknownHostException e){
            throw new ServerException(e);
        }
        Display.println("Trying to connect with RMI...");
        lookUpForRegistry();
        int MAXIMUM_ACTION_TO_SEND = 6;
        this.queue = new ArrayBlockingQueue<>(MAXIMUM_ACTION_TO_SEND);
    }

    /**
     * This method looks for the registry and binds it
     * @throws ServerException if the player can't connect to the server
     */
    private void lookUpForRegistry() throws ServerException {
        try{
            Display.println("Binding registry....");
            Registry registry = LocateRegistry.getRegistry(ip, PORT);
            String remoteObject = "RMIServer";
            rmiNetworkInterface = (RMINetworkInterface) registry.lookup(remoteObject);
        }catch (RemoteException | NotBoundException e){
            throw new ServerException(e);
        }
    }

    /**
     * Try to sign in.
     * @param username given by the user
     * @param password given by the user
     * @return true if has success
     * @throws ServerException if there's any problem with the network
     */
    @Override
    public boolean signIn(String username, String password) throws ServerException{
        try{
            if(rmiNetworkInterface.singIn(username,password)){
                createPlayer(username);
                rmiNetworkInterface.joinRoom(rmiPlayer);
                return true;
            }else {
                return false;
            }
        }catch (IOException e){
            throw new ServerException(e);
        }
    }

    /**
     * Try to sign up
     * @param username given by the user
     * @param password given by the user
     * @return true if the username does not exists and log on.
     * @throws ServerException if there is any network problem
     */
    @Override
    public boolean signUp(String username, String password) throws ServerException {
        try {
            if(rmiNetworkInterface.singUp(username, password)){
                createPlayer(username);
                rmiNetworkInterface.joinRoom(rmiPlayer);
                return true;
            }else {
                return false;
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Start the client
     */
    @Override
    public void start() {
        getControllerUI().setClient(this);
    }

    /**
     * Send an action to the server
     * @param object the object to send
     * @throws ServerException if there are problems in the sending phase
     */
    @Override
    public void sendAction(Object object) throws ServerException {
        try {
            queue.put(object);
        } catch (InterruptedException e) {
            throw new ServerException(e);
        }
    }

    @Override
    public void onClose() {

    }

    /**
     * Create an RMIRemotePlayer associated with this RMIClient
     * @param username the username of the player
     */
    private void createPlayer(String username){
        rmiPlayer = new RMIRemotePlayer(username, this);
    }

    /**
     * When receiving a message from the server
     * @param message the message received
     */
    @Override
    public void onMessage(String message){
        controllerUI.showMessage(message);
    }

    /**
     * When receiving the possible actions from the server
     * @param actionList the actionList
     * @param privateTimeOut the time left to choose
     * @throws IOException if there are problems in the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void onAction(String[] actionList, int privateTimeOut) throws TimeOutException, IOException {
        controllerUI.setTimeLeft(privateTimeOut);
        controllerUI.startTimer();
        controllerUI.setActions(actionList);
    }

    /**
     * Notify to the player that it is his turn
     */
    @Override
    public void isYourTurn(ServerAction serverAction) {
        controllerUI.yourTurn(serverAction.getCode());
    }

    /**
     * Notify to the player that it is not his turn
     */
    public void isNotYourTurn(ServerAction serverAction){
        controllerUI.notYourTurn(serverAction.getCode());
    }

    /**
     * When receiving the field from the server
     * @param field the new filed
     */
    @Override
    public void onField(Field field) {
        controllerUI.setField(field);
    }

    /**
     * When receiving the update on the status of the players
     * @param player the list of the players
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void onPlayerStatus(List<Player> player) throws RemoteException {
        controllerUI.setPlayerStatus(player);
    }

    /**
     * If the player is asked on how to convert his council privileges
     * @param councilPrivilege the number of council privileges he's able to convert
     * @param councilPrivilegeToResources the resources he can get in exchange
     * @param privateTimeOut the time left to choose
     * @return the resources chosen by the player
     * @throws TimeOutException if the time out has expired
     * @throws RemoteException if the are problems with the connection with the player
     */
    @Override
    public ArrayList<Resources> onConvertCouncilPrivilege(int councilPrivilege, List<Resources> councilPrivilegeToResources, int privateTimeOut) throws TimeOutException, RemoteException {
        controllerUI.convertCouncilPrivileges(councilPrivilege, councilPrivilegeToResources);
        ClientAction clientAction = (ClientAction) takeObjectFromQueue();
        return (ArrayList<Resources>) clientAction.getObjects();
    }

    /**
     * When the player is asked to choose a bonus tile at the beginning of the game
     * @param bonusTiles the list of all the bonus tiles available
     * @param privateTimeOut the time left to choose
     * @throws RemoteException if the are problems with the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void onChooseBonusTile(List<BonusTile> bonusTiles, int privateTimeOut) throws RemoteException, TimeOutException {
        controllerUI.setTimeLeft(privateTimeOut);
        controllerUI.startTimer();
        controllerUI.chooseBonusTile(bonusTiles);
    }

    /**
     * Ask the player to choose a leader card during the drafting phase
     * @param leaderCardList the list of the leader cards
     * @param privateTimeOut the time left to choose
     * @throws RemoteException if there are problems with the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void onDraftLeaderCards(List<LeaderCard> leaderCardList, int privateTimeOut) throws RemoteException, TimeOutException {
        controllerUI.setTimeLeft(privateTimeOut);
        controllerUI.startTimer();
        controllerUI.draftLeaderCards(leaderCardList);
    }

    /**
     * Take the object from the queue (which has been put by the player) and return
     * it to the server
     * @return the object read
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public Object takeObjectFromQueue() throws RemoteException{
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            throw new RemoteException();
        }
    }

    /**
     * Send to the players all the position available
     * @param positions the positions
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void setPositions(String[] positions) throws RemoteException {
        controllerUI.setPositions(positions);
    }

    /**
     * Ask the player which cards he wants to activate after the starting of a production
     * @param cards the cards he can activate
     * @param privateTimeOut the time left to choose
     * @throws RemoteException if there are problems with the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseProductionCards(List<YellowCard> cards, int privateTimeOut) throws RemoteException, TimeOutException {
        controllerUI.setTimeLeft(privateTimeOut);
        controllerUI.startTimer();
        controllerUI.chooseProductionCards(cards);
    }

    /**
     * Ask the player which cost he wants to pay after taking a card with more than
     * one cost
     * @param costs the costs the player can choose to pay
     * @param privateTimeOut the time left to choose
     * @throws RemoteException if there are problems with the connection of the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseCost(List<Cost> costs, int privateTimeOut) throws RemoteException, TimeOutException{
        controllerUI.setTimeLeft(privateTimeOut);
        controllerUI.startTimer();
        controllerUI.chooseCost(costs);
    }

    /**
     * Send to the player the update of the towers' area
     * @param towers the new towers' area
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void sendTowersUpdate(List<Tower> towers) throws RemoteException {
        controllerUI.updateTowers(towers);
    }

    /**
     * Send to the player the update of the market's area
     * @param marketArea the new market's area
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void sendMarketUpdate(MarketArea marketArea) throws RemoteException {
        controllerUI.updateMarketArea(marketArea);
    }

    /**
     * Send to the player the update of the harvest's area
     * @param harvestArea the new harvest's area
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void sendHarvestUpdate(HarvestArea harvestArea) throws RemoteException {
        controllerUI.updateHarvestArea(harvestArea);
    }

    /**
     * Send to the player the update of the production's area
     * @param productionArea the new production's area
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void sendProductionUpdate(ProductionArea productionArea) throws RemoteException {
        controllerUI.updateProductionArea(productionArea);
    }

    /**
     * Send to the player the update of the council's area
     * @param councilArea the new council's area
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void sendCouncilUpdate(CouncilArea councilArea) throws RemoteException {
        controllerUI.updateCouncilArea(councilArea);
    }

    /**
     * If the player is reconnected after a crash
     * @param field the updated field
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void reconnect(Field field) throws RemoteException {
        controllerUI.reconnect(field);
    }

    /**
     * Ask the player if he wants to support the Vatican
     * @param excommunicationCard the excommunication card of the actual period
     * @param privateTimeOut the time left to choose
     * @throws RemoteException if there are problems with the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void showSupport(ExcommunicationCard excommunicationCard, int privateTimeOut) throws RemoteException, TimeOutException {
        controllerUI.setTimeLeft(privateTimeOut);
        controllerUI.startTimer();
        controllerUI.onShowSupport(excommunicationCard);
    }

    /**
     * Notify to the player that the game has started
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void startGame() throws RemoteException {
        controllerUI.startGame(NetworkProtocol.GAME_STARTED);
    }

    /**
     * If an user is trying to connect with an username associated with an account
     * already online
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void onAlreadyOnlineUsers() throws RemoteException {
        controllerUI.alreadyOnlineWarning("ALREADY ONLINE");
    }

    /**
     * After the player has chosen to activate the "Lorenzo de Medici" leader card, ask him
     * what other leader's ability he wants to copy
     * @param leaderCards the leader cards he can copy
     * @param privateTimeOut the time left to choose
     * @throws RemoteException if there are problems with the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseLeaderCardToCopy(List<LeaderCard> leaderCards, int privateTimeOut) throws RemoteException, TimeOutException {
        controllerUI.setTimeLeft(privateTimeOut);
        controllerUI.startTimer();
        controllerUI.chooseLeaderCardToCopy(leaderCards);
    }

    /**
     * After the player has chosen to activate the "Federico da Montefeltro" leader card,
     * ask him which familyMember he wants to have a permanent value of 6
     * @param playerFamilyMembers the list of the familyMembers he can choose
     * @param privateTimeout the time left to choose
     * @throws RemoteException if there are problems with the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseAFamilyMemberToAddValue(List<FamilyMember> playerFamilyMembers, int privateTimeout) throws RemoteException, TimeOutException {
        controllerUI.setTimeLeft(privateTimeout);
        controllerUI.startTimer();
        controllerUI.chooseFamilyMemberToAddValue(playerFamilyMembers);
    }

    /**
     * Ask the player to choose in which position he wants to go after he picks an
     * instantRewardPick
     * @param positions the positions he can choose
     * @param privateTimeout the time left to choose
     * @throws RemoteException if there are problems with the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseInstantRewardPosition(String[] positions, int privateTimeout) throws RemoteException, TimeOutException {
        controllerUI.setTimeLeft(privateTimeout);
        controllerUI.startTimer();
        controllerUI.chooseInstantRewardPosition(positions);
    }

    /**
     * Ask the player what he wants to produce if he has decided to activate during a
     * production phase a yellow card with more then one reward
     * @param yellowCard the card activated by the player
     * @param privateTimeout the time left to choose
     * @throws RemoteException if there are problems with the connection with the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseRewardToProduce(YellowCard yellowCard, int privateTimeout) throws RemoteException, TimeOutException {
        controllerUI.setTimeLeft(privateTimeout);
        controllerUI.startTimer();
        controllerUI.chooseRewardToProduce(yellowCard);
    }

    /**
     * Notify to the player that he has won
     * @throws RemoteException if there are problems witth the connection with the player
     */
    @Override
    public void youWon() throws RemoteException {
        controllerUI.youWon("YOU WON");
    }

    /**
     * Notify to the player that he has lost
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void youLost() throws RemoteException {
        controllerUI.youLost("YOU LOST...");
    }

    /**
     * Send to the player the statistics of all the players in the room at the end
     * of the game
     * @param ranking the statistics
     * @throws RemoteException if there are problems with the connection with the player
     */
    @Override
    public void sendRanking(String ranking) throws RemoteException {
        controllerUI.showRanking(ranking);
    }
}

