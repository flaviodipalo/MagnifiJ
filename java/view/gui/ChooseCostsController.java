package view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.cards.developmentcards.Cost;
import model.cards.developmentcards.Resources;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;

import java.util.List;

/**
 * This class allows the user to choose one of the
 * costs a card has. It's called when the user pick a card with multiple costs.
 *
 */
public class ChooseCostsController {

    private Gui gui;
    private static final String COST_0 = "cost0";
    private static final int FIRST = 1;
    private static final int SECOND = 2;
    private static final int ZERO = 0;


    @FXML
    private AnchorPane coins1;

    @FXML
    private AnchorPane servants1;

    @FXML
    private AnchorPane stone1;

    @FXML
    private AnchorPane wood1;

    @FXML
    private AnchorPane faith1;

    @FXML
    private AnchorPane military1;

    @FXML
    private AnchorPane victory1;


    @FXML
    private Label coins;

    @FXML
    private Label servants;

    @FXML
    private Label stone;

    @FXML
    private Label wood;

    @FXML
    private Label faith;

    @FXML
    private Label military;

    @FXML
    private Label victory;


    @FXML
    private AnchorPane coins2;

    @FXML
    private AnchorPane servants2;

    @FXML
    private AnchorPane stone2;

    @FXML
    private AnchorPane wood2;

    @FXML
    private AnchorPane faith2;

    @FXML
    private AnchorPane military2;

    @FXML
    private AnchorPane victory2;

    private Stage stage;
    private Resources playerResources;



    private List<Cost> costList;

    public void init(List<Cost> costs, Resources playerResources, Stage stage){
        this.costList = costs;
        this.stage = stage;
        this.playerResources = playerResources;
        showCosts();
    }

    /**
     * Set all the labels with the correct cost value provided by the card.
     * It also displays the current player's resources in order to let him choose.
     */
    private void showCosts(){
        Cost first = costList.get(ZERO);
        Cost second = costList.get(FIRST);
        setCost(coins1, first.getResourcesNeeded().getCoins(), first.getResourcesToPay().getCoins());
        setCost(coins2, second.getResourcesNeeded().getCoins(), second.getResourcesToPay().getCoins());
        setCost(wood1, first.getResourcesNeeded().getWood(), first.getResourcesToPay().getWood());
        setCost(wood2, second.getResourcesNeeded().getWood(), second.getResourcesToPay().getWood());
        setCost(stone1, first.getResourcesNeeded().getStone(), first.getResourcesToPay().getStone());
        setCost(stone2, second.getResourcesNeeded().getStone(), second.getResourcesToPay().getStone());
        setCost(servants1, first.getResourcesNeeded().getServants(), first.getResourcesToPay().getServants());
        setCost(servants2, second.getResourcesNeeded().getServants(), second.getResourcesToPay().getServants());
        setCost(military1, first.getResourcesNeeded().getMilitaryPoints(), first.getResourcesToPay().getMilitaryPoints());
        setCost(military2, second.getResourcesNeeded().getMilitaryPoints(), second.getResourcesToPay().getMilitaryPoints());
        setCost(faith1, first.getResourcesNeeded().getFaithPoints(), first.getResourcesToPay().getFaithPoints());
        setCost(faith2, second.getResourcesNeeded().getFaithPoints(), second.getResourcesToPay().getFaithPoints());
        setCost(victory1, first.getResourcesNeeded().getVictoryPoints(), first.getResourcesToPay().getVictoryPoints());
        setCost(victory2, second.getResourcesNeeded().getVictoryPoints(), second.getResourcesToPay().getVictoryPoints());

        coins.setText(String.valueOf(playerResources.getCoins()));
        wood.setText(String.valueOf(playerResources.getWood()));
        stone.setText(String.valueOf(playerResources.getStone()));
        servants.setText(String.valueOf(playerResources.getServants()));
        military.setText(String.valueOf(playerResources.getMilitaryPoints()));
        victory.setText(String.valueOf(playerResources.getVictoryPoints()));
        faith.setText(String.valueOf(playerResources.getFaithPoints()));





    }

    /**
     *
     * @param pane is the line contain resources needed and resources to pay to fill them up with
     *             the correct costs.
     * @param resourcesNeeded for the card
     * @param resourcesToPay the card
     */
    private void setCost(AnchorPane pane, int resourcesNeeded, int resourcesToPay){
        Label firstLabel = (Label) pane.getChildren().get(FIRST);
        Label secondLabel = (Label) pane.getChildren().get(SECOND);

        firstLabel.setText(String.valueOf(resourcesNeeded));
        secondLabel.setText(String.valueOf(resourcesToPay));

    }

    /**
     * it's called when the user clicks over one of the two costs
     * displayed. As soon as he clicks the controller sends the cost to
     * the master
     * @param event mouse clicked
     */
    @FXML
    private void onCostSelected(MouseEvent event) {
        AnchorPane chosen = (AnchorPane) event.getSource();
        Cost cost;

        if(chosen.getId().equals(COST_0)){
            cost = costList.get(ZERO);
        }
        else {
            cost = costList.get(FIRST);
        }
        ClientAction action = new ClientAction(NetworkProtocol.CHOOSE_COST);
        action.setObject(cost);
        gui.sendAction(action);
        stage.close();

    }

    /**
     * As soon as the user goes over with mouse this
     * method highlights the field of red
     * @param event mouse over the field
     */
    @FXML
    private void onFieldEntered(MouseEvent event) {
        Pane pane = (Pane) event.getSource();
        pane.setStyle("-fx-border-color: red");
    }

    /**
     * As soon as the user exits from the field
     * this method restore the original border color
     * to transparent
     * @param event mouse exits from the field
     */
    @FXML
    private void onFieldExited(MouseEvent event) {
        Pane pane = (Pane) event.getSource();
        pane.setStyle("-fx-border-color: transparent");
    }

    public void setGui(Gui gui){
        this.gui = gui;
    }


}
