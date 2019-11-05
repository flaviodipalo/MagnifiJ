package view;

import controller.exceptions.ServerException;
import model.players.FamilyMember;
import model.players.Player;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.cards.developmentcards.BonusTile;
import model.cards.developmentcards.Cost;
import model.cards.developmentcards.Resources;
import model.cards.developmentcards.YellowCard;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.gameboard.*;
import view.cli.Display;
import view.gui.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This is the GUI of the game. It provides methods to interact with the user
 */
public class Gui extends ControllerUI{
    private transient MainApp mainApp;
    private transient GameBoardController gameController;
    private transient LauncherOverviewController launcherController;
    private boolean gameStarted;
    private Client client;
    private Gui thisGui;
    private String result;
    private String ranking;


    public Gui(MainApp mainApp){
        this.mainApp = mainApp;
        this.launcherController = mainApp.getLauncherController();
        thisGui = this;
        mainApp.setGui(this);

    }


    /**
     * Display the message in the launcher view if the game has not started yet
     * otherwise in the designed game board text area
     * @param message received from the server.
     */
    @Override
    public void showMessage(String message) {
        if(!gameStarted){
            launcherController.showMessage(message);
        }else {
            gameController.showMessage(message);
            gameController.setGui(this);
        }


    }


    /**
     * Wait for the completion of the game board loading.
     * Attempt to set the game controller. This function is called several times
     * because initialization of the game layout and its controller need time to complete.
     */
    private void loadingGUI(){
        Display.println("loading...");
        gameController = mainApp.getGameController();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Display.println(e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Activates the
     * @param field updated
     */
    @Override
    public void setField(Field field) {
        Platform.runLater(() -> gameController.setField(field));

    }

    /**
     *
     * @param actionList sent by the server
     */
    @Override
    public void setActions(String[] actionList) {
        gameController.setActions(actionList);

    }

    /**
     * This method is used to send the action to the server
     * @param action to send
     */
    public void sendAction(Object action){
        try {
            if(canDoAction()){
                    client.sendAction(action);
            }


        } catch (ServerException e) {
            Display.println("Connection lost!" , e);
            Platform.runLater(() -> Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.ERROR,
                    "Connection Lost!", "Close and restart the app, you will continue" +
                            "to play!"));

        }
    }


    /**
     * activates positions
     * @param positions to activate
     */
    @Override
    public void setPositions(String[] positions) {
        Platform.runLater(() -> gameController.setPositions(positions, true));

    }

    /**
     * disable all the action the player can make
     * @param code message sent
     */
    @Override
    public void notYourTurn(String code) {
        Platform.runLater(() -> {

                if(gameController != null){
                    gameController.setYourTurn(false);
                    gameController.showMessage(code);
                    gameController.setPlaceFamilyMember(false);
                    gameController.setSellLeaderCard(false);
                    gameController.setActivateLeaderCard(false);
                }

            });
    }

    @Override
    public void init() {
        // not needed for the gui
    }


    /**
     * Display council privilege popup
     * @param numberOfPrivileges to convert
     * @param resources to choose
     */
    @Override
    public void convertCouncilPrivileges(int numberOfPrivileges, List<Resources> resources) {
        Platform.runLater(() -> gameController.convertCouncilPrivileges(resources, numberOfPrivileges));
    }

    /**
     * Update players data in the game board
     * @param players array list of the players in game
     */
    @Override
    public void setPlayerStatus(List<Player> players) {
        Platform.runLater(() -> gameController.updatePlayers(players));
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
    /**
     * Sets the new council area and calls the update method
     * @param councilArea sent from the server
     */
    @Override
    public void updateCouncilArea(CouncilArea councilArea) {
        Platform.runLater(() -> gameController.setNewCouncilArea(councilArea));
    }
    /**
     * This method updates the market area
     * @param marketArea sent from the server
     */
    @Override
    public void updateMarketArea(MarketArea marketArea) {
        Platform.runLater(() -> gameController.updateMarket(marketArea));
    }
    /**
     * This method update the harvest area filling it with the correct family members
     * @param harvestArea sent from the server
     */
    @Override
    public void updateHarvestArea(HarvestArea harvestArea) {
        Platform.runLater(() -> gameController.setNewHarvestArea(harvestArea));
    }
    /**
     * update the production area with the correct family member
     * @param productionArea sent from the server
     */
    @Override
    public void updateProductionArea(ProductionArea productionArea) {
        Platform.runLater(() -> gameController.setNewProductionArea(productionArea));
    }
    /**
     * update the development tower during the game.
     * @param towers updated
     */
    @Override
    public void updateTowers(List<Tower> towers) {
        Platform.runLater(() -> gameController.updateTowers(towers));


    }
    /**
     * Let the player to choose the bonus tile
     * @param bonusTiles list
     */
    @Override
    public void chooseBonusTile(List<BonusTile> bonusTiles) {
        Platform.runLater(() -> mainApp.showBonusTiles(bonusTiles));

    }
    /**
     * Let the player choose the leader cards
     * @param leaderCards list
     */
    @Override
    public void draftLeaderCards(List<LeaderCard> leaderCards){
        Platform.runLater(() -> mainApp.showDraftLeaderCards(leaderCards));

    }
    /**
     * Display a window that contains the production cards that need to be activated
     * @param yellowCards sent from the server
     */
    @Override
    public void chooseProductionCards(List<YellowCard> yellowCards) {
        Platform.runLater(() -> gameController.chooseProductionCards(yellowCards));
    }

    /**
     * Allows the player to reconnect to the game he left
     * @param field sent from the server
     */
    @Override
    public void reconnect(Field field) {
        Platform.runLater(mainApp::showGameLayout);
        while (gameController == null) {
            loadingGUI();
        }
        gameController.setGui(this);
        setField(field);



    }

    /**
     * Init the game
     * @param firstObject object sent from the server
     */
    @Override
    public void startGame(Object firstObject) {
        Platform.runLater(mainApp::showGameLayout);
        while (gameController == null) {
            loadingGUI();
        }
        gameStarted = true;


    }

    /**
     * Inform the user that it is his turn
     * @param code message
     */
    @Override
    public void yourTurn(String code) {
        if(gameController != null){
            Platform.runLater(() -> {
                gameController.showMessage(code);
                gameController.setYourTurn(true);
            });
        }


    }

    /**
     * Allows the user choose a leader card to copy between the
     * ones sent from the server
     * @param card sent from the server
     */
    @Override
    public void chooseLeaderCardToCopy(List<LeaderCard> card) {
        Platform.runLater(() -> {

                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxml/ChooseLeaderCardToCopy.fxml"));
                    Stage stage = new Stage();
                    stage.setScene(new Scene(loader.load()));
                    stage.setResizable(false);
                    stage.setAlwaysOnTop(true);
                    ChooseLeaderToCopyController controller = loader.getController();
                    controller.setLeaderCards(card);
                    controller.setGui(thisGui);
                    controller.setStage(stage);
                    gameController.setPopup(true);
                    stage.showAndWait();
                    gameController.setPopup(false);
                } catch (IOException e) {
                    Display.println(e);
                }

            });

    }

    /**
     * This class allows the user to choose one family member
     * that will gain a dice value of six. it's called due an activation
     * of a leader card.
     * @param familyMembers list sent from the server
     */

    @Override
    public void chooseFamilyMemberToAddValue(List<FamilyMember> familyMembers){
        Platform.runLater(() -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/fxml/ChooseFamilyFedericoMontefeltro.fxml"));
                    Stage stage = new Stage();
                    stage.setScene(new Scene(fxmlLoader.load()));
                    stage.setResizable(false);
                    stage.setAlwaysOnTop(true);

                    ChooseFamilyMemberToAddValueController controller = fxmlLoader.getController();
                    controller.setFamilyMembers(familyMembers);
                    controller.setStage(stage);
                    controller.setGui(thisGui);
                    gameController.setPopup(true);
                    stage.showAndWait();
                    gameController.setPopup(false);
                }catch (IOException e){
                    Display.println(e);
                }

        });
    }

    /**
     * Display a warning if the user is already online
     * @param code message
     */

    @Override
    public void alreadyOnlineWarning(String code) {
        Platform.runLater(() -> Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.ERROR, code));
    }

    /**
     * @param positions to choose
     */
    @Override
    public void chooseInstantRewardPosition(String[] positions){
        Platform.runLater(() -> gameController.instantRewardPick(positions));
    }

    /**
     * This class display to the user the card he has to choose the reward when he
     * activates the production
     */
    @Override
    public void chooseRewardToProduce(YellowCard yellowCard) {
        Platform.runLater(() -> {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(MainApp.class.getResource("/fxml/ChooseRewardToProduce.fxml"));
                AnchorPane pane = fxmlLoader.load();
                Scene scene = new Scene(pane);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);

                ChooseRewardToProduceController controller = fxmlLoader.getController();
                controller.setGui(thisGui);
                controller.setStage(stage);
                controller.setYellowCard(yellowCard);

                gameController.setPopup(true);
                stage.showAndWait();
                gameController.setPopup(false);

            } catch (IOException e) {
                Display.println(e);
            }

        });

    }

    /**
     * Set the result of the game
     * @param code result
     */
    @Override
    public void youWon(String code) {
        gameController.setYourTurn(false);
        this.result = code;

    }
    /**
     * Set the result of the game
     * @param code result
     */
    @Override
    public void youLost(String code) {
        gameController.setYourTurn(false);
        this.result = code;

    }

    /**
     * Display at the end of the game a text area that contains
     *
     * @param ranking string sent from the server
     */
    @Override
    public void showRanking(String ranking) {
        this.ranking = ranking;
        Platform.runLater(mainApp::showRankings);

    }

    /**
     * Inform the user of the time remaining
     * @param timeLeft before change of turn
     */
    @Override
    public void showTimeLeft(int timeLeft) {
        Platform.runLater(() -> {
            if(gameController == null){
                launcherController.showMessage(String.valueOf(timeLeft) +
                        " seconds left");
            }else {
                gameController.showMessage(String.valueOf(timeLeft) + " " +
                        "seconds left to complete the action");
            }

        });
    }

    /**
     * query the user if he wants to support the church or not
     * @param card excommunication card
     */
    @Override
    public void onShowSupport(ExcommunicationCard card){
        Platform.runLater(() ->{
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxml/ShowSupport.fxml"));
                    AnchorPane pane = loader.load();
                    Stage stage = new Stage();
                    Scene scene = new Scene(pane);
                    stage.setScene(scene);
                    ShowSupportController controller = loader.getController();
                    controller.setGui(thisGui);
                    controller.setStage(stage);
                    controller.placeExcommunicationCard(card, gameController.getPlayer().getExcommunicationCards().size());

                    gameController.setPopup(true);
                    stage.setResizable(false);
                    stage.setAlwaysOnTop(true);
                    stage.showAndWait();
                    gameController.setPopup(false);

                } catch (IOException e) {
                    Display.println(e);
                }
            });

    }

    /**
     * If asked from the server, display a window in order to let the player
     * choose the cost of the card
     * @param costs to choose
     */
    @Override
    public void chooseCost(List<Cost> costs) {
        Platform.runLater(() -> gameController.chooseCost(costs));

    }

    public Client getClient() {
        return client;
    }

    public String getResult() {
        return result;
    }

    public String getRanking() {
        return ranking;
    }
}
