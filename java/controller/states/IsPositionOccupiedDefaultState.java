package controller.states;

import model.gameboard.Position;

/**
 * Default state for the method is isPositionOccupied
 */
public class IsPositionOccupiedDefaultState implements IsPositionOccupiedState {
    public IsPositionOccupiedDefaultState() {
    }

    //this method returns true if the Chosen Position is occupied by a Family Member
    public boolean isPositionOccupied(Position position){
        return !(position.isEmpty());
    }
}
