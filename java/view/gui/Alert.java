package view.gui;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * This class is used by the gui to display some alerts
 */
public class Alert {
    private Alert(){
        throw new IllegalAccessError("Utility class");
    }

    /**
     * This method allows to toString an alert message to the user through the GUI
     * @param alertType to set
     * @param title  to set
     * @param content to set
     */
    public static void displayAlert(Stage stage, javafx.scene.control.Alert.AlertType alertType, String title, String content){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(alertType);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

    }
    public static void displayAlert(Stage stage, javafx.scene.control.Alert.AlertType alertType, String title){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(alertType);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("The player is already online");
        alert.showAndWait();
        stage.setOnCloseRequest(event -> Platform.exit());

    }


}
