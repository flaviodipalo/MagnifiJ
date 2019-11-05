package controller.states.ExcommunicationStates;

import model.players.Player;

import java.io.Serializable;

/**
 * Generic excommunication state
 */
public abstract class ExcommunicationState implements Serializable{
    public void activate(Player player) {
    }
}
