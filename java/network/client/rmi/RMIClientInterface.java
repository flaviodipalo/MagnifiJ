package network.client.rmi;

import controller.exceptions.TimeOutException;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.players.FamilyMember;
import model.players.Player;
import model.gameboard.*;
import network.protocol.ServerAction;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface contains all the methods implemented by the RMIClient class
 */
public interface RMIClientInterface extends Remote, Serializable {

    void onMessage(String message) throws RemoteException;

    void onAction(String[] actionList, int privateTimeOut) throws IOException, TimeOutException;

    void isYourTurn(ServerAction serverAction)throws RemoteException;

    void isNotYourTurn(ServerAction serverAction) throws RemoteException;

    void onField(Field field)throws RemoteException;

    void onPlayerStatus(List<Player> player) throws RemoteException;

    ArrayList<Resources> onConvertCouncilPrivilege(int councilPrivilege, List<Resources> councilPrivilegeToResources, int privateTimeOut) throws RemoteException, InterruptedException, TimeOutException;

    void onChooseBonusTile(List<BonusTile> bonusTiles, int privateTimeOut) throws RemoteException, TimeOutException;

    void onDraftLeaderCards(List<LeaderCard> leaderCardList, int privateTimeOut) throws RemoteException, TimeOutException;

    Object takeObjectFromQueue() throws RemoteException, TimeOutException;

    void setPositions(String[] positions) throws RemoteException;

    void chooseProductionCards(List<YellowCard> cards, int privateTimeOut) throws RemoteException, TimeOutException;

    void chooseCost(List<Cost> costs, int privateTimeOut) throws RemoteException, TimeOutException;

    void sendTowersUpdate(List<Tower> towers) throws RemoteException;

    void sendMarketUpdate(MarketArea marketArea) throws RemoteException;

    void sendHarvestUpdate(HarvestArea harvestArea) throws RemoteException;

    void sendProductionUpdate(ProductionArea productionArea) throws RemoteException;

    void sendCouncilUpdate(CouncilArea councilArea) throws RemoteException;

    void reconnect(Field field) throws RemoteException;

    void showSupport(ExcommunicationCard excommunicationCard, int privateTimeOut) throws RemoteException, TimeOutException;

    void startGame() throws RemoteException;

    void onAlreadyOnlineUsers() throws RemoteException;

    void chooseLeaderCardToCopy(List<LeaderCard> leaderCards, int privateTimeOut) throws RemoteException, TimeOutException;

    void chooseAFamilyMemberToAddValue(List<FamilyMember> playerFamilyMembers, int privateTimeOut) throws RemoteException, TimeOutException;

    void chooseInstantRewardPosition(String[] positions, int privateTimeout) throws RemoteException, TimeOutException;

    void chooseRewardToProduce(YellowCard yellowCard, int privateTimeout) throws RemoteException, TimeOutException;

    void youWon() throws RemoteException;

    void youLost() throws RemoteException;

    void sendRanking(String ranking) throws RemoteException;
}
