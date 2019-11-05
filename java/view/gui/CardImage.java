package view.gui;

import javafx.scene.image.Image;
import view.MainApp;

/**
 * This class contains some methods used by the gui to place images
 */
public class CardImage {
    private static final String CARDS_PATH = "/pictures/LorenzoCards/devcards_f_en_c_";
    private static final String LEADER_PATH = "/pictures/leaderCards/leaders_f_c_";
    private static final String EXCOMM = "/pictures/excommunications/excomm_";
    private CardImage(){

    }


    /**
     * @param id of the card
     * @return an new Image with the correct card
     */
    public static Image placeDevCard(String id){
        return new Image(MainApp.class.getResourceAsStream(CARDS_PATH+ id + ".png"));
    }
    /**
     * @param id of the card
     * @return an new Image with the correct Leader card
     */
    public static Image placeLeaderCard(String id){
        return new Image(MainApp.class.getResourceAsStream(LEADER_PATH + id + ".jpg"));
    }
    /**
     * @param id of the card
     * @param era of the card
     * @return an new Image with the correct Excommunication card
     */
    public static Image placeExcommunication(int era, String id){
        return new Image(MainApp.class.getResourceAsStream(EXCOMM + era + "_" + id + ".png"));

    }

}
