package controller.states;

import model.cards.developmentcards.Resources;

import java.io.Serializable;

/**
 * this interface represent the State for the method get Tower Reward
 */
@FunctionalInterface
public interface GetTowerRewardState extends Serializable {
    void getTowerReward(Resources resources);
}
