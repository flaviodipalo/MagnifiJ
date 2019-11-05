package controller;

import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import java.io.Serializable;
import java.util.*;

public class Deck implements Serializable{
    private List<GreenCard> greenCard;
    private List<BlueCard> blueCard;
    private List<YellowCard> yellowCard;
    private List<PurpleCard> purpleCard;
    private List<LeaderCard> leaderCards;
    private List<BonusTile> bonusTiles;
    private List<ExcommunicationCard> excommunicationCards1;
    private List<ExcommunicationCard> excommunicationCards2;
    private List<ExcommunicationCard> excommunicationCards3;



    public Deck(List<GreenCard> greenCard, List<BlueCard> blueCard, List<YellowCard> yellowCard, List<PurpleCard> purpleCard, List<LeaderCard> leaderCards,
                List<BonusTile> bonusTiles, List<ExcommunicationCard> excommunicationCards1, List<ExcommunicationCard> excommunicationCards2,
                List<ExcommunicationCard> excommunicationCards3) {
        this.greenCard = greenCard;
        this.blueCard = blueCard;
        this.yellowCard = yellowCard;
        this.purpleCard = purpleCard;
        this.leaderCards = leaderCards;
        this.bonusTiles = bonusTiles;
        this.excommunicationCards1 = excommunicationCards1;
        this.excommunicationCards2 = excommunicationCards2;
        this.excommunicationCards3 = excommunicationCards3;

    }

    public void setLeaderCards(List<LeaderCard> leaderCards) {
        this.leaderCards = leaderCards;
    }

    public List<LeaderCard> getFourLeaderCard(){
        List<LeaderCard> fourLeaderCards = new LinkedList<>();
        for(int i = 0; i < 4; i++)
            fourLeaderCards.add(leaderCards.remove(i));
        return fourLeaderCards;
    }


    /**
     * creates a new instance of the deck
     * @param aDeck singleton
     * @return a deck new instance
     */
    public static Deck newInstance(Deck aDeck){
        return new Deck(aDeck.getGreenCards(), aDeck.getBlueCards(), aDeck.getYellowCards(),
                aDeck.getPurpleCards(), aDeck.getLeaderCards(), aDeck.getBonusTiles(),
                aDeck.getExcommunicationCards1(), aDeck.getExcommunicationCards2(), aDeck.getExcommunicationCards3());
    }

    public List<BlueCard> getBlueCards() {
        return blueCard;
    }
    public List<GreenCard> getGreenCards() {
        return greenCard;
    }
    public List<PurpleCard> getPurpleCards(){
        return purpleCard;
    }
    public List<YellowCard> getYellowCards(){
        return yellowCard;
    }
    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }
    public List<BonusTile> getBonusTiles() {
        return bonusTiles;
    }

    /**
     * @return a List of exactly four green cards.
     */
    public ArrayList<GreenCard> getFourGreenCards(){

        ArrayList<GreenCard> listToReturn = new ArrayList<>();
        for (int i=0;i<4;i++){
            listToReturn.add(greenCard.remove(0));
        }
        return listToReturn;
    }
    /**
     * @return a List of exactly four yellow cards.
     */
    public List<YellowCard> getFourYellowCards(){
        List<YellowCard> listToReturn = new ArrayList<>();
        for (int i=0;i<4;i++){
            listToReturn.add(yellowCard.remove(0));
        }

        return listToReturn;
    }
    /**
     * @return a List of exactly four blue cards.
     */
    public List<BlueCard> getFourBlueCards(){
        List<BlueCard> listToReturn = new ArrayList<>();
        for (int i=0;i<4;i++){
            listToReturn.add(blueCard.remove(0));
        }
        return listToReturn;
    }
    /**
     * @return a List of exactly four purple cards.
     */
    public List<PurpleCard> getFourPurpleCards(){
        List<PurpleCard> listToReturn = new ArrayList<>();
        for (int i=0;i<4;i++){
            listToReturn.add(purpleCard.remove(0));
        }
        return listToReturn;
    }
    /**
     * @return an array of exactly tre excommunication cards.
     */
    public ExcommunicationCard[] getExcommunicationCards() {
        Collections.shuffle(excommunicationCards1);
        Collections.shuffle(excommunicationCards2);
        Collections.shuffle(excommunicationCards3);
        ExcommunicationCard[] excommunicationCardsToPlace = new ExcommunicationCard[3];
        excommunicationCardsToPlace[0] = excommunicationCards1.get(0);
        excommunicationCardsToPlace[1] = excommunicationCards2.get(0);
        excommunicationCardsToPlace[2] = excommunicationCards3.get(0);
        return excommunicationCardsToPlace;
    }
    /**
     * Shuffle the cards in the deck
     */
    void shuffleDeck() {
        shuffleLeaderCards(getLeaderCards());
        shuffleTowerCards();
    }

    /**
     * Shuffle the leader cards in the deck
     * @param leaderCards the cards to shuffle
     */
    private void shuffleLeaderCards(List<LeaderCard> leaderCards) {
        Collections.shuffle(leaderCards);
        setLeaderCards(leaderCards);
    }


    public void shuffleTowerCards(){
    Collections.shuffle(greenCard);
    Collections.shuffle(blueCard);
    Collections.shuffle(yellowCard);
    Collections.shuffle(purpleCard);

    greenCard.sort(Comparator.comparing(Card::getPeriod));
    blueCard.sort(Comparator.comparing(Card::getPeriod));
    yellowCard.sort(Comparator.comparing(Card::getPeriod));
    purpleCard.sort(Comparator.comparing(Card::getPeriod));
    }


    public  List<ExcommunicationCard> getExcommunicationCards1() {
        return excommunicationCards1;
    }

    public List<ExcommunicationCard> getExcommunicationCards2() {
        return excommunicationCards2;
    }

    public List<ExcommunicationCard> getExcommunicationCards3() {
        return excommunicationCards3;
    }

}
