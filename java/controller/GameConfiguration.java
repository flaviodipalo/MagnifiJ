package controller;

import model.cards.developmentcards.CardColor;
import model.cards.developmentcards.Resources;
import util.Parser;
import view.cli.Display;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 * The class representing the configurations of the game
 */
public class GameConfiguration implements Serializable{

    private static GameConfiguration instance;
    private int maxFamilyMemberNumber;
    private int maxPlayerNumber;
    private int startingWood;
    private int startingServants;
    private int startingStones;
    private int startingCoins;
    private int startingMilitaryPoints;
    private int startingFaithPoints;
    private int startingVictoryPoints;
    private int startingCouncilPrivilege;
    private int maxNumberOfDevelopmentCards;
    private int numberOfPlayerTurn;
    private int numberOfTurns;
    private int leaderCardsToChoose;
    private int numberOfDices;
    private int[] faithPathPoints;
    private Resources[] bonusResourcesMarketPositions;
    private Resources bonusCouncilPosition;
    private  int[] militaryPointsNeededForGreenCard;
    private  int[] victoryPointsGotForGreenCard;
    private  int[] victoryPointsGotForBlueCard;
    private  int[] faithPointsNeededToAvoidExcommunication;
    private ArrayList<Resources> councilPrivilegeToResources;
    private Resources[] greenTowerFieldBonus;
    private Resources[] blueTowerFieldBonus;
    private Resources[] yellowTowerFieldBonus;
    private Resources[] purpleTowerFieldBonus;
    private Map<CardColor, Resources[]> fieldBonusMap;
    private int generalTimeout;
    private int roomTimer;


    private GameConfiguration(int maxFamilyMemberNumber, int maxPlayerNumber,
                              int startingWood, int startingServants, int startingStones,
                              int startingCoins, int startingMilitaryPoints, int startingFaithPoints,
                              int startingVictoryPoints, int startingCouncilPrivilege,
                              int maxNumberOfDevelopmentCards, int numberOfPlayerTurn,
                              int numberOfTurns, int leaderCardsToChoose, int numberOfDices, Resources[] bonusResourcesMarketPositions,
                              int[] faithPathPoints, Resources bonusCouncilPosition, int [] militaryPointsNeededForGreenCard,
                              int [] victoryPointsGotForGreenCard, int[] victoryPointsGotForBlueCard,
                              int[] faithPointsNeededToAvoidExcommunication, Resources[] greenTowerFieldBonus,
                              Resources[] blueTowerFieldBonus, Resources[] yellowTowerFieldBonus,
                              Resources[] purpleTowerFieldBonus, ArrayList<Resources> councilPrivilegeToResources,
                              int generalTimeout, int roomTimer) {
        this.maxFamilyMemberNumber = maxFamilyMemberNumber;
        this.maxPlayerNumber = maxPlayerNumber;
        this.startingWood = startingWood;
        this.startingServants = startingServants;
        this.startingStones = startingStones;
        this.startingCoins = startingCoins;
        this.startingMilitaryPoints = startingMilitaryPoints;
        this.startingFaithPoints = startingFaithPoints;
        this.startingVictoryPoints = startingVictoryPoints;
        this.startingCouncilPrivilege = startingCouncilPrivilege;
        this.maxNumberOfDevelopmentCards = maxNumberOfDevelopmentCards;
        this.numberOfPlayerTurn = numberOfPlayerTurn;
        this.numberOfTurns = numberOfTurns;
        this.leaderCardsToChoose = leaderCardsToChoose;
        this.numberOfDices = numberOfDices;
        this.bonusResourcesMarketPositions = bonusResourcesMarketPositions;
        this.faithPathPoints = faithPathPoints;
        this.bonusCouncilPosition = bonusCouncilPosition;
        this.militaryPointsNeededForGreenCard = militaryPointsNeededForGreenCard;
        this.victoryPointsGotForBlueCard = victoryPointsGotForBlueCard;
        this.militaryPointsNeededForGreenCard = militaryPointsNeededForGreenCard;
        this.victoryPointsGotForGreenCard = victoryPointsGotForGreenCard;
        this.faithPointsNeededToAvoidExcommunication = faithPointsNeededToAvoidExcommunication;
        this.greenTowerFieldBonus = greenTowerFieldBonus;
        this.blueTowerFieldBonus = blueTowerFieldBonus;
        this.yellowTowerFieldBonus = yellowTowerFieldBonus;
        this.purpleTowerFieldBonus = purpleTowerFieldBonus;
        this.councilPrivilegeToResources = councilPrivilegeToResources;
        this.generalTimeout = generalTimeout;
        this.roomTimer = roomTimer;
    }

    /**
     *
     * @return a ready to play configuration class created after
     * have parsed a json config file. In case the file is not available
     * a default constructor is created
     */
    public static GameConfiguration getInstance() {
        if(instance == null){
            try {
                Parser parser = new Parser();
                instance =  parser.createConfig();
                instance.initFieldBonusMap();
            }catch (IOException e){
                Display.println("Config file not found!" , e);
                instance = createBasicConfigFile();

            }
        }
        return instance;

    }

    private void initFieldBonusMap(){
        fieldBonusMap = new EnumMap<>(CardColor.class);
        fieldBonusMap.put(CardColor.GREEN, greenTowerFieldBonus);
        fieldBonusMap.put(CardColor.BLUE, blueTowerFieldBonus);
        fieldBonusMap.put(CardColor.YELLOW, yellowTowerFieldBonus);
        fieldBonusMap.put(CardColor.PURPLE, purpleTowerFieldBonus);
    }

    /**
     *
     * @return a basic configuration file in case the config file is missing.
     */
    public static GameConfiguration createBasicConfigFile() {
        int[] faithPathPoints = {0, 1, 2, 3, 4, 5, 7, 9, 11, 13, 15, 17, 19, 22, 25, 30};
        Resources[] bonusResourcesMarketPositions = {new Resources(0,5,0,0,0,0,0,0),
                new Resources(0,0,5,0,0,0,0,0),
                new Resources(0,2,0,0,0,0,3,0),
                new Resources(0,0,0,0,0,0,0,3)};
        Resources bonusCouncilPosition = new Resources(0,1,0,0,0,0,0,1);
        int[] militaryPointsNeededForGreenCard = {0,0,3,7,12,18};
        int[] victoryPointsGotForGreenCard = {0,0,0,1,4,10,20};
        int[] victoryPointsGotForBlueCard = {0,1,3,6,10,15,21};
        int[] faithPointsNeededToAvoidExcommunication = {3,4,5};
        Resources[] greenTowerFieldBonus= new Resources[]{new Resources(), new Resources(), new Resources(1, 0, 0, 0, 0, 0, 0, 0), new Resources(2, 0, 0, 0, 0, 0, 0, 0)};
        Resources[] blueTowerFieldBonus= new Resources[]{new Resources(), new Resources(), new Resources(0, 0, 0, 1, 0, 0, 0, 0), new Resources(0, 0, 0, 2, 0, 0, 0, 0)};
        Resources[] yellowTowerFieldBonus= new Resources[]{new Resources(), new Resources(), new Resources(0, 0, 0, 0, 0, 0, 1, 0), new Resources(0, 0, 0, 0, 0, 0, 2, 0)};
        Resources[] purpleTowerFieldBonus= new Resources[]{new Resources(), new Resources(), new Resources(0, 1, 0, 0, 0, 0, 0, 0), new Resources(0, 2, 0, 0, 0, 0, 0, 0)};
        ArrayList<Resources> councilPrivilegeToResources = new ArrayList<>();
        councilPrivilegeToResources.add(new Resources(1, 0, 0, 1, 0, 0, 0, 0));
        councilPrivilegeToResources.add(new Resources(0, 0, 2, 0, 0, 0, 0, 0));
        councilPrivilegeToResources.add(new Resources(0, 2, 0, 0, 0, 0, 0, 0));
        councilPrivilegeToResources.add(new Resources(0, 0, 0, 0, 0, 0, 2, 0));
        councilPrivilegeToResources.add(new Resources(0, 0, 0, 0, 0, 1, 0, 0));

        int generalTimeout = 60;
        int roomTimer = 20;


        return new GameConfiguration(4,4,2,
                3,2,5,0,
                0,0,0,
                6,4,6,
                4,3, bonusResourcesMarketPositions, faithPathPoints,
                bonusCouncilPosition, militaryPointsNeededForGreenCard, victoryPointsGotForGreenCard,
                victoryPointsGotForBlueCard, faithPointsNeededToAvoidExcommunication, greenTowerFieldBonus, blueTowerFieldBonus, yellowTowerFieldBonus,
                purpleTowerFieldBonus, councilPrivilegeToResources, generalTimeout, roomTimer);
    }




    int getStartingWood() {
        return startingWood;
    }

    int getStartingServants() {
        return startingServants;
    }

    int getStartingStones() {
        return startingStones;
    }

    int getStartingCoins() {
        return startingCoins;
    }

    int getStartingMilitaryPoints() {
        return startingMilitaryPoints;
    }

    int getStartingFaithPoints() {
        return startingFaithPoints;
    }

    int getStartingVictoryPoints() {
        return startingVictoryPoints;
    }

    int getStartingCouncilPrivilege() {
        return startingCouncilPrivilege;
    }

    public int getMaxNumberOfDevelopmentCards() {
        return maxNumberOfDevelopmentCards;
    }

    int getNumberOfPlayerTurn() {
        return numberOfPlayerTurn;
    }

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    int getLeaderCardsToChoose() {
        return leaderCardsToChoose;
    }

    public int getNumberOfDices() {
        return numberOfDices;
    }

    public Resources[] getBonusResourcesMarketPositions() {
        return bonusResourcesMarketPositions;
    }

    public int[] getFaithPathPoints() {
        return faithPathPoints;
    }


    public Resources getBonusCouncilPosition() {
        return bonusCouncilPosition;
    }

    public int[] getMilitaryPointsNeededForGreenCard() {
        return militaryPointsNeededForGreenCard;
    }

    public int[] getVictoryPointsGotForGreenCard() {
        return victoryPointsGotForGreenCard;
    }

    public int[] getVictoryPointsGotForBlueCard() {
        return victoryPointsGotForBlueCard;
    }

    public int[] getFaithPointsNeededToAvoidExcommunication() {
        return faithPointsNeededToAvoidExcommunication;
    }

    ArrayList<Resources> getCouncilPrivilegeToResources() {
        return councilPrivilegeToResources;
    }

    public Map<CardColor, Resources[]> getFieldBonusMap() {
        return fieldBonusMap;
    }

    public int getGeneralTimeOut() {
        return generalTimeout;
    }

    public int getRoomTimer() {
        return roomTimer;
    }
}
