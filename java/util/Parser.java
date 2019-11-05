package util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import controller.Deck;
import controller.GameConfiguration;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.*;
import controller.states.ExcommunicationStates.*;
import view.cli.Display;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used by the server to parse all the game resources from json files
 */
public class Parser {
    private static final String RESOURCE = System.getProperty("user.dir") + "/src/main/resources";
    private static final String DEV_CARDS_PATH = RESOURCE + "/config/cards.json";
    private static final String BONUS_TILES_PATH = RESOURCE + "/config/bonusTiles.json";
    private static final String LEADER_CARDS_PATH = RESOURCE + "/config/leaderCards.json";
    private static final String EXCOMMUNICATION_CARDS_PATH = RESOURCE + "/config/excommunicationCards.json";
    private static final String CONFIG = RESOURCE + "/config/config.json";
    private FileReader fr = null;
    private BufferedReader br = null;
    private ArrayList<GreenCard> greenCards;
    private ArrayList<BlueCard> blueCards;
    private ArrayList<YellowCard> yellowCards;
    private ArrayList<PurpleCard> purpleCards;
    private ArrayList<BonusTile> bonusTiles;
    private ArrayList<LeaderCard> leaderCards;
    private ArrayList<ExcommunicationCard> excommunications1;
    private ArrayList<ExcommunicationCard> excommunications2;
    private ArrayList<ExcommunicationCard> excommunications3;



    public Parser(){
        excommunications1 = new ArrayList<>();
        excommunications2 = new ArrayList<>();
        excommunications3 = new ArrayList<>();
        bonusTiles = new ArrayList<>();
        greenCards = new ArrayList<>();
        blueCards = new ArrayList<>();
        yellowCards = new ArrayList<>();
        purpleCards = new ArrayList<>();
        leaderCards = new ArrayList<>();
    }

    /**
     * Creates a configuration class containing all the values needed fort the game
     * to start
     * @return configuration class
     * @throws IOException if the file is missing
     */

    public GameConfiguration createConfig() throws IOException {
        Gson gson = new Gson();
        createBufferedReader(CONFIG);
        JsonParser jsonParser = new JsonParser();
        JsonObject configObj = jsonParser.parse(br).getAsJsonObject();
        GameConfiguration config = gson.fromJson(configObj, GameConfiguration.class);
        closeBufferedReader();
        return config;

    }
    /**
     * This method parse the leader cards contained in the config file
     * @throws IOException if the config file is not available
     */

    private void parseLeaderCardsFromJson() throws IOException{
        createBufferedReader(LEADER_CARDS_PATH);
        JsonParser jsonParser = new JsonParser();
        JsonObject object = jsonParser.parse(br).getAsJsonObject();

        JsonArray perTurnLeaderCards = object.get("perTurnLeaderCards").getAsJsonArray();
        JsonArray permanentLeaderCards = object.get("permanentLeaderCards").getAsJsonArray();

        for(int arrayIndex = 0; arrayIndex < perTurnLeaderCards.size(); arrayIndex++ ){
            JsonObject current = perTurnLeaderCards.get(arrayIndex).getAsJsonObject();
            createPerTurnLeaderCard(current);
        }
        for (int arrayIndex = 0; arrayIndex < permanentLeaderCards.size(); arrayIndex++){
            JsonObject current = permanentLeaderCards.get(arrayIndex).getAsJsonObject();
            createPermanentLeaderCard(current);
        }

        closeBufferedReader();
    }


    /**
     * This method creates a type of leader card
     * @param leaderCardObj json objected linked to the current card in the json file
     */
    private void createPerTurnLeaderCard(JsonObject leaderCardObj){
        String name = leaderCardObj.get("name").getAsString();
        String id = leaderCardObj.get("id").getAsString();
        String description = leaderCardObj.get("description").getAsString();
        ArrayList<LeaderRequirements> leaderRequirements;
        if(leaderCardObj.has("leaderRequirements")){
            leaderRequirements = getRequirements(leaderCardObj.get("leaderRequirements").getAsJsonArray());
        }
        else {
            leaderRequirements = null;
        }
        LeaderReward leaderReward;
        if(leaderCardObj.has("leaderReward")){
            leaderReward = getLeaderReward(leaderCardObj.get("leaderReward").getAsJsonObject());
        }
        else {
            leaderReward = null;
        }


        LeaderCard  leaderCard = new LeaderCardPerTurn(name, id,  leaderRequirements, leaderReward, description);

        leaderCards.add(leaderCard);



    }

    /**
     *
     * @param leaderReward json object linked to the current card's leader reward
     * @return the card's leader reward of
     */
    private LeaderReward getLeaderReward(JsonObject leaderReward){
        Gson gson = new Gson();
        if(leaderReward.has("leaderRewardHarvest")){
            JsonObject leaderRewardHarvestObj = leaderReward.get("leaderRewardHarvest").getAsJsonObject();
            return gson.fromJson(leaderRewardHarvestObj, LeaderRewardHarvest.class);
        }else if(leaderReward.has("leaderRewardFedericoDaMontefeltro")){
            JsonObject leaderRewardFedericoDaMontefeltroObj = leaderReward.get("leaderRewardFedericoDaMontefeltro").getAsJsonObject();
            return gson.fromJson(leaderRewardFedericoDaMontefeltroObj, LeaderRewardFedericoDaMontefeltro.class);
        }
        else if(leaderReward.has("leaderRewardProduction")){
            JsonObject leaderRewardProductionObj = leaderReward.get("leaderRewardProduction").getAsJsonObject();
            return gson.fromJson(leaderRewardProductionObj, LeaderRewardProduction.class);
        }
        else {
            JsonObject leaderRewardResourcesObj = leaderReward.get("leaderRewardResources").getAsJsonObject();
            return gson.fromJson(leaderRewardResourcesObj, LeaderRewardResources.class);
        }
    }

    /**
     *
     * @param requirementsArray json array linked to the current cards' requirement field
     * @return an array list of leader requirements of the current card
     */
    private ArrayList<LeaderRequirements> getRequirements(JsonArray requirementsArray){
        Gson gson = new Gson();
        ArrayList<LeaderRequirements> requirements = new ArrayList<>();
        for (int requirementsIndex = 0; requirementsIndex<requirementsArray.size(); requirementsIndex++){

            JsonObject requirementObject = requirementsArray.get(requirementsIndex).getAsJsonObject();

            LeaderRequirements requirement = gson.fromJson(requirementObject, LeaderRequirements.class);
            requirements.add(requirement);

        }

        return requirements;
    }

    /**
     * This method creates another type of leader card
     * @param leaderCardObj json object linked to the current leader card field in the json file
     */
    private void createPermanentLeaderCard(JsonObject leaderCardObj){
        Gson gson = new Gson();

        LeaderCardPermanent leaderCard = gson.fromJson(leaderCardObj, LeaderCardPermanent.class);

        leaderCards.add(leaderCard);


    }

    /**
     *
     * @throws IOException if the file is not available in the config folder
     */

    private void parseExcommunicationCards() throws IOException{
        createBufferedReader(EXCOMMUNICATION_CARDS_PATH);
        JsonParser jsonParser = new JsonParser();
        JsonObject object = jsonParser.parse(br).getAsJsonObject();
        JsonArray excommunicationCards1 = object.getAsJsonArray("excommunicationCards1");
        JsonArray excommunicationCards2 = object.getAsJsonArray("excommunicationCards2");
        JsonArray excommunicationCards3 = object.getAsJsonArray("excommunicationCards3");

        for (int i = 0; i<excommunicationCards1.size(); i++){
            JsonObject cardObj = excommunicationCards1.get(i).getAsJsonObject();
            createExcommunicationCard(cardObj);
        }
        for (int i = 0 ; i<excommunicationCards2.size(); i++){
            JsonObject cardObj = excommunicationCards2.get(i).getAsJsonObject();
            createExcommunicationCard(cardObj);
        }
        for (int i = 0 ; i<excommunicationCards3.size(); i++){
            JsonObject cardObj = excommunicationCards3.get(i).getAsJsonObject();
            createExcommunicationCard(cardObj);
        }

        closeBufferedReader();
    }

    /**
     * This method creates an excommunication card
     * @param cardObj json object linked to the excommunication card's field in the json file
     */
    private void createExcommunicationCard(JsonObject cardObj){
        try {
            String id = cardObj.get("id").getAsString();
            String description = cardObj.get("description").getAsString();
            Period period = getPeriod(cardObj.get("period").getAsString());
            ExcommunicationState excommunicationState = getExcommunicationState(cardObj.get("excommunicationState").getAsJsonObject());
            ExcommunicationCard card = new ExcommunicationCard(id, period, excommunicationState, description);
            if(period != null){
                switch (period.toInt()){
                    case 1 :
                        excommunications1.add(card);
                        break;
                    case 2 :
                        excommunications2.add(card);
                        break;
                    default:
                        excommunications3.add(card);

                }
            }
            else throw new ExcommunicationNotFoundException();


        }catch (ExcommunicationNotFoundException e){
            Display.println(e);
        }

    }

    /**
     *
     * @param excommunication json object linked to the excommunication field of the current card
     * @return an excommunication
     * @throws ExcommunicationNotFoundException if something wrong with the json file
     */
    private ExcommunicationState getExcommunicationState(JsonObject excommunication) throws ExcommunicationNotFoundException {
        Gson gson = new Gson();
        if(excommunication.has("excommunicationAddResourcesState")){
            JsonObject exResources = excommunication.get("excommunicationAddResourcesState").getAsJsonObject();
            return gson.fromJson(exResources, ExcommunicationAddResourcesState.class);
        }
        else if(excommunication.has("excommunicationHarvestState")){
            JsonObject exHarvest = excommunication.get("excommunicationHarvestState").getAsJsonObject();
            return gson.fromJson(exHarvest, ExcommunicationHarvestState.class);
        }
        else if(excommunication.has("excommunicationProductionState")){
            JsonObject exProduction = excommunication.get("excommunicationProductionState").getAsJsonObject();
            return gson.fromJson(exProduction, ExcommunicationProductionState.class);
        }else if(excommunication.has("excommunicationFamilyMemberState")){
            JsonObject exFamily = excommunication.get("excommunicationFamilyMemberState").getAsJsonObject();
            return gson.fromJson(exFamily, ExcommunicationFamilyMemberState.class);
        }else if (excommunication.has("excommunicationPickCardState")){
            JsonObject exPick = excommunication.get("excommunicationPickCardState").getAsJsonObject();
            return gson.fromJson(exPick, ExcommunicationPickCardState.class);
        }
        else if(excommunication.has("excommunicationMarketState")){
            JsonObject exMarket = excommunication.get("excommunicationMarketState").getAsJsonObject();
            return gson.fromJson(exMarket, ExcommunicationMarketState.class);
        }
        else if(excommunication.has("excommunicationAddServantsState")){
            JsonObject exServants = excommunication.get("excommunicationAddServantsState").getAsJsonObject();
            return gson.fromJson(exServants, ExcommunicationAddServantsState.class);
        }
        else if(excommunication.has("excommunicationSkipTurnState")){
            JsonObject exTurn = excommunication.get("excommunicationSkipTurnState").getAsJsonObject();
            return gson.fromJson(exTurn, ExcommunicationSkipTurnState.class);
        }
        else if(excommunication.has("excommunicationVictoryPointsToSubtractStateCards")){
            JsonObject exVictory = excommunication.get("excommunicationVictoryPointsToSubtractStateCards").getAsJsonObject();
            return gson.fromJson(exVictory, ExcommunicationVictoryPointsToSubtractStateCards.class);
        }else if(excommunication.has("excommunicationVictoryPointsToSubtractStateVictoryPoints")){
            JsonObject exVictory = excommunication.get("excommunicationVictoryPointsToSubtractStateVictoryPoints").getAsJsonObject();
            return gson.fromJson(exVictory, ExcommunicationVictoryPointsToSubtractStateVictoryPoints.class);
        }
        else if(excommunication.has("excommunicationVictoryPointsToSubtractStateMilitaryPoints")){
            JsonObject exVictory = excommunication.get("excommunicationVictoryPointsToSubtractStateMilitaryPoints").getAsJsonObject();
            return gson.fromJson(exVictory, ExcommunicationVictoryPointsToSubtractStateMilitaryPoints.class);
        }
        else if(excommunication.has("excommunicationVictoryPointsToSubtractStateCardCost")){
            JsonObject exVictory = excommunication.get("excommunicationVictoryPointsToSubtractStateCardCost").getAsJsonObject();
            return gson.fromJson(exVictory, ExcommunicationVictoryPointsToSubtractStateCardCost.class);
        }
        else if(excommunication.has("excommunicationVictoryPointsToSubtractStateResources")){
            JsonObject exVictory = excommunication.get("excommunicationVictoryPointsToSubtractStateResources").getAsJsonObject();
            return gson.fromJson(exVictory, ExcommunicationVictoryPointsToSubtractStateResources.class);
        }
        else {
            throw new ExcommunicationNotFoundException();
        }

    }


    /**
     * this method extracts the bonusTiles from the json file bonusTiles.json
     * @throws IOException if the files is not found
     */
    private void parseBonusTilesFromJson() throws IOException{
        createBufferedReader(BONUS_TILES_PATH);
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(br).getAsJsonObject();
        JsonArray bonusTilesArray = object.get("bonusTiles").getAsJsonArray();

        for(int tilesIndex = 0; tilesIndex<bonusTilesArray.size(); tilesIndex++){
            JsonObject bonusTileObj = bonusTilesArray.get(tilesIndex).getAsJsonObject();
            BonusTile bonusTile = gson.fromJson(bonusTileObj, BonusTile.class);
            bonusTiles.add(bonusTile);

        }
        closeBufferedReader();
    }

    /**
     * this method extracts the model.cards from the json file model.cards.json.
     */
    private void parseDevelopmentCardsFromJson() throws IOException{

        createBufferedReader(DEV_CARDS_PATH);
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(br).getAsJsonObject();
        /*
         * create an array of model.cards as JsonArray and add them to the cardDeck
         */
        JsonArray cardArray = object.get("cards").getAsJsonArray();
        for(int cardIndex=0; cardIndex<cardArray.size(); cardIndex++){
            Card card = createCard(cardArray.get(cardIndex).getAsJsonObject());
            if("green".equals(card.getCardColor().toString()))
            {
                greenCards.add((GreenCard) card);
            }
            else if("blue".equals(card.getCardColor().toString())){
                blueCards.add((BlueCard) card);
            }else if("yellow".equals(card.getCardColor().toString())){
                yellowCards.add((YellowCard)card);
            }
            else if("purple".equals(card.getCardColor().toString())){
                purpleCards.add((PurpleCard)card);
            }

        }
        closeBufferedReader();



    }

    /**
     *
     * @return a complete deck ready to use in a room
     * @throws IOException if one of the json file is missing
     */

    public Deck createDeck() throws IOException{
        parseBonusTilesFromJson();
        parseLeaderCardsFromJson();
        parseDevelopmentCardsFromJson();
        parseExcommunicationCards();
        return new Deck(greenCards, blueCards, yellowCards, purpleCards, leaderCards, bonusTiles, excommunications1, excommunications2,excommunications3);
    }

    /**
     * this method creates a Card object
     * @param card is the current jsonObject of the JsonArray model.cards
     */
    private Card createCard(JsonObject card){

        //get a card from the array

        String cardName = card.get("name").getAsString();
        Period period = getPeriod(card.get("period").getAsString());
        CardColor cardColor = getCardColor(card.get("cardColor").getAsString());
        String id = null;
        if(card.has("id")){
            id = card.get("id").getAsString();
        }




        // if exists it creates an array of costs , if it doesn't returns null. This if statement allows the user
        // to avoid to compile the cost (JsonArray) of green model.cards

        ArrayList<Cost> costs;
        if(card.has("cost")){
             costs = getCosts(card.get("cost").getAsJsonArray());
        }else {
            costs = null;
        }
        ArrayList<InstantReward> instantRewards;
        if(card.has("instantReward")){
             instantRewards = getInstantReward(card.get("instantReward").getAsJsonArray());
        }else{
            instantRewards = null;
        }

        assert cardColor != null;
        if("yellow".equals(cardColor.toString())){
            ArrayList<YellowReward> yellowRewards = getYellowReward(card.get("yellowReward").getAsJsonArray());
            return new YellowCard(id, cardName,period,cardColor,costs,instantRewards, yellowRewards);
        }else if("blue".equals(cardColor.toString())){
            BlueReward blueReward;
            if(card.has("blueReward")){
                blueReward = getBlueReward(card.get("blueReward").getAsJsonObject());
            }else{
                blueReward = null;
            }
            return new BlueCard(id, cardName, period, cardColor, costs, instantRewards, blueReward);
        }else if("purple".equals(cardColor.toString())){
            PurpleReward purpleReward = getPurpleReward(card.get("purpleReward").getAsJsonObject());
            return new PurpleCard(id, cardName, period, cardColor, costs, instantRewards,purpleReward);
        }else {
            GreenReward greenReward = getGreenReward(card.get("greenReward").getAsJsonObject());
            return new GreenCard(id, cardName, period, cardColor, instantRewards, greenReward);
        }

    }

    /**
     * This method parses the blueReward JsonObject and creates the BluereWard class
     * @param blueRewardObj json object linked to the BlueReward
     */
    private BlueReward getBlueReward(JsonObject blueRewardObj){
        Gson gson = new Gson();
        return gson.fromJson(blueRewardObj, BlueReward.class);
    }

    /**
     * This method parses the blueReward JsonObject and creates the bluereward class
     * @param purpleRewardObj json object linked to the PurpleReward class
     */
    private PurpleReward getPurpleReward(JsonObject purpleRewardObj){
        Gson gson = new Gson();
        return gson.fromJson(purpleRewardObj, PurpleReward.class);
    }
    private GreenReward getGreenReward(JsonObject greenRewardObj){
        Gson gson = new Gson();
        return gson.fromJson(greenRewardObj, GreenReward.class);
    }


    /**
     * This method parses the blueReward JsonObject and creates the bluereward class
     * @param yellowRewardArray json arrey linked to the YellowReward
     */
    private ArrayList<YellowReward> getYellowReward(JsonArray yellowRewardArray){
        Gson gson = new Gson();
        ArrayList<YellowReward> yellowRewards = new ArrayList<>();

         // get the current index of yellow reward array

        for (int rewardIndex = 0; rewardIndex<yellowRewardArray.size(); rewardIndex++){
            JsonObject current = yellowRewardArray.get(rewardIndex).getAsJsonObject();
            YellowReward yellowReward = gson.fromJson(current, YellowReward.class);
            yellowRewards.add(yellowReward);
        }

        return yellowRewards;

    }


    /**
     * this method returns an array list of Instant reward that might contain different type of instant reward.
     * @param instantRewardsArray is the json array containing all the card's instant reward
     * @return the instant reward of the current card
     */
    private ArrayList<InstantReward> getInstantReward(JsonArray instantRewardsArray){
        Gson gson = new Gson();
        ArrayList<InstantReward> instantRewards = new ArrayList<>();
        for(int rewardIndex = 0; rewardIndex<instantRewardsArray.size(); rewardIndex++){

            // get the current index of instant reward array

            JsonObject current = instantRewardsArray.get(rewardIndex).getAsJsonObject();

            if(current.has("instantRewardResources")){
                JsonObject type = current.get("instantRewardResources").getAsJsonObject();
                InstantRewardResources instantRewardResources = gson.fromJson(type, InstantRewardResources.class);
                instantRewards.add(instantRewardResources);
            }
            else if(current.has("instantRewardProduction")){
                JsonObject type = current.get("instantRewardProduction").getAsJsonObject();
                InstantRewardProduction instantRewardProduction = gson.fromJson(type, InstantRewardProduction.class);
                instantRewards.add(instantRewardProduction);
            }
            else if(current.has("instantRewardHarvest")){
                JsonObject type = current.get("instantRewardHarvest").getAsJsonObject();
                InstantRewardHarvest instantRewardHarvest = gson.fromJson(type, InstantRewardHarvest.class);
                instantRewards.add(instantRewardHarvest);
            }
            else if(current.has("instantRewardPick")){
                JsonObject type = current.get("instantRewardPick").getAsJsonObject();
                InstantRewardPick instantRewardPick = gson.fromJson(type, InstantRewardPick.class);
                instantRewards.add(instantRewardPick);
            }
            else if(current.has("instantRewardVictoryPoints")){
                JsonObject type = current.get("instantRewardVictoryPoints").getAsJsonObject();
                InstantRewardVictoryPoints instantRewardVictoryPoints = gson.fromJson(type, InstantRewardVictoryPoints.class);
                instantRewards.add(instantRewardVictoryPoints);
            }


        }
        return instantRewards;

    }

    /**
     *
     * this method returns the buffered reader object of model.cards.json
     */
    private void createBufferedReader(String filePath) throws IOException{
        fr = new FileReader(filePath);
         br = new BufferedReader(fr);

    }

    /**
     * this method closes the bufferedReader
     */
    private void closeBufferedReader() throws IOException{
        if(fr!=null){
            fr.close();
        }
        if (br != null){
            br.close();
        }

    }

    /**
     *
     * @param period as string
     * @return current card period
     */
    private Period getPeriod(String period){
        if("FIRST".equals(period)){
            return Period.FIRST;

        }else if("SECOND".equals(period)){
            return Period.SECOND;

        }else if("THIRD".equals(period)){
            return Period.THIRD;
        }else {
            return null;
        }

    }

    /**
     *
     * @param cardColor as string
     * @return current card color
     */
    private CardColor getCardColor(String cardColor){
        if("YELLOW".equals(cardColor)){
            return CardColor.YELLOW;
        }
        else if("BLUE".equals(cardColor)){
            return CardColor.BLUE;
        }
        else if("PURPLE".equals(cardColor)){
            return CardColor.PURPLE;
        }
        else if("GREEN".equals(cardColor)){
            return CardColor.GREEN;
        }
        else return null;
    }

    /**
     *
     * @return an arrayList containing all the costs of the card
     */
    private ArrayList<Cost> getCosts(JsonArray costArray){
        Gson gson = new Gson();
        ArrayList<Cost> costs = new ArrayList<>();
        for (int costIndex = 0; costIndex<costArray.size(); costIndex++){
            JsonObject cardCost = costArray.get(costIndex).getAsJsonObject();
            Cost cost = gson.fromJson(cardCost, Cost.class);
            costs.add(cost);
        }
        return costs;
    }

}
