package network.server.rmi;

import model.field.*;
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
import network.client.rmi.RMIClient;
import network.client.rmi.RMIClientInterface;
import network.server.remotePlayer.RemotePlayer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

/**
 * An intermediate class between the controller and a player connected with RMI
 * which represent the player himself
 */
public class RMIRemotePlayer extends RemotePlayer {
    private RMIClientInterface rmiClient;

    /**
     * Instantiate the RMIRemotePlayer
     * @param username the username of the player
     * @param rmiClient the RMIClient associated with it
     */
    public RMIRemotePlayer(String username, RMIClient rmiClient) {
        super(username);
        this.rmiClient = rmiClient;
    }

    /**
     * Send a message to the player
     * @param message the message to send
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendMessage(String message) throws ServerException {
        try {
            rmiClient.onMessage(message);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Notify to the player that it is his turn
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void isYourTurn() throws ServerException {
        try {
            rmiClient.isYourTurn(new ServerAction(NetworkProtocol.YOUR_TURN));
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Send the field to the player
     * @param field the field to send
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendField(Field field) throws ServerException {
        try {
            rmiClient.onField(field);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
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
        try {
            rmiClient.onAction(actionList, privateTimeOut);
            return (ClientAction) rmiClient.takeObjectFromQueue();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Send to the player the update for the market area
     * @param marketArea the new market area
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendMarketAreaUpdate(MarketArea marketArea) throws ServerException {
        try {
            rmiClient.sendMarketUpdate(marketArea);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Send to the player the update for the harvest area
     * @param harvestArea the new harvest area
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendHarvestAreaUpdate(HarvestArea harvestArea) throws ServerException {
        try {
            rmiClient.sendHarvestUpdate(harvestArea);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Send to the player the update of the production area
     * @param productionArea the new production area
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendProductionArea(ProductionArea productionArea) throws ServerException {
        try {
            rmiClient.sendProductionUpdate(productionArea);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Send to the player the update of the towers
     * @param towers the new towers
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendTowersUpdate(List<Tower> towers) throws ServerException {
        try {
            rmiClient.sendTowersUpdate(towers);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Send to the player the update of the council area
     * @param councilArea the new council area
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendCouncilAreaUpdate(CouncilArea councilArea) throws ServerException {
        try {
            rmiClient.sendCouncilUpdate(councilArea);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Send to the player the update of the status of all the model.players
     * @param players the new status of the model.players
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendPlayerStatus(List<Player> players) throws ServerException {
        try {
            rmiClient.onPlayerStatus(players);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Notify the player that is not his turn
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void isNotYourTurn() throws ServerException {
        try {
            rmiClient.isNotYourTurn(new ServerAction(NetworkProtocol.NOT_YOUR_TURN));
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
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
        try {
            rmiClient.onChooseBonusTile(bonusTiles, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return (BonusTile) clientAction.getObject();
        }catch (RemoteException e) {
            throw new ServerException(e);
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
    public List<Resources> convertCouncilPrivilege(int councilPrivilege, List<Resources> councilPrivilegeToResources, int privateTimeOut) throws ServerException, TimeOutException {
        try{
            return rmiClient.onConvertCouncilPrivilege(councilPrivilege, councilPrivilegeToResources, privateTimeOut);
        }catch(RemoteException | InterruptedException e){
            throw new ServerException(e);
        }
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
    public  Cost chooseCost(List<Cost> costs, int privateTimeOut) throws ServerException, TimeOutException{
        try {
            rmiClient.chooseCost(costs, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return (Cost) clientAction.getObject();
        } catch (RemoteException e) {
            throw new ServerException(e);
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
        try {
            rmiClient.setPositions(positions);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
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
        String[] positions = strings.toArray(new String[strings.size()]);
        try {
            rmiClient.chooseInstantRewardPosition(positions, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return clientAction.getPosition();
        } catch (RemoteException e) {
            throw new ServerException(e);
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
    @SuppressWarnings("unchecked")
    @Override
    public List<YellowCard> chooseProductionCards(List<YellowCard> cards, int privateTimeOut) throws ServerException, TimeOutException {
        try {
            rmiClient.chooseProductionCards(cards, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return (List<YellowCard>) clientAction.getObjects();
        } catch (IOException e) {
            throw new ServerException(e);
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
        try {
            rmiClient.onDraftLeaderCards(leaderCardList, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return clientAction.getLeaderCard();
        } catch (RemoteException e) {
            throw new ServerException(e);
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
        try {
            rmiClient.showSupport(excommunicationCard, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return (boolean) clientAction.getObject();
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Notify to the player that he has won
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void youWon() throws ServerException {
        try {
            rmiClient.youWon();
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Notify to the player that he has lost
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void youLost() throws ServerException {
        try {
            rmiClient.youLost();
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * If the player wants to reconnect after he has been disconnected
     * @param field the new field
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void reconnect(Field field) throws ServerException{
        try {
            rmiClient.reconnect(field);
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Notify to the user that the game is started
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void startGame() throws ServerException {
        try {
            rmiClient.startGame();
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * If an user tries to connect to the server with an username associated with a player
     * who is already online
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendAlreadyOnlineWarning() throws ServerException {
        try {
            rmiClient.onAlreadyOnlineUsers();
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
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
        try {
            rmiClient.chooseRewardToProduce(yellowCard, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return (YellowReward) clientAction.getObject();
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

    /**
     * At the end of the game, send the statistics of all the players in the game
     * @param ranking the statistics
     * @throws ServerException if the player is disconnected
     */
    @Override
    public void sendRanking(String ranking) throws ServerException {
        try{
            rmiClient.sendRanking(ranking);
        }catch(RemoteException e){
            throw new ServerException(e);
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
        try {
            rmiClient.chooseAFamilyMemberToAddValue(playerFamilyMembers, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return clientAction.getFamilyMember();
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
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
        try {
            rmiClient.chooseLeaderCardToCopy(leaderCards, privateTimeOut);
            ClientAction clientAction = (ClientAction) rmiClient.takeObjectFromQueue();
            return clientAction.getLeaderCard();
        } catch (RemoteException e) {
            throw new ServerException(e);
        }
    }

}
