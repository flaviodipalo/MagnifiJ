package view.gui;

import model.players.Player;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.cards.developmentcards.Card;
import view.gui.CardImage;

import java.util.List;


/**
 * This class controls the other player's punches board in order to show it
 * to the player.
 * This class is call when the player click over the Player Status button
 */
public class PlayerPunchBoardController {

    @FXML
    private AnchorPane greenCards;

    @FXML
    private AnchorPane blueCards;

    @FXML
    private AnchorPane yellowCards;

    @FXML
    private AnchorPane purpleCards;

    /**
     * Initialize the player's punch board  filling each line with his cards
     * @param player's punchboard
     */

    public void init(Player player) {
        fillLine(greenCards, player.getGreenCards());
        fillLine(yellowCards, player.getYellowCards());
        fillLine(purpleCards, player.getPurpleCards());
        fillLine(blueCards, player.getBlueCards());



    }

    /**
     * This method fill each line with the correct card
     * @param line to filled
     * @param cards to display
     */
    private void fillLine(AnchorPane line , List<? extends Card> cards){
        int i = 0;
        for (Card card : cards){
            StackPane cardPane = (StackPane) line.getChildren().get(i);
            ImageView cardImage = (ImageView) cardPane.getChildren().get(0);
            cardImage.setImage(CardImage.placeDevCard(card.getId()));
            cardPane.setVisible(true);
            i++;
        }
    }
}
