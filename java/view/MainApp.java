package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.cards.developmentcards.BonusTile;
import model.cards.leadercards.LeaderCard;
import view.cli.Display;
import view.gui.*;

import java.io.IOException;
import java.util.List;

/**
 * This is the the main app used for the GUI provided to the user
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private LauncherOverviewController launcherController;
    private GameBoardController gameController;

    private Gui gui;
    private LeaderCardsController leaderCardsController;



    public static void main(String[] args) {
        launch(args);
    }

    /**
     * initialize the GUI
     * @param primaryStage passed by the Java FX launcher
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Lorenzo il magnifico");
        initRootLayout();
        showClientOverview();
        primaryStage.setOnCloseRequest(event -> Platform.exit());

    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout(){
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/RootLayout.fxml"));
            BorderPane rootLayout =  loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            Display.println(e);
        }
    }

    /**
     * This method is called to provide the home page to the user.
     */
    private void showClientOverview(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/ClientOverview.fxml"));
            AnchorPane clientOverview = loader.load();

            Scene scene = new Scene(clientOverview);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();



            // Give the launcherController access to the main app.
            launcherController = loader.getController();
            launcherController.setMainApp(this);


        }catch (IOException e){
            Display.println(e);
        }
    }

    /**
     * Display the game board
     */

    void showGameLayout(){

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/GameLayout.fxml"));
            AnchorPane gameLayout = loader.load();
            Scene scene = new Scene(gameLayout);
            primaryStage.setScene(scene);

            primaryStage.setResizable(false);
            primaryStage.show();

            gameController = loader.getController();
            gameController.init();

            onClose();


        } catch (IOException e) {
            Display.println(e);
        }



    }

    /**
     * Let the player to choose the bonus tile
     * @param bonusTiles list
     */

    void showBonusTiles(List<BonusTile> bonusTiles){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/BonusTiles.fxml"));
            AnchorPane bonusTilesPane = loader.load();
            Scene scene = new Scene(bonusTilesPane);
            primaryStage.setScene(scene);
            primaryStage.show();
            onClose();
            primaryStage.setResizable(false);
            BonusTilesController bonusTilesController = loader.getController();
            bonusTilesController.setGui(gui);
            bonusTilesController.init(bonusTiles);
        }catch (IOException e){
            Display.println(e);
        }
    }




    /**
     * Let the player choose the leader cards
     * @param leaderCards list
     */
    void showDraftLeaderCards(List<LeaderCard> leaderCards) {
        if(leaderCardsController == null){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/LeaderCards.fxml"));
                AnchorPane leaderPane = loader.load();
                Scene scene = new Scene(leaderPane);
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();
                onClose();

                leaderCardsController = loader.getController();

                leaderCardsController.setGui(gui);
                leaderCardsController.onCardUpdate(leaderCards);




            }catch (IOException e){
                Display.println(e);
            }

        }else {
            leaderCardsController.onCardUpdate(leaderCards);
        }



    }

    void showRankings(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource("/fxml/Message.fxml"));
            Stage stage = new Stage();
            AnchorPane pane = fxmlLoader.load();
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            RankingController controller = fxmlLoader.getController();
            controller.setRanking(gui.getRanking());
            controller.setResult(gui.getResult());
            controller.init();
            stage.show();

        }catch (IOException e){
            Display.println(e);
        }
    }

    /**
     * Close the resources in case the user exit from the app
     */
    public void onClose(){
        primaryStage.setOnCloseRequest(event -> {
            gui.getClient().onClose();
            primaryStage.close();
            Platform.exit();
            System.exit(0);
        });
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    LauncherOverviewController getLauncherController() {
        return launcherController;
    }

    GameBoardController getGameController() {
        return gameController;
    }


    public void setGui(Gui gui){
        this.gui = gui;
    }
}
