package controller.states;

import model.cards.developmentcards.Resources;

import java.io.Serializable;

/**
 * this interface represent the State for the method receiveInstantReward.
 */
@FunctionalInterface
public interface ReceiveInstantRewardState extends Serializable {
    void receiveInstantReward(Resources resources);
}
