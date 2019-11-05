package controller.states.ExcommunicationStates;

import model.cards.developmentcards.Resources;
import model.players.Player;

import java.io.Serializable;

/**
 * If the player has taken an excommunication which will give him a malus on the victoryPoints
 * received at the end of the game
 */
@FunctionalInterface
public interface ExcommunicationVictoryPointsToSubtractState extends Serializable {
     Resources victoryPointsToSubtract(Player player);
}
