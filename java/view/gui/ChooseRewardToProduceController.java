package view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.cards.developmentcards.YellowCard;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;

/**
 * This class display to the user the card he has to choose the reward when he
 * activates the production
 */
public class ChooseRewardToProduceController {
    private YellowCard yellowCard;
    private Gui gui;
    private Stage stage;

    @FXML
    private ImageView card;

    /**
     * Handles the button
     * @param event button clicked
     */
    @FXML
    void onClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        int index = Integer.valueOf(button.getId());
        ClientAction action = new ClientAction(NetworkProtocol.CHOOSE_REWARD_TO_PRODUCE);
        action.setObject(yellowCard.getYellowReward().get(index));
        gui.sendAction(action);
        stage.close();

    }

    /**
     *
     * @param yellowCard to be set into the image view
     */
    public void setYellowCard(YellowCard yellowCard){
        this.yellowCard = yellowCard;
        initCard();

    }

    /**
     * load the card into the image view
     */
    private void initCard(){
        card.setImage(CardImage.placeDevCard(yellowCard.getId()));
    }



    public void setGui(Gui gui) {
        this.gui = gui;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
