package view;

import controller.exceptions.TimeOutException;
import model.cards.developmentcards.BonusTile;
import model.cards.developmentcards.Cost;
import model.cards.developmentcards.Resources;
import model.cards.developmentcards.YellowCard;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.gameboard.*;
import model.players.FamilyMember;
import model.players.Player;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class contains all the methods implemented by the CLI and GUI.
 *
 */
public abstract class ControllerUI implements Serializable{
    private transient Timer timer;
    private int timeLeft;
    private boolean canDoAction;
    /**
     * Display to the user interface the info message receive from the server.
     * @param message received from the server.
     */
    public abstract void showMessage(String message);

    /**
     * update the player's field
     * @param field updated
     */
    public abstract void setField(Field field);

    /**
     * Display to the user a list of the possible actions to be chosen.
     * @param actionList sent by the server
     */

    public abstract void setActions(String[] actionList) throws TimeOutException;

    public abstract void setPositions(String[] positions);

    public abstract void notYourTurn(String code);

    public abstract void init();

    public abstract void convertCouncilPrivileges(int numberOfPrivileges, List<Resources> resources) throws TimeOutException;

    public abstract void setPlayerStatus(List<Player> players);

    public abstract void setClient(Client client);

    public abstract void updateCouncilArea(CouncilArea councilArea);

    public abstract void updateMarketArea(MarketArea marketArea);

    public abstract void updateHarvestArea(HarvestArea harvestArea);

    public abstract void updateProductionArea(ProductionArea productionArea);

    public abstract void updateTowers(List<Tower> towers);

    public abstract void onShowSupport(ExcommunicationCard card) throws TimeOutException;

    public abstract void chooseCost(List<Cost> costs) throws TimeOutException;

    public abstract void chooseBonusTile(List<BonusTile> bonusTiles) throws TimeOutException;

    public abstract void draftLeaderCards(List<LeaderCard> leaderCards) throws TimeOutException;

    public abstract void chooseProductionCards(List<YellowCard> yellowCards) throws TimeOutException;

    public abstract void reconnect(Field field);

    public abstract void startGame(Object firstObject);

    public abstract void yourTurn(String code);

    public abstract void chooseLeaderCardToCopy(List<LeaderCard> card) throws TimeOutException;

    public abstract void chooseFamilyMemberToAddValue(List<FamilyMember> familyMembers) throws TimeOutException;

    public abstract void alreadyOnlineWarning(String code);

    public abstract void chooseInstantRewardPosition(String[] positions) throws TimeOutException;

    public abstract void chooseRewardToProduce(YellowCard yellowRewards) throws TimeOutException;

    public abstract void youWon(String code);

    public abstract void youLost(String s) ;

    public abstract void showTimeLeft(int timeLeft);

    /**
     * Start the timer on the client side
     */
    public void startTimer() {
        int delay = 0;
        int period = 1000;
        if(timer != null)
            timer.cancel();
        timer = new Timer();
        canDoAction = true;
        showTimeLeft(timeLeft);
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                timeLeft = setInterval(timeLeft);
            }
        }, delay, period);
    }

    /**
     * The thread which is going to decrease the timeout
     * @param timeOut time left
     * @return timeOut - 1
     */
    private int setInterval(int timeOut){
        int timerTemp = timeOut;
        if(timeOut <= 0) {
            cancelTimeout();
            canDoAction = false;
        }
        return --timerTemp;
    }

    protected void cancelTimeout(){
        if(this.timer != null)
            timer.cancel();
    }

    /**
     * Set the timeLeft
     * @param timeLeft the new timeLeft
     */
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public abstract void showRanking(String ranking);

    protected boolean canDoAction() {
        return canDoAction;
    }
}
