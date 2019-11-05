package view.gui;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.cards.developmentcards.YellowCard;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;
import view.gui.CardImage;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class allows the user to choose
 * the production cards to activate.
 * It's called as soon as the user places
 * the family member in the production area.
 */
public class ChooseCardsController {

    private ArrayList<YellowCard> chosenYellowCards;
    private List<YellowCard> yellowCards;
    private Gui gui;
    private Stage stage;

    @FXML
    private AnchorPane cards;

    /**
     * initialize the scene with the production cards
     * @param yellowCards production cards to display
     * @param stage current stage
     */
    public void chooseProduction(List<YellowCard> yellowCards, Stage stage){
        this.yellowCards = yellowCards;
        this.stage = stage;
        chosenYellowCards = new ArrayList<>();
        int i = 0;
        for (YellowCard yellowcard : yellowCards){
            insertCard(yellowcard.getId(), i);
            i++;
        }

    }

    /**
     * This method inserts the card's image into the correct field
     * @param id cards'id
     * @param cardPosition in the array of cards
     */
    private void insertCard(String id, int cardPosition){
        StackPane card = (StackPane) cards.getChildren().get(cardPosition);
        ImageView imageView = (ImageView) card.getChildren().get(0);
        imageView.setImage(CardImage.placeDevCard(id));
        card.setId(id);
        card.setVisible(true);
    }



    /**
     * @param event card clicked
     */
    @FXML
    private void onCardSelected(MouseEvent event) {
        Pane pane = (Pane) event.getSource();
        if(!pane.isDisabled() && pane.isVisible()){
            addCard(pane);
        }else if (pane.isVisible()){
            removeCard(pane);
        }

    }



    /**
     * remove the card from the array if the player does not to activate it anymore
     * @param pane card clicked
     */
    private void removeCard(Pane pane){
        chosenYellowCards.removeIf(yellowCard -> yellowCard.getId().equals(pane.getId()));

        pane.setDisable(false);
        pane.setStyle("-fx-border-color: transparent");
    }

    /**
     * add the card into the array of activated cards
     * @param pane current card
     */
    private void addCard(Pane pane) {

        if(pane.isVisible()){
            for (YellowCard yellowCard : yellowCards){
                if(yellowCard.getId().equals(pane.getId())){
                    chosenYellowCards.add(yellowCard);
                }
            }
            pane.setStyle("-fx-border-color: blue");

        }
        pane.setDisable(true);

    }

    public void setGui(Gui gui){
        this.gui = gui;
    }

    /**
     * handle the confirm button
     */
    @FXML
    private void onConfirm(){
        ClientAction action = new ClientAction(NetworkProtocol.CHOOSE_PRODUCTION_CARDS);
        action.setObjects(chosenYellowCards);
        gui.sendAction(action);
        stage.close();
    }


}
