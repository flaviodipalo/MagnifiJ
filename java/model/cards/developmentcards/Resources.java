package model.cards.developmentcards;

import view.cli.Display;
import view.cli.Emoji;

import java.io.Serializable;

/**
 * This class contains all the types of resources present in the game
 */
public class Resources implements Serializable{
    private int wood;
    private int coins;
    private int servants;
    private int stone;
    private int victoryPoints;
    private int faithPoints;
    private int militaryPoints;
    private int councilPrivilege;
    private int emojiCharacters;

    public Resources(){
        this.coins = 0;
        this.wood = 0;
        this.stone = 0;
        this.servants = 0;
        this.victoryPoints = 0;
        this.faithPoints = 0;
        this.militaryPoints = 0;
        this.councilPrivilege = 0;

    }

    /**
     * this method is used to make a shallow copy of resource object
     * @param another resource to copy
     */
    public Resources(Resources another){
        this.coins = another.coins;
        this.wood = another.wood;
        this.stone = another.stone;
        this.servants = another.servants;
        this.victoryPoints = another.victoryPoints;
        this.faithPoints = another.faithPoints;
        this.militaryPoints = another.militaryPoints;
        this.councilPrivilege = another.councilPrivilege;
    }
    @SuppressWarnings("all")
    public Resources(int wood, int coins, int servants, int stone, int victoryPoints, int faithPoints, int militaryPoints, int councilPrivilege){
        this.coins = coins;
        this.wood = wood;
        this.stone = stone;
        this.servants = servants;
        this.victoryPoints = victoryPoints;
        this.faithPoints = faithPoints;
        this.militaryPoints = militaryPoints;
        this.councilPrivilege = councilPrivilege;
    }



    public int getCoins(){
        return this.coins;
    }
    public int getWood(){
        return this.wood;
    }
    public int getServants(){
        return this.servants;
    }
    public int getStone(){
        return this.stone;
    }
    public int getFaithPoints(){
        return this.faithPoints;
    }
    public int getMilitaryPoints(){
        return this.militaryPoints;
    }
    public int getVictoryPoints(){
        return this.victoryPoints;
    }
    public int getCouncilPrivilege() {
        return councilPrivilege;
    }

    public void printResources(){
        if(coins!=0){
            Display.println("\t-coins: " + coins);
        }
        if(wood!=0){
            Display.println("\t-wood: " + wood);
        }
        if(servants!=0){
            Display.println("\t-servants: " + servants);
        }
        if (stone!=0){
            Display.println("\t-stone: " + stone);
        }
        if(victoryPoints!=0){
            Display.println("\t-victoryPoints: " + victoryPoints);
        }
        if(militaryPoints !=0){
            Display.println("\t-militaryPoints: " + militaryPoints);
        }
        if (councilPrivilege != 0) {

            Display.println("\t-councilPrivileges: " + councilPrivilege);
        }
        if (faithPoints != 0){
            Display.println("\t-faithPoints: " + faithPoints);
        }
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        if(coins != 0) {
            builder.append(Emoji.COINS);
            builder.append(coins);
            builder.append(" ");
            emojiCharacters += 1;
        }
        if(wood!=0) {
            builder.append(Emoji.WOOD);
            builder.append(wood);
            builder.append(" ");
            emojiCharacters += 1;
        }
        if(servants!=0){
            builder.append(Emoji.SERVANTS);
            builder.append(servants);
            builder.append(" ");
            emojiCharacters += 0;
        }
        if(stone != 0) {
            builder.append(Emoji.STONE);
            builder.append(stone);
            builder.append(" ");
        }
        if(victoryPoints != 0) {
            builder.append(Emoji.VICTORY_POINTS);
            builder.append(victoryPoints);
            builder.append(" ");
            emojiCharacters += 1;
        }
        if(militaryPoints != 0) {
            builder.append(Emoji.MILITARY_POINTS);
            builder.append(militaryPoints);
            builder.append(" ");
        }
        if(councilPrivilege != 0) {
            builder.append(Emoji.COUNCIL_PRIVILEGE);
            builder.append(councilPrivilege);
            builder.append( " ");
            emojiCharacters += 0;
        }
        if(faithPoints != 0) {
            builder.append(Emoji.FAITH_POINTS);
            builder.append(faithPoints);
            builder.append(" ");
            emojiCharacters += 0;
        }
        return builder.toString();
    }

    public int getEmojiCharacters(){
        return emojiCharacters;
    }

    public void addResources(Resources resourcesToAdd){
        this.coins += resourcesToAdd.getCoins();
        this.wood +=  resourcesToAdd.getWood();
        this.stone +=  resourcesToAdd.getStone();
        this.servants +=  resourcesToAdd.getServants();
        this.victoryPoints +=  resourcesToAdd.getVictoryPoints();
        this.faithPoints +=  resourcesToAdd.getFaithPoints();
        this.militaryPoints +=  resourcesToAdd.getMilitaryPoints();
        this.councilPrivilege +=  resourcesToAdd.getCouncilPrivilege();
    }



    public void subtractResources(Resources resourcesToAdd){
        this.coins -= resourcesToAdd.getCoins();
        this.wood -= resourcesToAdd.getWood();
        this.stone -= resourcesToAdd.getStone();
        this.servants -= resourcesToAdd.getServants();
        this.victoryPoints -= resourcesToAdd.getVictoryPoints();
        this.faithPoints -= resourcesToAdd.getFaithPoints();
        this.militaryPoints -= resourcesToAdd.getMilitaryPoints();
        this.councilPrivilege -= resourcesToAdd.getCouncilPrivilege();
    }

    public boolean isPositive(){
        return coins >= 0 && wood >= 0 && stone >= 0 && servants >= 0 && victoryPoints >= 0 && faithPoints >= 0 && militaryPoints >= 0 && councilPrivilege >= 0;
    }

    public Resources multiplyResources(int mult){
        Resources initializeResources = new Resources();
        initializeResources.addResources(this);
        for (;mult>1;mult--){
            this.addResources(initializeResources);
        }
        return initializeResources;
    }

    boolean isEmpty(){
        return !(coins > 0 && wood > 0 && stone > 0 && servants > 0
                && militaryPoints > 0 && faithPoints > 0 && victoryPoints > 0 && councilPrivilege > 0);
    }

    public boolean isWood() {
        if(wood==1&&coins==0&&stone==0&&servants==0&&militaryPoints==0&&faithPoints==0&&victoryPoints==0&&councilPrivilege==0)
            return true;
        return false;
    }
    public boolean isCoins() {
        if(wood==0&&coins==1&&stone==0&&servants==0&&militaryPoints==0&&faithPoints==0&&victoryPoints==0&&councilPrivilege==0)
            return true;
        return false;

    }
    public boolean isStone() {
        if(wood==0&&coins==0&&stone==1&&servants==0&&militaryPoints==0&&faithPoints==0&&victoryPoints==0&&councilPrivilege==0)
            return true;
        return false;

    }
    public boolean isServants() {
        if(wood==0&&coins==0&&stone==0&&servants==1&&militaryPoints==0&&faithPoints==0&&victoryPoints==0&&councilPrivilege==0)
            return true;
        return false;

    }
    public boolean isMilitaryPoints() {
        if(wood==0&&coins==0&&stone==0&&servants==0&&militaryPoints==1&&faithPoints==0&&victoryPoints==0&&councilPrivilege==0)
            return true;
        return false;
    }
    public boolean isFaithPoints() {
        if(wood==0&&coins==0&&stone==0&&servants==0&&militaryPoints==0&&faithPoints==1&&victoryPoints==0&&councilPrivilege==0)
            return true;
        return false;
    }
    public boolean isVictoryPoints() {
        if(wood==0&&coins==0&&stone==0&&servants==0&&militaryPoints==0&&faithPoints==0&&victoryPoints==1&&councilPrivilege==0)
            return true;
        return false;
    }
    public boolean isCouncilPrivilege() {
        if(wood==0&&coins==0&&stone==0&&servants==0&&militaryPoints==0&&faithPoints==0&&victoryPoints==0&&councilPrivilege==1)
            return true;
        return false;
    }

    public boolean equals(Object r){
        Resources re = (Resources)r;
        return re.getCouncilPrivilege() == this.councilPrivilege && re.getMilitaryPoints() == this.militaryPoints && re.getCoins() == this.coins && re.getServants() == servants && re.getWood() == wood && re.getStone() == stone && re.getFaithPoints() == faithPoints;
    }

    public void resetCouncilPrivilege() {
        councilPrivilege = 0;
    }
}
