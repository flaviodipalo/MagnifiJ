package controller.states;

import java.io.Serializable;

/**
 * this interface represent the State for the method getSupportBonus
 */
@FunctionalInterface
public interface ShowSupportBonusState extends Serializable {
    int getSupportBonus();
}
