package controller.states;

import model.gameboard.Position;

/**
 * Ludovico Ariosto state for the method is isPositionOccupied
 */
public class IsPositionOccupiedLudovicoAriostoState implements IsPositionOccupiedState {
    private IsPositionOccupiedState state;

    public IsPositionOccupiedLudovicoAriostoState(IsPositionOccupiedState state) {
        this.state = state;
    }

    public boolean isPositionOccupied(Position position){
        return false;
    }
}
