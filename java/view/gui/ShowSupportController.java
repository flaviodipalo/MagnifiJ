package view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.cards.excommunicationcards.ExcommunicationCard;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;

/**
 * This Class is used to query the user if he wants to support
 * the church or not
 */
public class ShowSupportController {

    @FXML
    private StackPane cardPane;

    @FXML
    private TextArea descriptionPane;
    @FXML
    private StackPane lackOfFaith;

    private boolean response;



    private ExcommunicationCard card;
    private Gui gui;
    private Stage stage;
    private int excommunicationTimes;


    public void placeExcommunicationCard(ExcommunicationCard card, int excommunicationTimes){
        this.card = card;
        this.excommunicationTimes = excommunicationTimes;
        descriptionPane.appendText(card.getDescription());

        placeCard();

    }

    /**
     * Place the excommunication cards into the correct pane
     */
    private void placeCard() {
        ImageView image = (ImageView) cardPane.getChildren().get(0);
        image.setImage(CardImage.placeExcommunication(card.getPeriod().toInt(), card.getId()));

    }


    /**
     * Handles the "no" button
     */

    @FXML
    private void onNoClicked() {
        response = false;
        sendAction();


    }


    /**
     * Handles the "yes" button
     */
    @FXML
    private void onYesClicked() {
        response = true;
        sendAction();

    }

    /**
     * Send the response to the server.
     * If
     */
    private void sendAction() {

        if(excommunicationTimes == 2 && !response){
            showLackOfFaith();
            return;

        }
        ClientAction action = new ClientAction(NetworkProtocol.SHOW_SUPPORT);
        action.setObject(response);
        gui.sendAction(action);
        stage.close();
    }


    /**
     * I dare you to be excommunicated for three times and see what happens
     */

    @FXML
    private void closePane(){
        ClientAction action = new ClientAction(NetworkProtocol.SHOW_SUPPORT);
        action.setObject(response);
        gui.sendAction(action);
        stage.close();
    }

    public void setGui(Gui gui){
        this.gui = gui;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * I dare you to be excommunicated for three times and see what happens
     */
    private void showLackOfFaith() {
        lackOfFaith.setVisible(true);
    }


}
