package model.cards.excommunicationcards;

import model.cards.developmentcards.*;
import model.players.Player;
import controller.states.ExcommunicationStates.ExcommunicationState;

import java.io.Serializable;

/**
 * This class represent the excommunication card which gives
 * the user a malus during all game
 */
public class ExcommunicationCard implements Serializable{
    private String id;
    private Period period;
    private String description;
    private ExcommunicationState state;

    public ExcommunicationCard(String id, Period period, ExcommunicationState state, String description) {
        this.period = period;
        this.state = state;
        this.description = description;
        this.id = id;
    }

    public void activate(Player player){
        state.activate(player);
    }

    public String getId() {
        return id;
    }

    public Period getPeriod() {
        return period;
    }

    public String getDescription() {
        return description;
    }

    public void setState(ExcommunicationState state) {
        this.state = state;
    }

    public ExcommunicationState getState() {
        return state;
    }
}
