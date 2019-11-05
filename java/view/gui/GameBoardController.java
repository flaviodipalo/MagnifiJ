package view.gui;

import controller.Actions;
import model.field.*;
import model.players.Player;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.cards.developmentcards.Card;
import model.cards.developmentcards.Cost;
import model.cards.developmentcards.Resources;
import model.cards.developmentcards.YellowCard;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.dices.Dice;
import model.gameboard.*;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import util.ColorConverter;
import view.Gui;
import view.MainApp;
import view.cli.Display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the GUI controller of the GameLayout
 */
public class GameBoardController {
    private static final String BONUS_TILE_PATH = "/pictures/bonus_tiles/personalbonustile_";
    private Field field;
    private Gui gui;


    private boolean yourTurn;

    @FXML
    private AnchorPane councilField;

    @FXML
    private AnchorPane leaderCards;

    @FXML
    private StackPane bonusTile;

    @FXML
    private Label blackDice;
    @FXML
    private Label orangeDice;
    @FXML
    private Label whiteDice;

    @FXML
    private Label servantQty;
    @FXML
    private Label stoneQty;
    @FXML
    private Label woodQty;
    @FXML
    private Label coinQty;
    @FXML
    private Label faithQty;
    @FXML
    private Label militaryQty;
    @FXML
    private Label victoryQty;

    @FXML
    private TextArea boxMessage;

    @FXML
    private AnchorPane greenTower;
    @FXML
    private AnchorPane blueTower;
    @FXML
    private AnchorPane yellowTower;
    @FXML
    private AnchorPane purpleTower;

    @FXML
    private AnchorPane greenPositionsPane;
    @FXML
    private AnchorPane bluePositionsPane;
    @FXML
    private AnchorPane yellowPositionsPane;
    @FXML
    private AnchorPane purplePositionsPane;

    @FXML
    private AnchorPane greenPunchBoard;
    @FXML
    private AnchorPane bluePunchBoard;
    @FXML
    private AnchorPane yellowPunchBoard;
    @FXML
    private AnchorPane purplePunchBoard;
    @FXML
    private AnchorPane market;

    @FXML
    private AnchorPane playersOrderPane;

    @FXML
    private AnchorPane productionZone;

    @FXML
    private AnchorPane harvestZone;

    @FXML
    private AnchorPane excommunications;

    @FXML
    private Label instantRewardLabel;

    @FXML
    private AnchorPane playerExPane;





    private Player player;
    private Stage familyStage;
    private Stage chooseCardsStage;

    private List<Player> players;

    private boolean placeFamilyMember;
    private boolean activateLeaderCard;
    private boolean sellLeaderCard;
    private boolean instantRewardPick;

    private String position;
    private String action;
    private Map<String, ActionSetter> availableActions;
    private Map<String, ZoneSetter> availableZones;
    private boolean popup;
    private static final String ENTERED = "-fx-border-color: blue";

    /**
     * Called as soon as the game board is shown.
     */
    public void init(){
        initZoneMap();
        initActionsMap();
        initZones();
    }


    /**
     * Initialize the action hash map to activate the correct gameb oard zones
     */
    private void initActionsMap(){
        availableActions = new HashMap<>();
        availableActions.put(Actions.PLACE_FAMILY_MEMBERS, this::setPlaceFamilyMember);
        availableActions.put(Actions.ACTIVATE_LEADER_CARDS, this::setActivateLeaderCard);
        availableActions.put(Actions.SELL_LEADER_CARDS, this::setSellLeaderCard);
        availableActions.put(Actions.PASS_TURN, this::setYourTurn);
    }

    /**
     * initialize hash map for the game board areas
     */
    private void initZoneMap(){
        availableZones = new HashMap<>();
        availableZones.put(Actions.GREEN_1, this::activateGreenPosition);
        availableZones.put(Actions.GREEN_2, this::activateGreenPosition);
        availableZones.put(Actions.GREEN_3, this::activateGreenPosition);
        availableZones.put(Actions.GREEN_4, this::activateGreenPosition);
        availableZones.put(Actions.BLUE_1, this::activateBluePosition);
        availableZones.put(Actions.BLUE_2, this::activateBluePosition);
        availableZones.put(Actions.BLUE_3, this::activateBluePosition);
        availableZones.put(Actions.BLUE_4, this::activateBluePosition);
        availableZones.put(Actions.YELLOW_1, this::activateYellowPosition);
        availableZones.put(Actions.YELLOW_2, this::activateYellowPosition);
        availableZones.put(Actions.YELLOW_3, this::activateYellowPosition);
        availableZones.put(Actions.YELLOW_4, this::activateYellowPosition);
        availableZones.put(Actions.PURPLE_1, this::activatePurplePosition);
        availableZones.put(Actions.PURPLE_2, this::activatePurplePosition);
        availableZones.put(Actions.PURPLE_3, this::activatePurplePosition);
        availableZones.put(Actions.PURPLE_4, this::activatePurplePosition);

        availableZones.put(Actions.MARKET_1, this::activateMarket);
        availableZones.put(Actions.MARKET_2, this::activateMarket);
        availableZones.put(Actions.MARKET_3, this::activateMarket);
        availableZones.put(Actions.MARKET_4, this::activateMarket);

        availableZones.put(Actions.START_COUNCIL, this::activateCouncilZone);
        availableZones.put(Actions.START_HARVEST, this::activateHarvestZone);
        availableZones.put(Actions.START_PRODUCTION, this::activateProductionZone);

    }

    /**
     * activate the council zones
     * @param zone just for log purposes
     * @param set boolean to set
     */
    private void activateCouncilZone(String zone, boolean set){
        Display.log(zone);
        productionZone.setDisable(set);
    }
    /**
     * activate the harvest zones
     * @param zone just for log purposes
     * @param set boolean to set
     */
    private void activateHarvestZone(String zone, boolean set){
        Display.log(zone);
        harvestZone.setDisable(set);

    }
    /**
     * activate the production zones
     * @param zone just for log purposes
     * @param set boolean to set
     */
    private void activateProductionZone(String zone, boolean set){
        Display.log(zone);
        productionZone.setDisable(set);

    }
    /**
     * activate the market zones
     * @param zone to activate
     * @param set boolean to set for activation
     */
    private void activateMarket(String zone, boolean set){
        setZone(zone, market.getChildren(), set);
    }

    /**
     * activate the blue tower zones
     * @param zone to activate
     * @param set boolean to set for activation
     */
    private void activateBluePosition(String zone, boolean set){
        setZone(zone, blueTower.getChildren(), set);
    }
    /**
     * activate the green tower zones
     * @param zone to activate
     * @param set boolean to set for activation
     */
    private void activateGreenPosition(String zone, boolean set){
        setZone(zone, greenTower.getChildren(), set);
    }
    /**
     * activate the purple tower zones
     * @param zone to activate
     * @param set boolean to set for activation
     */
    private void activatePurplePosition(String zone, boolean set){
        setZone(zone, purpleTower.getChildren(),set);
    }
    /**
     * activate the yellow tower zones
     * @param zone to activate
     * @param set boolean to set for activation
     */
    private void activateYellowPosition(String zone, boolean set){
        setZone(zone, yellowTower.getChildren(), set);
    }

    /**
     *
     * disables all the towers
     */
    private void initZones(){
        for(Node node : greenTower.getChildren()){
            node.setDisable(true);
        }
        for(Node node : blueTower.getChildren()){
            node.setDisable(true);
        }
        for(Node node : yellowTower.getChildren()){
            node.setDisable(true);
        }
        for(Node node : purpleTower.getChildren()){
            node.setDisable(true);
        }
        for(Node node : yellowTower.getChildren()){
            node.setDisable(true);
        }

        for(Node node : market.getChildren()){
            node.setDisable(true);
        }


        harvestZone.setDisable(true);
        productionZone.setDisable(true);


    }


    /**
     *
     * @param zone zone of the tower to activate
     * @param tower to activate
     * @param available false if you want to activate the zone
     */
    private void setZone(String zone, ObservableList<Node> tower, boolean available){
        for (Node node : tower){
            if(node.getId().equals(zone)){
                node.setDisable(available);
                return;
            }
        }
    }




    /**
     * Select the chosen card and display the family member popup
     * @param event mouse clicked
     */
    @FXML
    private void onCardClicked(MouseEvent event) {
        StackPane stackPane = (StackPane) event.getSource();
        if(!stackPane.isDisabled() && yourTurn && placeFamilyMember && !popup){
            action = Actions.PLACE_FAMILY_MEMBERS;
            position = stackPane.getId();
            chooseFamilyMember();

        }
        if(!stackPane.isDisabled() && yourTurn && !popup && instantRewardPick){
            ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_INSTANT_REWARD_POSITION);
            clientAction.setPosition(stackPane.getId());
            gui.sendAction(clientAction);
            setInstantRewardPick(false);
            instantRewardLabel.setVisible(false);

        }



    }



    /**
     * place the excommunications on the game board
     */

    private void placeExcommunication(){
        int excommPosition = 0;
        for (int era = 1; era <=3 ; era++, excommPosition++){
            ExcommunicationCard card = field.getExcommunicationCard(era);
            StackPane excommPane = (StackPane) excommunications.getChildren().get(excommPosition);
            ImageView excommImage = (ImageView) excommPane.getChildren().get(0);
            excommImage.setImage(CardImage.placeExcommunication(era, card.getId()));
            excommPane.setId(String.valueOf(era));

        }
    }

    /**
     * As soon as the user goes over with the mouse, this
     * method highlights the field of blue
     * @param event mouse over the field
     */


    @FXML
    private void onExcommEntered(MouseEvent event){
        StackPane pane = (StackPane) event.getSource();
        if(pane.isVisible()){
            pane.setStyle(ENTERED);
        }


    }

    /**
     * As soon as the user clicks over with the mouse, this
     * method displays  text area containing the description
     * of the excommunication.
     * @param event mouse click the field
     */

    @FXML
    private void onExcommClicked(MouseEvent event){
        StackPane pane = (StackPane) event.getSource();
        if(!popup && pane.isVisible()){
            int era = Integer.valueOf(pane.getId());
            String excommDescription = field.getExcommunicationCard(era).getDescription();
            TextArea description= new TextArea(excommDescription);
            description.setPrefSize(300, 300);
            description.setEditable(false);
            description.setWrapText(true);
            Stage stage = new Stage();
            AnchorPane descriptionPane = new AnchorPane();
            descriptionPane.getChildren().add(description);
            Scene scene = new Scene(descriptionPane);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            setPopup(true);
            stage.showAndWait();
            setPopup(false);
        }


    }

    /**
     * Handle market area on click
     * @param event click
     */
    @FXML
    private void onMarketClicked(MouseEvent event){
        StackPane stackPane = (StackPane) event.getSource();
        if(yourTurn &&placeFamilyMember && !stackPane.isDisabled() && !popup){
            action = Actions.PLACE_FAMILY_MEMBERS;
            position = stackPane.getId();
            chooseFamilyMember();
        }
    }

    /**
     * Handle production area on click
     */
    @FXML
    private void onProductionClicked(){
        if(yourTurn && placeFamilyMember && !productionZone.isDisabled() && !popup){
            action = Actions.PLACE_FAMILY_MEMBERS;
            position = Actions.START_PRODUCTION;
            chooseFamilyMember();
        }
    }

    /**
     * Handle harvest area on click
     */
    @FXML
    private void onHarvestClicked(){
        if(yourTurn && placeFamilyMember && !harvestZone.isDisabled() && !popup){
            action = Actions.PLACE_FAMILY_MEMBERS;
            position = Actions.START_HARVEST;
            chooseFamilyMember();
        }
    }

    /**
     * Handle council area on click
     */
    @FXML
    private void onCouncilClicked(){
        if(yourTurn && placeFamilyMember && !popup){
            action = Actions.PLACE_FAMILY_MEMBERS;
            position = Actions.START_COUNCIL;
            chooseFamilyMember();
        }
    }

    /**
     * Highlight the field border of blue
     * @param event mouse enter into the field
     */
    @FXML
    private void onFieldEntered(MouseEvent event){
        Pane pane = (Pane) event.getSource();
        if(yourTurn  && !pane.isDisabled() && !popup){
            pane.setStyle(ENTERED);
        }



    }

    /**
     * Reset the field border
     * @param event mouse exit from field
     */
    @FXML
    private void onFieldExited(MouseEvent event){
        Pane pane = (Pane) event.getSource();
        pane.setStyle("-fx-border-color: transparent");
    }

    /**
     * Handles the pass turn button
     */
    @FXML
    private void onPassTurn(){
        if(!yourTurn){
            boxMessage.appendText("it's not your turn!\n");
        }else if(!popup){
            gui.sendAction(new ClientAction(Actions.PASS_TURN));

        }
    }



    /**
     * handle leader cards on click
     * @param event click
     */
    @FXML
    private void onLeaderClicked(MouseEvent event) {
        StackPane card = (StackPane) event.getSource();
        if(yourTurn && (sellLeaderCard || activateLeaderCard) && !popup) {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/LeaderCardAction.fxml"));
                AnchorPane pane = loader.load();
                LeaderCardActionController controller = loader.getController();
                Stage stage = new Stage();
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setAlwaysOnTop(true);
                setPopup(true);
                controller.init(player.getLeaderCards(), card.getId());
                controller.setGui(gui);
                controller.setStage(stage);

                stage.showAndWait();
                setPopup(false);


            }catch (IOException e){
                Display.println(e);
            }
        }

    }


    @FXML
    private void onLeaderEntered(MouseEvent event){
        StackPane card = (StackPane) event.getSource();
        if(yourTurn && card.isVisible() && !popup){
            card.setStyle(ENTERED);
        }

    }


    /**
     * Display a popup to let the user choose the family member
     */
    private void chooseFamilyMember(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/FamilyMemberPopUp.fxml"));
            AnchorPane familyPopUp = loader.load();
            Scene scene = new Scene(familyPopUp);
            familyStage = new Stage();
            FamilyMemberPopUpController familyController = loader.getController();
            familyController.setGameController(this);
            familyController.setGui(gui);
            familyStage.initStyle(StageStyle.UNDECORATED);
            familyStage.setScene(scene);
            familyStage.setResizable(false);
            familyController.init();
            setPopup(true);
            familyStage.setAlwaysOnTop(true);
            familyStage.showAndWait();
            setPopup(false);
        }catch (IOException e){
            Display.println(e);
        }

    }


    /**
     * update the field
     * @param field sent from the server
     */

    public void setField(Field field){
        this.field = field;
        updatePlayerStatus();
        updateTowers();
        updateMarketPosition();
        updateCouncilArea();
        updateProductionArea();
        updateHarvestArea();
        updateResources();
        updateDices();
        setBonusTile();
        updateLeaderCards();
        placeExcommunication();



    }

    /**
     * Set the chosen bonus tile in the player punch board
     */
    public void setBonusTile(){
        ImageView imageView = (ImageView) bonusTile.getChildren().get(0);
        imageView.setImage(new Image(getClass().getResourceAsStream(BONUS_TILE_PATH + player.getBonusTile().getId() + ".png")));
    }

    /**
     * update the dice labels with the correct value
     */
    private void updateDices(){
        for(Dice dice : field.getDices()){
            switch (dice.getDiceColor().toString()){
                case "orange" :
                    orangeDice.setText(String.valueOf(dice.getDiceValue()));
                    break;
                case "black" :
                    blackDice.setText(String.valueOf(dice.getDiceValue()));
                    break;
                default :
                    whiteDice.setText(String.valueOf(dice.getDiceValue()));
                    break;

            }
        }
    }

    /**
     * Update the player's resources display in the north est area of the game board
     */

    private void updateResources(){
        Resources playerResources = player.getPlayerResources();
        coinQty.setText(String.valueOf(playerResources.getCoins()));
        woodQty.setText(String.valueOf(playerResources.getWood()));
        stoneQty.setText(String.valueOf(playerResources.getStone()));
        servantQty.setText(String.valueOf(playerResources.getServants()));
        faithQty.setText(String.valueOf(playerResources.getFaithPoints()));
        militaryQty.setText(String.valueOf(playerResources.getMilitaryPoints()));
        victoryQty.setText(String.valueOf(playerResources.getVictoryPoints()));


    }

    /**
     * Select the user's player and update the view
     */

    private void updatePlayerStatus(){
        for(Player currentPlayer : field.getPlayersOrder()){
            if(currentPlayer.getUsername().equals(gui.getClient().getUsername())){
                this.player = currentPlayer;
            }
        }
        players = field.getPlayersOrder();
        updatePlayersOrder();
        updateLeaderCards();
        updatePlayerExcommunications();

    }

    /**
     * This method updates the player's excommunications
     * displaying them in the low-right zone of the game board
     */
    private void updatePlayerExcommunications() {
        int i = 0;
        for(ExcommunicationCard card : player.getExcommunicationCards()){
            StackPane pane = (StackPane) playerExPane.getChildren().get(i);
            ImageView exImage = (ImageView) pane.getChildren().get(0);
            exImage.setImage(CardImage.placeExcommunication(card.getPeriod().toInt(), card.getId()));
            pane.setVisible(true);
            i++;
        }
    }

    /**
     * This method set the correct order in the game board.
     * It highlights the player circle with a more evident s
     */
    private void updatePlayersOrder(){
        int i = 0;
        for(Player currentPlayer : players){
            Circle circle = (Circle) playersOrderPane.getChildren().get(i);
            circle.setFill(ColorConverter.toPaint(currentPlayer.getPlayerColor()));
            if(currentPlayer.getPlayerColor().equals(player.getPlayerColor())){
                circle.setStyle("-fx-stroke-width : 3px ");
            }
            else {
                circle.setStyle("-fx-stroke-width : 0px ");
            }
            i++;
        }
    }

    /**
     * This method update the leader cards in the Leader cards tab pane
     */
    private void updateLeaderCards(){
        for (Node node : leaderCards.getChildren()){
            node.setVisible(false);
        }
        int i = 0;
        for (LeaderCard leaderCard : player.getLeaderCards()){
            StackPane card = (StackPane) leaderCards.getChildren().get(i);
            card.setVisible(true);
            ImageView cardImage = (ImageView) card.getChildren().get(0);
            cardImage.setImage(CardImage.placeLeaderCard(leaderCard.getId()));
            card.setId(leaderCard.getId());
            if(leaderCard.isActivated()){
                card.setStyle("-fx-border-color: green");
            }else {
                card.setStyle("-fx-border-color: transparent");
            }
            i++;
        }
    }

    /**
     * Update the development towers with the correct cards
     */

    private void updateTowers(){
        fillTower(field.getGreenTower().getTowerPositions(), greenTower.getChildren(), greenPositionsPane);
        fillTower(field.getBlueTower().getTowerPositions(), blueTower.getChildren(), bluePositionsPane);
        fillTower(field.getYellowTower().getTowerPositions(), yellowTower.getChildren(), yellowPositionsPane);
        fillTower(field.getPurpleTower().getTowerPositions(), purpleTower.getChildren(), purplePositionsPane);

        
    }

    /**
     * Update players data in the game board
     * @param players array list of the players in game
     */
    public void updatePlayers(List<Player> players) {
        field.setPlayersOrder(players);
        updatePlayerStatus();
        updateResources();
        updatePunchBoard();

    }

    /**
     * Update the punch board with the correct images of the cards
     */
    private void updatePunchBoard(){
        int i = 0;
        for (Card card : player.getGreenCards()){
            StackPane cardPane = (StackPane) greenPunchBoard.getChildren().get(i);
            ImageView cardImage = (ImageView) cardPane.getChildren().get(0);
            cardImage.setImage(CardImage.placeDevCard(card.getId()));
            i++;
        }
        i = 0;

        for (Card card : player.getBlueCards()){
            StackPane cardPane = (StackPane) bluePunchBoard.getChildren().get(i);
            ImageView cardImage = (ImageView) cardPane.getChildren().get(0);
            cardImage.setImage(CardImage.placeDevCard(card.getId()));
            i++;
        }

        i = 0;

        for (Card card : player.getYellowCards()){
            StackPane cardPane = (StackPane) yellowPunchBoard.getChildren().get(i);
            ImageView cardImage = (ImageView) cardPane.getChildren().get(0);
            cardImage.setImage(CardImage.placeDevCard(card.getId()));
            i++;
        }
        i = 0;

        for (Card card : player.getPurpleCards()){
            StackPane cardPane = (StackPane) purplePunchBoard.getChildren().get(i);
            ImageView cardImage = (ImageView) cardPane.getChildren().get(0);
            cardImage.setImage(CardImage.placeDevCard(card.getId()));
            i++;
        }



    }


    /**
     * update the development tower during the game.
     * @param towers updated
     */
    public void updateTowers(List<Tower> towers){
        field.setTowers(towers);
        updateTowers();
    }

    /**
     * Fill the towers with the correct cards
     * @param positions of field sent from the server
     * @param tower displayed in the gui
     */
    private void fillTower(ArrayList<TowerPosition> positions, ObservableList<Node> tower, AnchorPane familyPosition){
        int i = 0;
        for(TowerPosition currentPosition : positions){
            StackPane card = (StackPane) tower.get(i);
            ImageView imageView = (ImageView) card.getChildren().get(0);
            if(currentPosition.getCard() != null){
                imageView.setImage(CardImage.placeDevCard(currentPosition.getCard().getId()));
                card.setVisible(true);
                deleteFamilyMemberFromTower(currentPosition, card, familyPosition);
            }else {
                addFamilyMemberToTower(currentPosition, card, familyPosition);

            }
            i++;
        }
    }

    /**
     * This method deletes the family member from the tower after
     * an update sent from the server
     * @param currentPosition position to check
     * @param card to check
     * @param familyPosition to delete if needed
     */

    private void deleteFamilyMemberFromTower(TowerPosition currentPosition, StackPane card, AnchorPane familyPosition) {
        for (Node currentFamilyPosition : familyPosition.getChildren()){
            StackPane current = (StackPane) currentFamilyPosition;
            if(current.getId().equals(card.getId()) && currentPosition.getFamilyMember() == null){
                current.setVisible(false);
            }
        }
    }



    /**
     * Add a family member
     * @param currentPosition  is the position in which you want to added it
     * @param card card (position) next to the family position
     * @param familyPosition position next to the card in which you want to add the family member
     */
    private void addFamilyMemberToTower(TowerPosition currentPosition, StackPane card, AnchorPane familyPosition){
        for(Node currentFamilyPosition : familyPosition.getChildren()){
            StackPane current = (StackPane) currentFamilyPosition;
            if(current.getId().equals(card.getId())){
                placeFamilyMember(currentPosition, current);

            }
        }
        card.setVisible(false);
    }




    /**
     * Display a message in the player console
     * @param message displayed
     */
    public void showMessage(String message){
        boxMessage.appendText(message+"\n");
    }




    /**
     * Activate the available actions
     * @param actionList sent from the server
     */

    public void setActions(String[] actionList) {
        setPlaceFamilyMember(false);
        setSellLeaderCard(false);
        setActivateLeaderCard(false);
        for (String availableAction : actionList){
            availableActions.get(availableAction).set(true);

        }
    }








    /**
     * Display the punch boards of the other players
     */
    @FXML
    private void onViewPlayerStatus(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/PlayersOverview.fxml"));
            AnchorPane playersStatus = loader.load();
            Scene scene = new Scene(playersStatus);
            Stage playersStage = new Stage();
            PlayersOverviewController controller = loader.getController();
            controller.setGameController(this);
            playersStage.setScene(scene);
            playersStage.setResizable(false);
            controller.init();
            playersStage.setAlwaysOnTop(true);
            setPopup(true);
            playersStage.showAndWait();
            setPopup(false);
        }catch (IOException e){
            Display.println(e);
        }

    }


    /**
     * set to available the position sent from the server
     * @param positions to activate
     */
    public void setPositions(String[] positions, boolean activate) {
        for (String positionToActivate : positions){
            if(availableZones.containsKey(positionToActivate)){
                // false -> activate zone
                availableZones.get(positionToActivate).set(positionToActivate, !activate);
            }


        }

    }


    /**
     * This method updates the market area
     * @param marketArea sent from the server
     */
    public void updateMarket(MarketArea marketArea) {
        field.setMarket(marketArea);
        updateMarketPosition();

    }

    /**
     * Handle the update of the market with the correct family members
     */
    private void updateMarketPosition(){
        int i = 0;
        for(MarketPosition marketPosition : field.getMarket().getMarket()){
            StackPane marketField = (StackPane) market.getChildren().get(i);
            if(marketPosition.getFamilyMember()!= null){

                placeFamilyMember(marketPosition, marketField);

            }
            else {
                removeFamilyMember(marketPosition, marketField);
            }
            i++;
        }
    }

    /**
     *
     * @param current position
     * @param field to remove
     */
    private void removeFamilyMember(Position current, StackPane field) {
        if(current.getFamilyMember() == null){
            Circle playerCircle = (Circle) field.getChildren().get(0);
            Circle familyCircle = (Circle) field.getChildren().get(1);
            playerCircle.setFill(Color.TRANSPARENT);
            familyCircle.setFill(Color.TRANSPARENT);

        }
    }

    /**
     * This method fill the position with the family member
     * @param position that contains the family member
     * @param field place to be filled with the family member provided from the position
     */

    private void placeFamilyMember(Position position, Pane field){
        Circle playerCircle = (Circle) field.getChildren().get(0);
        Circle familyCircle = (Circle) field.getChildren().get(1);
        if(position.getFamilyMember() != null){
            playerCircle.setFill(ColorConverter.toPaint(position.getFamilyMember().getPlayerColor()));
            familyCircle.setFill(ColorConverter.toPaint(position.getFamilyMember().getDiceColor()));
            field.setVisible(true);

        }
        else {
            playerCircle.setFill(Color.TRANSPARENT);
            familyCircle.setFill(Color.TRANSPARENT);
            field.setVisible(false);
        }


    }

    /**
     * update the production area with the correct family member
     * @param productionArea sent from the server
     */
    public void setNewProductionArea(ProductionArea productionArea) {
        field.setProduction(productionArea);
        updateProductionArea();

    }

    /**
     * Updates the production area
     */
    private void updateProductionArea(){
        int i = 0;
        for (ProductionPosition productionPosition : field.getProduction().getProduction()){
            StackPane productionField = (StackPane) productionZone.getChildren().get(i);
            placeFamilyMember(productionPosition, productionField);
            i++;
        }
    }

    /**
     * Display a window that contains the production cards that need to be activated
     * @param yellowCards sent from the server
     */

    public void chooseProductionCards(List<YellowCard> yellowCards) {
        ChooseCardsController controller;
        try {

            controller = chooseCards();
            controller.chooseProduction(yellowCards, chooseCardsStage);
            controller.setGui(gui);
            setPopup(true);
            chooseCardsStage.showAndWait();
            setPopup(false);
        } catch (IOException e) {
            Display.println(e);
        }


    }

    /**
     * Sets the new council area and calls the update method
     * @param councilArea sent from the server
     */
    public void setNewCouncilArea(CouncilArea councilArea){
        field.setCouncil(councilArea);
        updateCouncilArea();
    }

    /**
     * Updates the council area
     */
    private void updateCouncilArea(){
        int i = 0;
        for(CouncilPosition councilPosition : field.getCouncil().getCouncilPositions()){
            StackPane currentPosition = (StackPane) councilField.getChildren().get(i);
            placeFamilyMember(councilPosition, currentPosition);
            i++;
        }
    }

    /**
     *
     * @return controller loaded from the fxml file that contains the production cards
     * @throws IOException if the fxml file is not found
     */


    private ChooseCardsController chooseCards() throws IOException{

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ChooseCards.fxml"));
            AnchorPane chooseCards = loader.load();
            chooseCardsStage = new Stage();
            Scene scene = new Scene(chooseCards);
            chooseCardsStage.setScene(scene);
            chooseCardsStage.setResizable(false);
            chooseCardsStage.setAlwaysOnTop(true);


            return loader.getController();




    }

    /**
     * allow the user to choose a card contained
     * in the positions sent from the server
     * @param positions sent from the server
     */

    public void instantRewardPick(String[] positions){
        initZones();
        setPlaceFamilyMember(false);
        setInstantRewardPick(true);
        setPositions(positions, true);
        instantRewardLabel.setVisible(true);



    }

    /**
     * If asked from the server, display a window in order to let the player
     * choose the cost of the card
     * @param costs to choose
     */
    public void chooseCost(List<Cost> costs) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ChooseCosts.fxml"));
            AnchorPane chooseCosts = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(chooseCosts);
            stage.setScene(scene);
            stage.setResizable(false);

            ChooseCostsController controller = loader.getController();

            controller.setGui(gui);
            controller.init(costs, player.getPlayerResources(), stage);
            setPopup(true);
            stage.showAndWait();
            setPopup(false);

        }catch (IOException e){
            Display.println(e);
        }
    }

    /**
     * This method update the harvest area filling it with the correct family members
     * @param harvestArea sent from the server
     */

    public void setNewHarvestArea(HarvestArea harvestArea) {
        field.setHarvest(harvestArea);
        updateHarvestArea();

    }

    /**
     * THis method update the harvest area
     */
    private void updateHarvestArea(){
        int i = 0;
        for (HarvestPosition harvestPosition : field.getHarvest().getHarvest()){
            StackPane pos = (StackPane) harvestZone.getChildren().get(i);
            placeFamilyMember(harvestPosition, pos);
            i++;
        }
    }

    /**
     * Display a windows to let the player choose the resources
     * @param resources resources sent from the server
     * @param nPrivilegesToConvert decided from the game controller
     */

    public void convertCouncilPrivileges(List<Resources> resources, int nPrivilegesToConvert) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CouncilPrivilege.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            CouncilPrivilegeController controller = loader.getController();
            controller.setGui(gui);
            controller.setStage(stage);
            controller.setResources(resources, nPrivilegesToConvert);


            setPopup(true);
            stage.showAndWait();
            setPopup(false);
        }catch (IOException e){
            Display.println(e);
        }
    }

    public void setPopup(boolean value){
        popup = value;
    }

    public void setInstantRewardPick(boolean instantRewardPick) {
        this.instantRewardPick = instantRewardPick;
    }


    public void setGui(Gui gui) {
        this.gui = gui;
    }


    public String getPosition() {
        return position;
    }

    public String getAction() {
        return action;
    }

    public Player getPlayer() {
        return player;
    }

    public Stage getFamilyStage() {
        return familyStage;
    }

    public void setPlaceFamilyMember(boolean value){
        placeFamilyMember = value;
        councilField.setDisable(false);
    }
    public void setActivateLeaderCard(boolean value){
        activateLeaderCard = value;
    }
    public void setSellLeaderCard(boolean value){
        sellLeaderCard = value;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    @FunctionalInterface
    private interface ZoneSetter {
        void set(String zone, boolean disable);
    }
    @FunctionalInterface
    private interface ActionSetter{
        void set(boolean value);
    }





}
