package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import model.players.Player;
import view.cli.Display;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class creates multiples tabs containing the other player's punch boards
 */
public class PlayersOverviewController {
    private GameBoardController gameController;
    private ArrayList<Player> players;

    @FXML
    private TabPane tabPane;

    /**
     * Called as soon as the players wants to see the
     * other player's punch boards
     */
    public void init(){
        players = new ArrayList<>(gameController.getPlayers());
        for (Player player : players){
            try {
                if(!player.getUsername().equals(gameController.getPlayer().getUsername()) ){
                    PlayerPunchBoardController controller = createTabPlayer(player.getUsername());
                    controller.init(player);


                }
            }catch (IOException e){
                Display.println(e);
            }
        }




    }

    /**
     * This method creates a new tab that contains the other player punch boards
     * @param username of the player
     * @return the controller of the tab pane
     * @throws IOException if the fxml is missing
     */
    private PlayerPunchBoardController createTabPlayer(String username) throws IOException{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/PlayerCards.fxml"));
            Tab tab = new Tab();
            tab.setText(username);
            AnchorPane content = loader.load();

            tab.setContent(content);

            tabPane.getTabs().add(tab);

            return loader.getController();




    }

    public void setGameController(GameBoardController gameController) {
        this.gameController = gameController;
    }




}
