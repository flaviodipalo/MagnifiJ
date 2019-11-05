package view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.cards.developmentcards.Resources;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is called whenever the player needs to convert a
 * council privilege.
 */
public class CouncilPrivilegeController {

    private Gui gui;
    private List<Resources> resources;
    private Stage stage;
    private int maxPrivilege;
    private int nClicked;
    private ArrayList<Resources> chosen;

    @FXML
    private Label maxPrivileges;

    @FXML
    private void initialize(){
        chosen = new ArrayList<>();
        nClicked = 0;
    }

    /**
     * This method show in a label the number of council privileges
     * the player has to convert
     * @param resources list sent from the server
     * @param maxPrivilege to choose
     */
    public void setResources(List<Resources> resources, int maxPrivilege) {
        this.resources = resources;
        this.maxPrivilege = maxPrivilege;
        maxPrivileges.setText(String.valueOf(maxPrivilege));
    }


    /**
     * This method handles the player's mouse click over
     * a resource. After the click this method add the
     * resource to an array list
     * @param event mouse click
     */
    @FXML
    private void onClicked(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();
        if(!pane.isDisabled() && nClicked < maxPrivilege){
            chosen.add(resources.get(Integer.valueOf(pane.getId())));
            nClicked++;
            pane.setStyle("-fx-border-color: red");
            pane.setDisable(true);
        }




    }

    /**
     * As soon as the user goes over with mouse this
     * method highlights the field of blue
     * @param event mouse over the field
     */

    @FXML
    private void onEntered(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();
        if(!pane.isDisabled()){
            pane.setStyle("-fx-border-color: blue");
        }

    }

    /**
     * As soon as the user exits from the field
     * this method restore the original border color
     * to transparent
     * @param event mouse exits from the field
     */

    @FXML
    private void onExited(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();
        if(!pane.isDisabled()){
            pane.setStyle("-fx-border-color: transparent");
        }

    }

    /**
     * This method handles the on confirm button.
     * As soon as the player click over the button
     * it sends the action to the server and closes the stage
     * If the player has not chosen the max number of privileges
     * displays a warning and prevent the array of resources to be sent.
     */

    @FXML
    private void onConfirm(){
        if(nClicked == maxPrivilege){
            ClientAction action = new ClientAction(NetworkProtocol.CONVERT_COUNCIL_PRIVILEGE);
            action.setObjects(chosen);
            gui.sendAction(action);

            stage.close();
        }
        else {
            maxPrivileges.setTextFill(Color.RED);
        }
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
