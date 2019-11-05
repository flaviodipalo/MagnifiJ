package view.cli;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import model.cards.developmentcards.*;
import model.players.FamilyMember;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains the cli methods
 */
public class Display {
    private static ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();
    private static final Logger LOGGER = Logger.getLogger(Display.class.getName());

    private Display(){}

    public static void println(String string) {
        cp.println(string);
    }

    public static void log(String string){
        LOGGER.log(Level.SEVERE, string);
    }

    public static void println(String string, Throwable e) {
        cp.println(string);
        LOGGER.log(Level.FINEST, e.getMessage(), e);
    }

    public static void println(Throwable e) {
        LOGGER.log(Level.FINEST, e.getMessage(), e);
    }

    public static void println(int integer) {
        cp.println(integer);
    }

    static void print(String message, Ansi.Attribute attribute, Ansi.FColor fColor, Ansi.BColor bColor){
        cp.print(message, attribute, fColor, bColor);
        cp.clear();
    }

    static void print(int integer){
        cp.print(integer);
    }

    static void print(String message){
        cp.print(message);
    }

    public static void println(String s, Ansi.Attribute attribute, Ansi.FColor fColor, Ansi.BColor bColor) {
        cp.println(s, attribute, fColor, bColor);
        cp.clear();
    }

    static void printActionList(String[] actionList) {
        int i = 1;
        for(String choice : actionList){
            println("\n\n" + i + ") " + choice + "\n", Ansi.Attribute.UNDERLINE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            i++;
        }
    }

    static void printStartingMenu(String[] actionList) {
        int id = 1;
        cp.print(" /$$      /$$ /$$$$$$$$ /$$   /$$ /$$   /$$\n" +
                "| $$$    /$$$| $$_____/| $$$ | $$| $$  | $$\n" +
                "| $$$$  /$$$$| $$      | $$$$| $$| $$  | $$\n" +
                "| $$ $$/$$ $$| $$$$$   | $$ $$ $$| $$  | $$\n" +
                "| $$  $$$| $$| $$__/   | $$  $$$$| $$  | $$\n" +
                "| $$\\  $ | $$| $$      | $$\\  $$$| $$  | $$\n" +
                "| $$ \\/  | $$| $$$$$$$$| $$ \\  $$|  $$$$$$/\n" +
                "|__/     |__/|________/|__/  \\__/ \\______/ \n", Ansi.Attribute.BOLD, Ansi.FColor.BLACK, Ansi.BColor.CYAN);
        for (int i = 0; i < actionList.length; i++, id++)
            cp.println("\n\n" + id + ") " + actionList[i] + " \n", Ansi.Attribute.UNDERLINE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
        cp.clear();
    }

    static void showFamilyMembers(List<FamilyMember> familyMembers) {
        int id = 0;
        for (FamilyMember fm : familyMembers) {
            Display.println(id + 1 + ") Color: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.println(fm.getDiceColor().toString());
            Display.println("Value: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.println(fm.getValue() + "\n");
            id++;
        }
    }

    static void showCost(Cost cost) {
        Display.println(cost.getResourcesNeeded().getCoins());
    }
}

