package network.server.remotePlayer;

import model.players.FamilyMember;
import model.players.Player;
import controller.exceptions.ServerException;
import controller.exceptions.TimeOutException;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.gameboard.*;
import network.protocol.ClientAction;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;
import java.util.Set;

/**
 * The class responsible to communicate with the client
 */
public abstract class RemotePlayer implements Remote, Serializable{
    private String username;
    private boolean offline;

    public RemotePlayer(String username){
        this.username = username;
    }

    public abstract void sendMessage(String message) throws ServerException;

    public String getUsername(){
        return username;
    }

    public abstract void isYourTurn() throws ServerException;

    public abstract void sendField(Field field) throws ServerException;

    public abstract ClientAction whatAction(String[] actionList, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract void sendMarketAreaUpdate(MarketArea marketArea) throws ServerException;

    public abstract void sendHarvestAreaUpdate(HarvestArea harvestArea) throws ServerException;

    public abstract void sendProductionArea(ProductionArea productionArea) throws ServerException;

    public abstract void sendTowersUpdate(List<Tower> towers) throws ServerException;

    public abstract void sendCouncilAreaUpdate(CouncilArea councilArea) throws ServerException;

    public abstract void sendPlayerStatus(List<Player> player) throws ServerException;

    public abstract void isNotYourTurn() throws ServerException;

    public abstract BonusTile chooseBonusTile(List<BonusTile> bonusTiles, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract Cost chooseCost(List<Cost> costs, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract List<Resources> convertCouncilPrivilege(int councilPrivilege, List<Resources> councilPrivilegeToResources, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract void sendPositions(Set keySet) throws ServerException;

    public abstract String chooseInstantRewardPosition(Set<String> strings, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract List<YellowCard> chooseProductionCards(List<YellowCard> cards, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract LeaderCard draftLeaderCard(List<LeaderCard> leaderCardList, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract boolean showSupport(ExcommunicationCard excommunicationCard, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract void youWon() throws ServerException;

    public abstract void youLost() throws ServerException;

    public abstract void reconnect(Field field) throws ServerException;

    public abstract void startGame() throws ServerException;

    public boolean isOffline(){
        return offline;
    }
    public void setOffline(boolean value){
        offline = value;
    }

    public abstract LeaderCard chooseLeaderCardToCopy(List<LeaderCard> leaderCards, int privateTimeOut) throws ServerException, TimeOutException;


    public abstract FamilyMember chooseAFamilyMemberToAddValue(List<FamilyMember> playerFamilyMembers, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract void sendAlreadyOnlineWarning() throws ServerException;

    public abstract YellowReward chooseRewardToProduce(YellowCard yellowCard, int privateTimeOut) throws ServerException, TimeOutException;

    public abstract void sendRanking(String ranking) throws ServerException;
}
