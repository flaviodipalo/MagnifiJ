package view.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.cards.leadercards.LeaderCard;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;
import view.gui.CardImage;

import java.util.List;

/**
 * This class allows the user to choose the leader cards
 * sent from the server
 */
public class LeaderCardsController {

    @FXML
    private AnchorPane cards;
    private List<LeaderCard> leaderCards;
    private Gui gui;

    @FXML
    private StackPane wait;


    /**
     * This method is called as soon as the fxml file is loaded
     */
    @SuppressWarnings("unused")
    @FXML
    private void initialize(){
        refresh();
    }


    /**
     * Allows the user to select a card and send it to the server
     * @param event card clicked
     */
    @FXML
    private void onCardSelected(MouseEvent event){
        if(!wait.isVisible()){
            StackPane stackPane = (StackPane) event.getSource();
            for(LeaderCard leaderCard : leaderCards){
                if(stackPane.getId().equals(leaderCard.getId())){
                    wait.setVisible(true);
                    ClientAction action = new ClientAction(NetworkProtocol.DRAFT_LEADER_CARD);
                    action.setLeaderCard(leaderCard);
                    gui.sendAction(action);
                }
            }

        }

    }

    /**
     * Updates the leader cards displayed
     * @param leaderCards to choose sent from the server
     */
    public void onCardUpdate(List<LeaderCard> leaderCards){
        this.leaderCards = leaderCards;
        refresh();
        int i = 0;
        for (LeaderCard leaderCard : leaderCards){
            StackPane pane = (StackPane) cards.getChildren().get(i);
            pane.setId(leaderCard.getId());
            pane.setVisible(true);
            ImageView card = (ImageView) pane.getChildren().get(0);
            card.setImage(CardImage.placeLeaderCard(leaderCard.getId()));

            i++;

        }

    }

    /**
     * Set all the cards to not visible
     */

    private void refresh(){
        for (Node node : cards.getChildren()){
            StackPane pane = (StackPane) node;
            pane.setVisible(false);
            wait.setVisible(false);

        }
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }
}
