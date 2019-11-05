package view.gui;

import model.players.FamilyMember;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import model.dices.DiceColor;
import network.protocol.ClientAction;
import view.Gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the controller of the family member popup which
 * is displayed as soon as the player choose to place a family member
 */
public class FamilyMemberPopUpController {
    private GameBoardController gameController;


    private String memberColor;
    private Gui gui;

    @FXML
    private Label error;

    @FXML
    private ChoiceBox<String> nServants;
    @FXML
    private Button confirmButton;
    @FXML
    private StackPane orangeMember;
    @FXML
    private StackPane blackMember;
    @FXML
    private StackPane whiteMember;
    @FXML
    private StackPane neutralMember;

    private ArrayList<StackPane> members;

    private Map<String, SetFamilyValue> familyMap;

    /**
     * Initialized the
     */

    public void init(){
        initFamilyArray();
        initFamilyMap();
        initServants();
        initFamilyMembers();
    }

    /**
     * Creates an array list of stack panes containing the family members
     */
    private void initFamilyArray(){
        members = new ArrayList<>();
        members.add(orangeMember);
        members.add(blackMember);
        members.add(whiteMember);
        members.add(neutralMember);
    }

    /**
     * Initialize the hash map used for set to visible the family member
     * the players has
     */
    private void initFamilyMap(){

        for(StackPane member : members){
            member.setVisible(false);
        }
        familyMap = new HashMap<>();
        familyMap.put(DiceColor.BLACK.toString(), this::setBlackMember);
        familyMap.put(DiceColor.WHITE.toString(), this::setWhiteMember);
        familyMap.put(DiceColor.ORANGE.toString(), this::setOrangeMember);
        familyMap.put(DiceColor.NEUTRAL.toString(), this::setNeutralMember);
    }

    /**
     * fill up the neutral member with his dice value
     * @param value of the dice
     */
    private void setNeutralMember(String value){
        neutralMember.setVisible(true);
        Label memberValue = (Label) neutralMember.getChildren().get(1);
        memberValue.setText(value);

    }
    /**
     * fill up the orange member with his dice value
     * @param value of the dice
     */
    private void setOrangeMember(String value){
        orangeMember.setVisible(true);
        Label memberValue = (Label) orangeMember.getChildren().get(1);
        memberValue.setText(value);
    }
    /**
     * fill up the black member with his dice value
     * @param value of the dice
     */
    private void setBlackMember(String value){
        blackMember.setVisible(true);
        Label memberValue = (Label) blackMember.getChildren().get(1);
        memberValue.setText(value);
    }
    /**
     * fill up the white member with his dice value
     * @param value of the dice
     */
    private void setWhiteMember(String value){
        whiteMember.setVisible(true);
        Label memberValue = (Label) whiteMember.getChildren().get(1);
        memberValue.setText(value);
    }


    /**
     *  loop the family members of the player and activates and initialize
     *  them with the theirs dice values
     */
    private void initFamilyMembers(){
        List<FamilyMember> familyMembers = gameController.getPlayer().getFamilyMembers();
        for (FamilyMember familyMember : familyMembers){
            familyMap.get(familyMember.getDiceColor().toString()).setFamilyValue(String.valueOf(familyMember.getValue()));
        }
    }

    /**
     * Fills a choose box with the values between 0 and the maximum number
     * of servants the player has
     */
    private void initServants(){
        ObservableList<String> connectionList = FXCollections.observableArrayList();
        for(int i = 0; i<= gameController.getPlayer().getPlayerResources().getServants(); i++){
            connectionList.add(String.valueOf(i));
        }
        nServants.setItems(connectionList);
        nServants.setValue("0");
    }

    /**
     * As soon as the player selects the family member
     * this method set its border to blue
     * @param event mouse over the field
     */

    @FXML
    private void onFamilySelected(MouseEvent event){
        for(StackPane member : members){
            member.setStyle("-fx-border-color: transparent");
        }
        StackPane memberChosen = (StackPane) event.getSource();
        memberChosen.setStyle("-fx-border-color: blue");

        Circle circle = (Circle) memberChosen.getChildren().get(0);
        memberColor = circle.getId();

    }

    /**
     * Handles the click over the confirm button.
     * Sends an ClientAction containing the position
     * the family member chosen and the servants added
     */
    @FXML
    private void onConfirmAction(){
        if(memberColor!=null){
            ClientAction action = new ClientAction(gameController.getAction());
            action.setPosition(gameController.getPosition());
            for(FamilyMember familyMember : gameController.getPlayer().getFamilyMembers()){
                if(familyMember.getDiceColor().toString().equals(memberColor)){
                    action.setFamilyMember(familyMember);
                }
            }
            action.setServants(Integer.valueOf(nServants.getValue()));
            gui.sendAction(action);
            gameController.getFamilyStage().close();

        }else {
            confirmButton.setStyle("-fx-border-color: red");
            error.setText("Please select a family member");

        }

    }

    /**
     * On cancel close the stage
     */
    @FXML
    private void onCancelAction(){
        gameController.getFamilyStage().close();
    }



    public void setGameController(GameBoardController gameController) {
        this.gameController = gameController;
    }
    public void setGui(Gui gui){
        this.gui = gui;
    }

    /**
     * Iterface used for the lambda functions in the hash map
     */
    @FunctionalInterface
    private interface SetFamilyValue{

        void setFamilyValue(String value);
    }


}
