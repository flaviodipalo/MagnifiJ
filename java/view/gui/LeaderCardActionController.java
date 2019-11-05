package view.gui;

import controller.Actions;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.cards.leadercards.LeaderCard;
import network.protocol.ClientAction;
import view.Gui;
import view.gui.CardImage;

import java.util.List;

/**
 * This class is the controller of the leader card action.
 * Allow the user to choose to sell of activate the leader card and provide
 * a tool tip
 */
public class LeaderCardActionController {
    @FXML
    private ImageView card;
    private String id;
    private Gui gui;
    private String action;
    private List<LeaderCard> leaderCards;
    private Stage stage;
    private LeaderCard leaderCard;

    public void init(List<LeaderCard> leaderCards, String id){
        this.leaderCards = leaderCards;
        this.id = id;
        placeCard();
        getCard();
    }


    private void getCard(){
        for (LeaderCard currentLeaderCard : leaderCards){
            if(currentLeaderCard.getId().equals(id)){
                this.leaderCard = currentLeaderCard;
            }
        }
    }
    private void placeCard(){
        card.setImage(CardImage.placeLeaderCard(id));
    }

    public void setGui(Gui gui){
        this.gui = gui;
    }
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * Handles the sell button
     */
    @FXML
    private void onSell(){
        action = Actions.SELL_LEADER_CARDS;
        sendAction();
        stage.close();


    }

    /**
     * Handles the mouse over the card event
     * and display a tooltip with the description of the leader card
     * @param event mouse over the stack pane
     */
    @FXML
    private void onCardEntered(MouseEvent event){
        StackPane cardPane = (StackPane) event.getSource();
        Tooltip tooltip = new Tooltip(leaderCard.getDescription());
        Tooltip.install(cardPane, tooltip);
    }


    /**
     * This method creates and sends the action to the server
     */
    private void sendAction(){
        ClientAction clientAction = new ClientAction(action);
        clientAction.setLeaderCard(leaderCard);
        gui.sendAction(clientAction);

    }
    /**
     * Handles the activate button
     */
    @FXML
    private void onActivate(){
        action = Actions.ACTIVATE_LEADER_CARDS;
        sendAction();
        stage.close();
    }

    @FXML
    private void onCancel(){
        stage.close();
    }
}
