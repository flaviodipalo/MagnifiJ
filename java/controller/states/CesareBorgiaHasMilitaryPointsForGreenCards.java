package controller.states;

/**
 * This state represent the permanent effect of Cesare Borgia, it wraps the original HasMilitaryPointsForGreenCard of the player
 * with a new state that returns true in each condition.
 */
public class CesareBorgiaHasMilitaryPointsForGreenCards implements HasMilitaryPointsForGreenCardsState {
    private HasMilitaryPointsForGreenCardsState state;

    public CesareBorgiaHasMilitaryPointsForGreenCards(HasMilitaryPointsForGreenCardsState state) {
        this.state = state;
    }

    public boolean hasMilitaryPointsForGreenCards() {
        return true;
    }
}
