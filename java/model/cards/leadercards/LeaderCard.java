package model.cards.leadercards;

import controller.exceptions.NotEnoughValueException;
import controller.exceptions.TimeOutException;
import model.players.Player;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents the leader card
 */
public abstract class LeaderCard implements Serializable{
    protected String name;
    String description;
    private List<LeaderRequirements> leaderRequirements;
    private boolean activated = false;
    private String id;

    public LeaderCard(String name, String id, List<LeaderRequirements> leaderRequirements, String description) {
        this.name = name;
        this.leaderRequirements = leaderRequirements;
        this.description = description;
        this.id = id;
    }


    public abstract void activate(Player player) throws TimeOutException, NotEnoughValueException;

    public abstract void activate();

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void deactivate(){

    }
    public List<LeaderRequirements> getLeaderRequirements() {
        return leaderRequirements;
    }

    public String getName() {
        return name;
    }

    @Override
    public abstract String toString();

    public String getDescription(){
        return description;
    }

    public abstract String getEffect();

    public String getId() {
        return id;
    }
}
