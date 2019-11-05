package controller.states;

import model.cards.developmentcards.Resources;

/**
 * State caused by the activation of Preacher overrides the method get tower reward
 */
public class PreacherGetTowerRewardState implements GetTowerRewardState {
    GetTowerRewardState state;

    public PreacherGetTowerRewardState(GetTowerRewardState state) {
        this.state = state;
    }
    /**
     * the Preacher doesn't allow you to get resources from towerReward.
     * @param resources return nothing
     */
    @Override
    public void getTowerReward(Resources resources){
        //
    }
}
