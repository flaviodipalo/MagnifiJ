package controller.states;

import model.gameboard.Position;

import java.io.Serializable;

/**
 * this interface represent the State for the method isPositionOccupied
 */

@FunctionalInterface
public interface IsPositionOccupiedState extends Serializable {
    boolean isPositionOccupied(Position position);

}
