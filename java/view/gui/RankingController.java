package view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * This class is used for displaying to the user
 * the rankings of the game
 */
public class RankingController {
    private String ranking;
    private String result;

    @FXML
    private Label resultLabel;
    @FXML
    private TextArea rankingArea;

    /**
     * Initialize and displays the results of the game and rankings
     */
    public void init(){
        rankingArea.appendText(ranking);
        resultLabel.setText(result);
    }





    /**
     * Handles the exit button.
     * Closes the game
     */
    @FXML
    private void onExit() {
        Platform.exit();
    }



    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public void setResult(String result){
        this.result = result;
    }



}
