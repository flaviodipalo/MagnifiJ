package view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.cards.leadercards.LeaderCard;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;
import view.gui.CardImage;

import java.util.List;

/**
 * This class is called whenever the player activates a specific
 * Leader card which allows him to copy on of the other player's leader
 * cards activated.
 */
public class ChooseLeaderToCopyController {
    private Gui gui;
    private List<LeaderCard> leaderCards;
    private Stage stage;


    @FXML
    private AnchorPane cards;

    public void setLeaderCards(List<LeaderCard> leaderCards){
        this.leaderCards = leaderCards;
        init();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    /**
     * initialize the leader cards sent by the server
     * in order to let player choose one of them.
     */
    private void init(){
        StackPane cardPane;
        ImageView cardImage;
        int i = 0;
        for(LeaderCard leaderCard : leaderCards){
            cardPane = (StackPane) cards.getChildren().get(i);
            cardPane.setVisible(true);
            cardPane.setId(leaderCard.getId());
            cardImage = (ImageView) cardPane.getChildren().get(0);
            cardImage.setImage(CardImage.placeLeaderCard(leaderCard.getId()));
            i++;
            if(i == 4){
                return;
            }
        }
    }

    /**
     * Handles the player's click over a card and send it
     * directly to the server.
     * After have sent the card this method closes the stage
     * @param event card click
     */
    @FXML
    private void onCardClicked(MouseEvent event) {
        StackPane cardPane = (StackPane) event.getSource();
        if(cardPane.isVisible()){
            for (LeaderCard leaderCard : leaderCards){
                if(leaderCard.getId().equals(cardPane.getId())){
                    ClientAction action = new ClientAction(NetworkProtocol.CHOOSE_LEADER_CARD_TO_COPY);
                    action.setObject(leaderCard);
                    gui.sendAction(action);
                    stage.close();


                }

            }
        }

    }
    /**
     * As soon as the user goes over with mouse this
     * method highlights the field of blue
     * Display also a tool tip containing the description
     * of the leader card.
     * @param event mouse over the field
     */

    @FXML
    private void onCardEntered(MouseEvent event) {
        StackPane cardPane = (StackPane) event.getSource();
        if(cardPane.isVisible()){
            cardPane.setStyle("-fx-border-color: blue");
            for (LeaderCard leaderCard : leaderCards){
                if(cardPane.getId().equals(leaderCard.getId())){
                    Tooltip tooltip = new Tooltip(leaderCard.getDescription());
                    Tooltip.install(cardPane, tooltip);
                }
            }
        }

    }
    /**
     * As soon as the user exits from the field
     * this method restore the original border color
     * to transparent
     * @param event mouse exits from the field
     */
    @FXML
    private void onCardExited(MouseEvent event){
        StackPane cardPane = (StackPane) event.getSource();
        cardPane.setStyle("-fx-border-color: transparent");
    }
}
