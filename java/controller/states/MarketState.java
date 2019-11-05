package controller.states;

import controller.exceptions.NotEnoughValueException;
import model.cards.developmentcards.Resources;
import model.gameboard.MarketPosition;
import model.players.FamilyMember;

import java.io.Serializable;

/**
 * this interface represent the Market state for the method goToMarket
 */
@FunctionalInterface

public interface MarketState extends Serializable {

    Resources goToMarket(FamilyMember familyMember, MarketPosition marketPosition) throws NotEnoughValueException;
}
