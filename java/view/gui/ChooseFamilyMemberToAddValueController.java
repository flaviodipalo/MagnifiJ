package view.gui;

import model.players.FamilyMember;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;

import java.util.List;

/**
 * This class allows the user to choose one family member
 * that will gain a dice value of six. it's called due an activation
 * of a leader card.
 */
public class ChooseFamilyMemberToAddValueController {

    private Gui gui;
    private Stage stage;
    private List<FamilyMember> familyMembers;


    public void setFamilyMembers(List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the click event over the family member chosen
     * and sends it to the server.
     * As soon as the action is sent close the stage
     * @param event mouse click
     */
    @FXML
    private void onClick(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();

        for (FamilyMember familyMember : familyMembers){
            if(familyMember.getDiceColor().toString().equals(pane.getId())){
                ClientAction action = new ClientAction(NetworkProtocol.CHOOSE_FAMILY_TO_ADD_VALUE);
                action.setFamilyMember(familyMember);
                gui.sendAction(action);
                stage.close();
            }
        }

    }
    /**
     * As soon as the user goes over with mouse this
     * method highlights the field of blue
     * @param event mouse over the field
     */
    @FXML
    private void onEntered(MouseEvent event) {
        StackPane stackPane = (StackPane) event.getSource();
        stackPane.setStyle("-fx-border-color: blue");
    }
    /**
     * As soon as the user exits from the field
     * this method restore the original border color
     * to transparent
     * @param event mouse exits from the field
     */
    @FXML
    private void onExited(MouseEvent event) {
        StackPane stackPane = (StackPane) event.getSource();
        stackPane.setStyle("-fx-border-color: transparent");
    }
}
