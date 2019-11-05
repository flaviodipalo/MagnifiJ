package controller.states.ExcommunicationStates;

import model.cards.developmentcards.Resources;
import model.players.Player;
import controller.states.AddResourcesState;

import java.util.ArrayList;

/**
 * If the player has taken an excommunication which will give him a malus on the resources received
 */
public class ExcommunicationAddResourcesState extends ExcommunicationState implements AddResourcesState  {
    AddResourcesState state;
    private ArrayList<Resources> resourcesToSubtract;
    private int resourcesSubtractedForEachResource;

    public ExcommunicationAddResourcesState(ArrayList<Resources> resourcesToSubtract, int resourcesSubtractedForEachResource) {
        this.resourcesToSubtract = resourcesToSubtract;
        this.resourcesSubtractedForEachResource = resourcesSubtractedForEachResource;
    }

    public void activate(Player player){
        state = player.getAddResourcesState();
        player.setAddResourcesState(this);
    }

    public void addResources(Resources resources1){
        Resources resources = new Resources(resources1);
        Resources newResources;
        for(Resources resourceToSubtract:resourcesToSubtract) {
            for (int i = 0; i < resourcesSubtractedForEachResource; i++) {
                if (resourceToSubtract.isWood()) {
                    if (resources.getWood() > 0) {
                        newResources = new Resources(1, 0, 0, 0, 0, 0, 0, 0);
                        resources.subtractResources(newResources);
                    }
                }
                if (resourceToSubtract.isCoins()) {
                    if (resources.getCoins() > 0) {
                        newResources = new Resources(0, 1, 0, 0, 0, 0, 0, 0);
                        resources.subtractResources(newResources);
                    }
                }
                if (resourceToSubtract.isServants()) {
                    if (resources.getServants() > 0) {
                        newResources = new Resources(0, 0, 1, 0, 0, 0, 0, 0);
                        resources.subtractResources(newResources);
                    }
                }
                if (resourceToSubtract.isStone()) {
                    if (resources.getStone() > 0) {
                        newResources = new Resources(0, 0, 0, 1, 0, 0, 0, 0);
                        resources.subtractResources(newResources);
                    }
                }
                if (resourceToSubtract.isVictoryPoints()) {
                    if (resources.getVictoryPoints() > 0) {
                        newResources = new Resources(0, 0, 0, 0, 1, 0, 0, 0);
                        resources.subtractResources(newResources);
                    }
                }
                if (resourceToSubtract.isFaithPoints()) {
                    if (resources.getFaithPoints() > 0) {
                        newResources = new Resources(0, 0, 0, 0, 0, 1, 0, 0);
                        resources.subtractResources(newResources);
                    }
                }
                if (resourceToSubtract.isMilitaryPoints()) {
                    if (resources.getMilitaryPoints() > 0) {
                        newResources = new Resources(0, 0, 0, 0, 0, 0, 1, 0);
                        resources.subtractResources(newResources);
                    }
                }
                if (resourceToSubtract.isCouncilPrivilege()) {
                    if (resources.getCouncilPrivilege() > 0) {
                        newResources = new Resources(0, 0, 0, 0, 0, 0, 0, 1);
                        resources.subtractResources(newResources);
                    }
                }
            }
        }
        state.addResources(resources);
    }

    public ArrayList<Resources> getResourcesToSubtract() {
        return resourcesToSubtract;
    }

    public int getResourcesSubtractedForEachResource() {
        return resourcesSubtractedForEachResource;
    }

    public void setResourcesSubtractedForEachResource(int resourcesSubtractedForEachResource) {
        this.resourcesSubtractedForEachResource = resourcesSubtractedForEachResource;
    }

    public void setResourcesToSubtract(ArrayList<Resources> resourcesToSubtract) {
        this.resourcesToSubtract = resourcesToSubtract;
    }

    public void setState(AddResourcesState state) {
        this.state = state;
    }
}
