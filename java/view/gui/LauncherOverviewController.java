package view.gui;

import controller.exceptions.ServerException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import network.client.rmi.RMIClient;
import network.client.socket.SocketClient;
import view.Client;
import view.Gui;
import view.MainApp;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the view controller of the launcher. Allows the user to choose between console
 * and graphical user interface and between socket and rmi connection. It handles the sign in
 * and sing up request.
 */
public class LauncherOverviewController {
    private static final String SOCKET = "Socket";
    private static final String RMI = "RMI";
    private static final String BUSY = "Port busy";
    private static final String CONNECTION_ERROR = "Connection error";
    private ObservableList<String> connectionList = FXCollections.observableArrayList(SOCKET, RMI);
    private boolean logged;
    private static final Logger LOGGER = Logger.getLogger(LauncherOverviewController.class.getName());
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ChoiceBox<String> connectionChoiceBox;
    @FXML
    private TextArea consoleArea;
    @FXML
    private Button signInButton;
    @FXML
    private Button signUpButton;

    @FXML
    private TextField addressField;

    private String connection;

    //reference to the main application
    private MainApp mainApp;

    private Client client;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @SuppressWarnings("unused")
    @FXML
    private void initialize(){
        connectionChoiceBox.setItems(connectionList);
        connectionChoiceBox.setValue(SOCKET);

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp contains the root layout
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }

    /**
     * This method is called when the user click on the Sign In button. It tries to connect
     * with the provided username, password and connection type.
     * It displays various alerts of confirmation or error.
     */

    @FXML
    public void singUp(){
        getConnection();
        if(isInputValid()){
            try{
                if(SOCKET.equals(connection)){
                    client = new SocketClient(addressField.getText(), new Gui(mainApp));
                    logged = client.signUp(usernameField.getText(), passwordField.getText());


                }else if(RMI.equals(connection)){
                    client = new RMIClient(addressField.getText(), new Gui(mainApp));
                    logged = client.signUp(usernameField.getText(), passwordField.getText());


                }

                if(logged){
                    Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.CONFIRMATION, "Connected",  "You are connected with the server");
                    login();



                }
                else {
                    Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.ERROR, "Error",  "Username already exists");
                }
            }catch (ServerException e){
                LOGGER.log(Level.FINEST, e.getMessage(), e);
                Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.ERROR, CONNECTION_ERROR,  BUSY);
            }


        }else {
            Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.ERROR, "Credential error",  "Do not leave empty username or password field");
        }










    }

    /**
     * If logged starts the client
     */
    private void login(){
        client.setUsername(usernameField.getText());
        client.start();
        disableButtons();
        onClose();

    }

    /**
     * This method is called when the user click on the Sign Up button. It tries to connect
     * with the provided username, password and connection type.
     * It displays various alerts of confirmation or error.
     */

    @FXML
    public void signIn() {
        getConnection();
        if(isInputValid()){
            try{
                if(SOCKET.equals(connection)){
                    client = new SocketClient(addressField.getText(), new Gui(mainApp));
                    logged = client.signIn(usernameField.getText(), passwordField.getText());
                }else {
                    client = new RMIClient(addressField.getText(), new Gui(mainApp));
                    logged = client.signIn(usernameField.getText(), passwordField.getText());
                }
                if(logged) {
                    Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.CONFIRMATION, "Connected",  "You are connected with the server");
                    login();

                }
                else {
                    Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.ERROR, "Error",  "Username or password not valid");
                }
            }catch (ServerException e) {
                Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.ERROR, "Connection error", BUSY);
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }

        }else {
            Alert.displayAlert(mainApp.getPrimaryStage(), javafx.scene.control.Alert.AlertType.WARNING, "credentials error",  "Username or password not empty");
        }




    }

    /**
     * This method disables the buttons after
     * the player is logged with the server
     */
    private void disableButtons(){
        usernameField.setEditable(false);
        passwordField.setEditable(false);
        addressField.setEditable(false);
        signInButton.setDisable(true);
        signUpButton.setDisable(true);
        connectionChoiceBox.setDisable(true);

    }




    /**
     * check that the username and password model.cards.field are not empty
     * @return true if the input is valid
     */
    private boolean isInputValid(){
        return !(usernameField.getText() == null || usernameField.getText().length() == 0
                || passwordField.getText() == null || passwordField.getText().length() == 0);


    }

    private void onClose(){
        mainApp.getPrimaryStage().setOnCloseRequest(e -> {
            mainApp.onClose();
        });
    }

    public void showMessage(String message) {
        consoleArea.appendText(message+"\n");
    }

    private void getConnection(){
        connection = connectionChoiceBox.getValue();

    }
}
