package view;

import javafx.application.Application;
import view.cli.Cli;
import view.cli.Display;

import java.util.Scanner;

/**
 * Let the user to choose between cli and gui 
 */
public class Launcher {
    private static final String GUI = "2";
    private static final String CLI = "1";

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        launcher.askUI();
    }

    /**
     * Ask the user whether he wants to use the CLI or the GUI
     */
    private void askUI(){
        Scanner in = new Scanner(System.in);
        String choice;
        do{
            Display.println("Please select the user interface you want to use: ");
            Display.println("\n1) CLI\n2) GUI");
            choice = in.nextLine();

        }while(!(choice.equals(CLI) || choice.equals(GUI)));

        if(choice.equals(CLI)){
            Cli cli = new Cli();
            cli.init();
        }else {
            Application.launch(MainApp.class);

        }


    }
}
