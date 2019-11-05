package view.cli;

import com.diogonunes.jcdp.color.api.Ansi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A thread which reads everything written on the console by the user when it is not
 * his turn
 */
public class NotYourTurnThread implements Runnable {
    private Cli cli;

    /**
     * Instantiate the Thread
     * @param cli the cli associated with the thread
     */
    NotYourTurnThread(Cli cli){
        this.cli = cli;
    }

    /**
     * Start the thread, which keeps reading until is interrupted
     */
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            Display.println("IS NOT YOUR TURN", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
            while (!cli.isYourTurn()) {
                if(in.ready()) {
                    Display.println("IS NOT YOUR TURN", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
                    in.readLine();
                }
            }
        } catch (IOException e) {
            Display.println(e);
        }
    }
}
