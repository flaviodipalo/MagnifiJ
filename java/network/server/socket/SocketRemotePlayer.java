package network.server.socket;

import model.players.FamilyMember;
import model.players.Player;
import controller.exceptions.ServerException;
import controller.exceptions.TimeOutException;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.gameboard.*;
import network.protocol.ClientAction;
import network.protocol.ServerAction;
import network.protocol.NetworkProtocol;
import network.server.remotePlayer.RemotePlayer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Set;

/**
 * An intermediate class between the controller and a player connected with socket
 * which represent the player himself
 */
public class SocketRemotePlayer extends RemotePlayer {
    private transient SocketClientHandler socketClientHandler;
    private static final int NORMAL_SCALE_FOR_SECONDS = 1000;

    /**
     * Create the socketRemotePlayer
     * @param username the username of the player
     * @param socketClientHandler the socketClientHandler of the player
     */
    SocketRemotePlayer(String username, SocketClientHandler socketClientHandler){
        super(username);
        this.socketClientHandler = socketClientHandler;
    }

    /**
     * Notify to the player that it is his turn
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void isYourTurn() throws ServerException{
        socketClientHandler.sendObject(new ServerAction(NetworkProtocol.YOUR_TURN));
    }

    /**
     * Send the field to the player
     * @param field the field to send
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendField(Field field) throws ServerException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.FIELD);
        serverAction.setFirstObject(field);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Ask the player what action he wants to do
     * @param actionList the action available for the player
     * @param privateTimeOut time left to choose
     * @return the string associated with the action
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public ClientAction whatAction(String[] actionList, int privateTimeOut) throws ServerException, TimeOutException{
        ServerAction serverAction = new ServerAction(NetworkProtocol.ACTION_LIST);
        serverAction.setFirstObject(actionList);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            return (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
        } catch (IOException e) {
            throw new TimeOutException(e);
        }
    }


    /**
     * Send to the player the update for the market area
     * @param marketArea the new market area
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendMarketAreaUpdate(MarketArea marketArea) throws ServerException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.MARKET_UPDATE);
        serverAction.setFirstObject(marketArea);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Send to the player the update for the harvest area
     * @param harvestArea the new harvest area
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendHarvestAreaUpdate(HarvestArea harvestArea) throws ServerException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.HARVEST_UPDATE);
        serverAction.setFirstObject(harvestArea);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Send to the player the update of the production area
     * @param productionArea the new production area
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendProductionArea(ProductionArea productionArea) throws ServerException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.PRODUCTION_UPDATE);
        serverAction.setFirstObject(productionArea);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Send to the player the update of the towers
     * @param towers the new towers
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendTowersUpdate(List<Tower> towers) throws ServerException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.TOWER_UPDATE);
        serverAction.setFirstObject(towers);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Send to the player the update of the council area
     * @param councilArea the new council area
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendCouncilAreaUpdate(CouncilArea councilArea) throws ServerException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.COUNCIL_UPDATE);
        serverAction.setFirstObject(councilArea);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Send to the player the update of the status of all the model.players
     * @param players the new status of the model.players
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendPlayerStatus(List<Player> players) throws ServerException{
        ServerAction serverAction = new ServerAction(NetworkProtocol.PLAYERS_STATUS);
        serverAction.setFirstObject(players);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Notify the player that is not his turn
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void isNotYourTurn() throws ServerException{
        ServerAction serverAction = new ServerAction(NetworkProtocol.NOT_YOUR_TURN);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Ask the player which bonus tile he wants during the drafting phase
     * @param bonusTiles the bonus tilers available for the user to choose
     * @param privateTimeOut the time left to choose
     * @return the bonus tile chosen by the user
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public BonusTile chooseBonusTile(List<BonusTile> bonusTiles, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.CHOOSE_BONUS_TILE);
        serverAction.setFirstObject(bonusTiles);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return (BonusTile) clientAction.getObject();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * Ask the player how he wants to convert his council privileges
     * @param councilPrivilege how many council privileges he can convert
     * @param councilPrivilegeToResources  the type of resources the council privilege can be convert into
     * @param privateTimeOut the time left to choose
     * @return list of the resources chosen by the player
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Resources> convertCouncilPrivilege(int councilPrivilege, List<Resources> councilPrivilegeToResources, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.CONVERT_COUNCIL_PRIVILEGE);
        serverAction.setFirstObject(councilPrivilege);
        serverAction.setSecondObject(councilPrivilegeToResources);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);

            return (List<Resources>) clientAction.getObjects();

        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * All the positions of the field where the user can go
     * @param keySet the set of the positions
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendPositions(Set keySet) throws ServerException {
        String[] positions = (String[]) keySet.toArray(new String[keySet.size()]);
        ServerAction serverAction = new ServerAction(NetworkProtocol.POSITIONS);
        serverAction.setFirstObject(positions);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Ask the player in which position he wants to go after he has taken a instantPickReward
     * @param strings all the strings associated with the positions available for the player
     * @param privateTimeOut the time left to choose
     * @return the string associated with the position chosen by the player
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public String chooseInstantRewardPosition(Set<String> strings, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.CHOOSE_INSTANT_REWARD_POSITION);
        String[] positions = strings.toArray(new String[strings.size()]);
        serverAction.setPositions(positions);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return clientAction.getPosition();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * Ask the player which yellow cards he wants to activate after the starting of a production
     * @param cards the cards he can activate
     * @param privateTimeOut the time left to choose
     * @return the yellow cards the user wants to activate
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<YellowCard> chooseProductionCards(List<YellowCard> cards, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.CHOOSE_PRODUCTION_CARDS);
        serverAction.setFirstObject(cards);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return (List<YellowCard>) clientAction.getObjects();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * Ask the user which leader card he wants to keep during the drafting phase
     * @param leaderCardList the list of the leader cards he can choose
     * @param privateTimeOut the time left to choose
     * @return the leader card chosen
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public LeaderCard draftLeaderCard(List<LeaderCard> leaderCardList, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.DRAFT_LEADER_CARD);
        serverAction.setFirstObject(leaderCardList);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return clientAction.getLeaderCard();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * Ask the user if he wants to support the vatican
     * @param excommunicationCard the excommunication card of the period of the game
     * @param privateTimeOut the time left to choose
     * @return true if the player has decided to support the vatican, false otherwise
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public boolean showSupport(ExcommunicationCard excommunicationCard, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.SHOW_SUPPORT);
        serverAction.setFirstObject(excommunicationCard);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return (Boolean) clientAction.getObject();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * Notify to the player that he has won
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void youWon() throws ServerException {
        socketClientHandler.sendObject(new ServerAction(NetworkProtocol.YOU_WON));
    }

    /**
     * Notify to the player that he has lost
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void youLost() throws ServerException {
        socketClientHandler.sendObject(new ServerAction(NetworkProtocol.YOU_LOST));
    }

    /**
     * If the player wants to reconnect after he has been disconnected
     * @param field the new field
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void reconnect(Field field) throws ServerException{
        ServerAction serverAction = new ServerAction(NetworkProtocol.RECONNECT);
        serverAction.setFirstObject(field);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Notify to the user that the game is started
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void startGame() throws ServerException {
        socketClientHandler.sendObject(new ServerAction(NetworkProtocol.GAME_STARTED));
    }

    /**
     * When the user activate the leader card "Lorenzo De Medici", ask the user
     * which other leader card he wants to copy
     * @param leaderCards the other leader cards that can be copied
     * @param privateTimeOut the time left to choose
     * @return the leader card the user wants to copy
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public LeaderCard chooseLeaderCardToCopy(List<LeaderCard> leaderCards, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.CHOOSE_LEADER_CARD_TO_COPY);
        serverAction.setFirstObject(leaderCards);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return clientAction.getLeaderCard();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * When the user activate the leader card "Federico da Montefeltro", ask the user
     * which family member he wants to have with a permanent value of 6
     * @param playerFamilyMembers all his family members
     * @param privateTimeOut the time left to choose
     * @return the family member chosen by the user
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public FamilyMember chooseAFamilyMemberToAddValue(List<FamilyMember> playerFamilyMembers, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.CHOOSE_FAMILY_TO_ADD_VALUE);
        serverAction.setFirstObject(playerFamilyMembers);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return clientAction.getFamilyMember();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * If an user tries to connect to the server with an username associated with a player
     * who is already online
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendAlreadyOnlineWarning() throws ServerException {
        socketClientHandler.sendObject(new ServerAction(NetworkProtocol.ALREADY_ONLINE));
    }

    /**
     * Ask the player, after the starting of a production, if he has decided to activate
     * a yellow card which has two different yellowRewards
     * @param yellowCard the yellow rewards
     * @param privateTimeOut the time left to choose
     * @return the yellowReward chosen by the user
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public YellowReward chooseRewardToProduce(YellowCard yellowCard, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.CHOOSE_REWARD_TO_PRODUCE);
        serverAction.setFirstObject(yellowCard);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return (YellowReward) clientAction.getObject();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }

    /**
     * At the end of the game, send the statistics of all the players in the game
     * @param ranking the statistics
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendRanking(String ranking) throws ServerException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.RANKING);
        serverAction.setFirstObject(ranking);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Send a message to the player
     * @param message the message to send
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendMessage(String message) throws ServerException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.MESSAGE);
        serverAction.setFirstObject(message);
        socketClientHandler.sendObject(serverAction);
    }

    /**
     * Ask the user which cost he wants to pay if choose to pick a card with two different
     * costs
     * @param costs the costs he can pay
     * @param privateTimeOut the time left to choose
     * @return the cost chosen by the user
     * @throws ServerException if the player is disconnected
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public Cost chooseCost(List<Cost> costs, int privateTimeOut) throws ServerException, TimeOutException {
        ServerAction serverAction = new ServerAction(NetworkProtocol.CHOOSE_COST);
        serverAction.setFirstObject(costs);
        serverAction.setTimeout(privateTimeOut);
        socketClientHandler.sendObject(serverAction);
        try {
            ClientAction clientAction = (ClientAction) socketClientHandler.readObject(privateTimeOut, NORMAL_SCALE_FOR_SECONDS);
            return (Cost) clientAction.getObject();
        } catch (SocketTimeoutException e) {
            throw new TimeOutException(e);
        }
    }
}
