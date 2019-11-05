package view.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.cards.developmentcards.BonusTile;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import view.Gui;

import java.util.List;

/**
 * This class allows the user to select his personal bonus tile.
 * When the user clicks on a bonus tile, the controller manages to send it
 * to the server.
 */
public class BonusTilesController {
    private Gui gui;
    private static final String BONUS_TILES_PATH = "/pictures/bonus_tiles/personalbonustile_";
    @FXML
    private AnchorPane bonusTilesPane;
    @FXML
    private StackPane wait;
    private List<BonusTile> bonusTiles;
    private boolean canClick;


    /**
     * This method initialize the bonus tiles in order to let the player choose
     * one of them.
     *@param bonusTiles to display
     */
    @FXML
    public void init(List<BonusTile> bonusTiles){
        this.bonusTiles = bonusTiles;
        canClick = true;
        int index = 0;
        for(BonusTile bonusTile : bonusTiles){
            StackPane bonusTileView = (StackPane) bonusTilesPane.getChildren().get(index);
            ImageView bonusTileImage = (ImageView) bonusTileView.getChildren().get(0);
            bonusTileImage.setImage(new Image(getClass().getResourceAsStream(BONUS_TILES_PATH + bonusTile.getId() + ".png")));
            bonusTileView.setVisible(true);
            bonusTileView.setId(String.valueOf(bonusTile.getId()));
            index++;


        }
    }

    /**
     * Manage the user's click on the bonus tile chosen.
     * This method send the personal bonus tile to the server.
     * @param event bonus tile clicked
     */
    @FXML
    private void onClick(MouseEvent event){
        StackPane stackPane = (StackPane) event.getSource();
        if(canClick && stackPane.isVisible()){
            waitForPlayers();
            String id = stackPane.getId();
            for (BonusTile bonusTile : bonusTiles){
                if(bonusTile.getId().equals(id) ){
                    ClientAction action = new ClientAction(NetworkProtocol.CHOOSE_BONUS_TILE);
                    action.setObject(bonusTile);
                    gui.sendAction(action);
                    return;
                }
            }


        }




    }

    /**
     * Display a wait message after have chosen the
     * personal bonus tile
     */
    private void waitForPlayers() {
        for(Node node : bonusTilesPane.getChildren()){
            node.setVisible(false);
        }
        wait.setVisible(true);

    }



    






    public void setGui(Gui gui) {
        this.gui = gui;
    }
}
