package view.cli;

import com.diogonunes.jcdp.color.api.Ansi;
import controller.Actions;
import controller.GameConfiguration;
import model.players.FamilyMember;
import model.players.Player;
import controller.exceptions.ServerException;
import controller.exceptions.TimeOutException;
import model.cards.developmentcards.*;
import model.cards.excommunicationcards.ExcommunicationCard;
import model.cards.leadercards.LeaderCard;
import model.cards.leadercards.LeaderRequirements;
import model.dices.DiceColor;
import model.gameboard.*;
import network.protocol.ClientAction;
import network.protocol.NetworkProtocol;
import network.client.rmi.RMIClient;
import network.client.socket.SocketClient;
import view.Client;
import view.ControllerUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class is the cli interface provided to the user.
 */
public class Cli extends ControllerUI implements Serializable{
    private transient BufferedReader in;
    private static final String SOCKET = "1";
    private static final String RMI = "2";
    private static final String SIGN_IN = "1";
    private static final String SIGN_UP = "2";
    private Field field;
    private ArrayList<Player> playersStatus;
    private Client client;
    private HashMap<Integer, ActionDefaultMap> actionsMap;
    private HashMap<String, ActionDefaultMap> masterActions;
    private FamilyMember familyMemberChosen;
    private int servantsToAdd;
    private String[] actionList;
    private String username;
    private static final int CARD_LENGTH = 60;
    private static final int NUMBER_OF_TOWERS = 4;
    private String[] towersPositions;
    private transient Thread notYourTurnThread;
    private boolean isYourTurn;
    private boolean familyMemberPlaced;
    private static final String DEFAULT_MESSAGE_IF_NOT_FAMILY_MEMBER = "\n\nYOU HAVE ALREADY PLACED A FAMILY MEMBER, PRESS 0 TO GO BACK TO MENU\n\n";
    private GameConfiguration config = GameConfiguration.getInstance();
    private static final String NEXT_POSITION_AVAILABLE = "\nNEXT POSITION AVAILABLE\n";
    private static final String NEUTRAL_FAMILY_MEMBER = " (neutral)";
    private static final String MESSAGE_FOR_SERVER_EXCEPTION = "\nCONNECTION PROBLEM\n";

    /**
     * Instantiate the CLi
     */
    public Cli() {
        in = new BufferedReader (new InputStreamReader(System.in));
        actionsMap = new HashMap<>();
        masterActions = new HashMap<>();
        initializeHashMap();
        initializedMasterActionsMap();
        initializeTowersPositions();
    }

    /**
     * Initialize the strings of the towers' positions
     */
    private void initializeTowersPositions() {
        this.towersPositions = new String[]{Actions.GREEN_1,  Actions.BLUE_1, Actions.YELLOW_1, Actions.PURPLE_1,
                Actions.GREEN_2, Actions.BLUE_2, Actions.YELLOW_2, Actions.PURPLE_2,
                Actions.GREEN_3, Actions.BLUE_3, Actions.YELLOW_3, Actions.PURPLE_3,
                Actions.GREEN_4, Actions.BLUE_4, Actions.YELLOW_4, Actions.PURPLE_4};
    }

    /**
     * Initialize the hashMap to handle the menu
     */
    private void initializeHashMap() {
        actionsMap.put(1, this::onViewPlayersStatus);
        actionsMap.put(2, this::onViewTowerArea);
        actionsMap.put(3, this::onViewMarketArea);
        actionsMap.put(4, this::onProductionHarvestArea);
        actionsMap.put(5, this::onViewCouncilArea);
    }

    /**
     * Initialize the hashMap to handle the actions
     */
    private void initializedMasterActionsMap(){
        masterActions.put(DefaultCliMenu.secondMenuChoices[0], this :: onSellLeaderCard);
        masterActions.put(DefaultCliMenu.secondMenuChoices[1], this :: onActivateLeaderCard);
        masterActions.put(DefaultCliMenu.secondMenuChoices[2], this :: onPassTurn);
        masterActions.put(DefaultCliMenu.secondMenuChoices[3], this :: onGetBackToMenu);
    }

    /**
     * If the player has chosen to go back to the menu
     @throws TimeOutException if the timeout has expired
     */
    private void onGetBackToMenu() throws TimeOutException{
        getToStartingMenu();
    }

    /**
     * If action chosen is to pass the turn
     * @throws TimeOutException if the timeout has expired
     */
    private void onPassTurn() throws TimeOutException{
        try {
            int choice = confirmChoices();
            if (choice == 1) {
                client.sendAction(new ClientAction(Actions.PASS_TURN));
                cancelTimeout();
            } else
                getToStartingMenu();
        }catch(IOException e){
            Display.println(e);
        }
    }

    /**
     * Ask the player if he's sure about the choice
     * @return 1 if he confirms, 0 if not
     * @throws TimeOutException if the timeout has expired
     */
    private int confirmChoices() throws TimeOutException {
        Display.println("\nDO YOU CONFIRM (0 for no and go back to menu, 1 for yes)?\n", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
        try {
            return getChoice(0, 1);
        } catch (ServerException e) {
            Display.println(e);
            return 0;
        }
    }

    /**
     * If the action chosen is to place a familyMember
     * @param choiceMade by the player
     * @throws TimeOutException if the timeout has expired
     */
    private void onPlaceFamilyMember(String choiceMade) throws TimeOutException {
        try {
            for (Player p : playersStatus) {
                if (p.getUsername().equals(this.client.getUsername())) {
                    this.familyMemberChosen = getFamilyMember(p.getFamilyMembers());
                    this.servantsToAdd = chooseServants(p.getPlayerResources().getServants());
                    break;
                }
            }
            int chosen = confirmChoices();
            if (chosen == 1) {
                if (canDoAction()) {
                    ClientAction clientAction = new ClientAction(Actions.PLACE_FAMILY_MEMBERS);
                    clientAction.setPosition(choiceMade);
                    clientAction.setFamilyMember(familyMemberChosen);
                    clientAction.setServants(servantsToAdd);
                    client.sendAction(clientAction);
                }
            } else
                getToStartingMenu();
        }catch(IOException e){
            Display.println(e);
        }
    }

    /**
     * If the action chosen is to activate a leaderCard
     * @throws TimeOutException if the timeout has expired
     */
    private void onActivateLeaderCard() throws TimeOutException {
        try {
            Display.print("\nCHOOSE A LEADER CARD TO ACTIVATE\n\n", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
            for (Player p : playersStatus) {
                if (p.getUsername().equals(client.getUsername())) {
                    displayLeaderCards(p.getNonActivatedLeaderCards());
                    int choice = getChoice(1, p.getLeaderCards().size());
                    int chosen = confirmChoices();
                    if (chosen == 1) {
                        if (canDoAction()) {
                            ClientAction clientAction = new ClientAction(Actions.ACTIVATE_LEADER_CARDS);
                            clientAction.setLeaderCard(p.getLeaderCards().get(choice - 1));
                            client.sendAction(clientAction);
                        }
                    } else {
                        getToStartingMenu();
                    }
                    break;
                }
            }
        }catch(IOException e){
            Display.println(e);
        }

    }

    /**
     * If the action chosen is to sell a leaderCard
     * @throws TimeOutException if the timeout has expired
     */
    private void onSellLeaderCard() throws TimeOutException {
        int j = 0;
        for (Player p : playersStatus) {
            if (p.getUsername().equals(this.client.getUsername())) {
                displayLeaderCards(p.getLeaderCards());
                Display.print("\n\n");
                break;
            }
            j++;
        }
        try {
            if (!playersStatus.get(j).getLeaderCards().isEmpty()) {
                int choice = getChoice(1, playersStatus.get(j).getLeaderCards().size());
                LeaderCard leaderCardChosen = playersStatus.get(j).getLeaderCards().get(choice - 1);
                int chosen = confirmChoices();
                if (chosen == 1) {
                    if (canDoAction()) {
                        ClientAction clientAction = new ClientAction(Actions.SELL_LEADER_CARDS);
                        clientAction.setLeaderCard(leaderCardChosen);
                        client.sendAction(clientAction);
                    }
                    return;
                }
            }
            Display.println("\nYOU DON'T HAVE ANY LEADER CARD TO SELL\n", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
            getToStartingMenu();
        }catch(IOException e){
            Display.println(e);
        }
    }

    /**
     * If the player wants to view the council area
     * @throws TimeOutException if the timeout has expired
     */
    private void onViewCouncilArea() throws TimeOutException{
        try {
            showCouncilArea(field.getCouncil());
            if (!familyMemberPlaced) {
                Display.print("\n\nPRESS 1 TO PLACE A FAMILY MEMBER IN THE COUNCIL AREA, 0 TO GO BACK TO MENU\n\n", Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
                int choice = getChoice(0, 1);
                String councilAction = Actions.START_COUNCIL;
                if (choice == 0)
                    getToStartingMenu();
                else
                    onPlaceFamilyMember(councilAction);
            } else {
                Display.print(DEFAULT_MESSAGE_IF_NOT_FAMILY_MEMBER, Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
                getChoice(0, 0);
                getToStartingMenu();
            }
        }catch(IOException e){
            Display.println(e);
        }
    }

    /**
     * If the player wants to view the production and harvest area
     * @throws TimeOutException if the timeout has expired
     */
    private void onProductionHarvestArea() throws TimeOutException{
        try {
            showHarvestArea(field.getHarvest());
            showProductionArea(field.getProduction());
            if (!familyMemberPlaced) {
                Display.print("\n\nPRESS 1 TO START An HARVEST, PRESS 2 TO START A PRODUCTION," +
                        " 0 TO GO BACK TO MENU\n\n", Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
                int choice = getChoice(0, 2);
                String harvestAction = Actions.START_HARVEST;
                String productionAction = Actions.START_PRODUCTION;
                if (choice == 0)
                    getToStartingMenu();
                else if (choice == 1)
                    onPlaceFamilyMember(harvestAction);
                else
                    onPlaceFamilyMember(productionAction);
            } else {
                Display.print(DEFAULT_MESSAGE_IF_NOT_FAMILY_MEMBER, Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
                getChoice(0, 0);
                getToStartingMenu();
            }
        }catch(IOException e){
            Display.println(e);
        }
    }

    /**
     * Show the production area
     * @param productionArea to display
     */
    private void showProductionArea(ProductionArea productionArea) {
        int id = 1;
        boolean toBreak = false;
        Display.print("\n\n\nPRODUCTION AREA\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        for (ProductionPosition productionPosition : productionArea.getProduction()) {
            if (productionPosition.getFamilyMember() == null) {
                Display.print(NEXT_POSITION_AVAILABLE, Ansi.Attribute.UNDERLINE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
                toBreak = true;
            }
            Display.print(id + ") Production Position\n", Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            displayMinimumDiceValue(productionPosition.getDiceValue(), Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            Display.print("Malus on familyMember value: ", Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            Display.print(productionPosition.getProductionMalus() + "\n");
            String toDisplay = productionPosition.familyMemberToString();
            if (productionPosition.getFamilyMember() != null && productionPosition.getFamilyMember().getDiceColor().equals(DiceColor.NEUTRAL))
                toDisplay += NEUTRAL_FAMILY_MEMBER;
            displayFamilyMember(toDisplay, Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            if (toBreak)
                break;
            id++;
        }
    }

    /**
     * Show the harvest area
     * @param harvestArea to display
     */
    private void showHarvestArea(HarvestArea harvestArea) {
        Display.print("\nHARVEST AREA\n", Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.NONE);
        int id = 1;
        boolean toBreak = false;
        for (HarvestPosition harvestPosition : harvestArea.getHarvest()) {
            if (harvestPosition.getFamilyMember() == null) {
                Display.print(NEXT_POSITION_AVAILABLE, Ansi.Attribute.UNDERLINE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
                toBreak = true;
            }
            Display.print(id + ") Harvest Position\n", Ansi.Attribute.NONE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            displayMinimumDiceValue(harvestPosition.getDiceValue(), Ansi.Attribute.NONE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            Display.print("Malus on familyMember value: ", Ansi.Attribute.NONE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            Display.print(harvestPosition.getHarvestMalus() + "\n");
            String toDisplay = harvestPosition.familyMemberToString();
            if (harvestPosition.getFamilyMember() != null && harvestPosition.getFamilyMember().getDiceColor().equals(DiceColor.NEUTRAL))
                toDisplay += NEUTRAL_FAMILY_MEMBER;
            displayFamilyMember(toDisplay, Ansi.Attribute.NONE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            if (toBreak)
                break;
            id++;
        }
    }

    /**
     * If the player wants to view the market area
     * @throws TimeOutException if the timeout has expired
     */
    private void onViewMarketArea() throws TimeOutException{
        try {
            showMarket(field.getMarket());
            if (!familyMemberPlaced) {
                Display.print("\n\nPRESS 1 TO PLACE A FAMILY MEMBER IN A MARKET POSITION, 0 TO GO BACK TO MENU\n\n", Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
                int choice = getChoice(0, 1);
                if (choice == 0)
                    getToStartingMenu();
                else {
                    for (int i = 0; i < field.getMarket().getMarket().length; i++) {
                        String toDisplay = (i + 1) + ") Market " + (i + 1);
                        Display.print(toDisplay, Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
                        appendSpaces(10, toDisplay.length());
                    }
                    Display.println("\n");
                    choice = getChoice(1, field.getMarket().getMarket().length);
                    onPlaceFamilyMember(Actions.MARKET_POSITIONS[choice - 1]);
                }
            } else {
                Display.print(DEFAULT_MESSAGE_IF_NOT_FAMILY_MEMBER, Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
                getChoice(0, 0);
                getToStartingMenu();
            }
        }catch(IOException e){
            Display.println(e);
        }
    }

    /**
     * If the player wants to view the towers area
     * @throws TimeOutException if the timeout has expired
     */
    private void onViewTowerArea() throws TimeOutException{
        try {
            showTowers(field.getGreenTower(), field.getBlueTower(), field.getYellowTower(), field.getPurpleTower());
            if (!familyMemberPlaced) {
                Display.print("\n\nPRESS 1 TO PLACE A FAMILY MEMBER IN A TOWER POSITION, 0 TO GO BACK TO MENU\n\n", Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
                int choice = getChoice(0, 1);
                if (choice == 0)
                    getToStartingMenu();
                else {
                    for (int i = 0; i < towersPositions.length; i++) {
                        String toDisplay = (i + 1) + ") " + towersPositions[i];
                        Display.print(toDisplay, Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
                        appendSpaces(20, toDisplay.length());
                        if ((i + 1) % NUMBER_OF_TOWERS == 0)
                            Display.print("\n\n");
                    }
                    Display.println("\n");
                    choice = getChoice(1, towersPositions.length);
                    onPlaceFamilyMember(towersPositions[choice - 1]);
                }
            } else {
                Display.print(DEFAULT_MESSAGE_IF_NOT_FAMILY_MEMBER, Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
                getChoice(0, 0);
                getToStartingMenu();
            }
        }catch(IOException e){
            Display.println(e);
        }
    }

    /**
     * Display the cards
     * @param cards to display
     */
    private void displayCards(List<? extends Card> cards){
        for (Card card : cards)
            displayCardColor(card);
        Display.print("\n");
        for (Card card : cards)
            displayName(card);
        Display.print("\n");
        for (Card card : cards)
            displayCost(card);
        Display.print("\n");
        for (Card card : cards)
            displayInstantReward(card);
        Display.print("\n");
        for(Card card : cards)
            displayPermanentReward(card);

        Display.print("\n");

    }

    /**
     * Show the cards of a specific player
     * @param p the player
     */
    private void showAllPlayerCards(Player p){
        displayCards(p.getGreenCards());
        displayCards(p.getBlueCards());
        displayCards(p.getYellowCards());
        displayCards(p.getPurpleCards());
    }

    /**
     * If the player wants to see the status of all the players
     * @throws TimeOutException if the timeout has expired
     */
    private void onViewPlayersStatus() throws TimeOutException{
        for (Player p : playersStatus) {
            Display.println("\nResources of player: " + p.getUsername() + " (" + p.getPlayerColor() + ")", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.println("Coins: " + p.getPlayerResources().getCoins());
            Display.println("Wood: " + p.getPlayerResources().getWood());
            Display.println("Stone: " + p.getPlayerResources().getStone());
            Display.println("Servants: " + p.getPlayerResources().getServants());
        }
        Display.println("\n");
        for (Player p : playersStatus) {
            Display.println("\nPoints of player: " + p.getUsername() + " (" + p.getPlayerColor() + ")", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            Display.println("Victory points: " + p.getPlayerResources().getVictoryPoints());
            Display.println("Military points: " + p.getPlayerResources().getMilitaryPoints());
            Display.println("Faith points: " + p.getPlayerResources().getFaithPoints());
        }
        Display.println("\n");
        for (Player p : playersStatus) {
            Display.println("\nCards of player: " + p.getUsername() + " (" + p.getPlayerColor() + ")", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
            showAllPlayerCards(p);
        }
        Display.println("\n");
        for(Player p : playersStatus){
            Display.println("\nBonus tile of player: " + p.getUsername() + " (" + p.getPlayerColor() + ")", Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
            showPlayerBonusTile(p);
        }
        try {
            getToMenu();
        } catch (IOException e) {
            Display.println(e);
        }
    }

    /**
     * Show the bonus tile of a player
     * @param p the player
     */
    private void showPlayerBonusTile(Player p) {
        Display.print("\n");
        Display.println("BONUS TILE: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
        Display.print("Production reward: ");
        Display.println(p.getBonusTile().getProductionReward().toString());
        Display.print("Harvest reward: ");
        Display.println(p.getBonusTile().getHarvestReward().toString());
        Display.println("\n");
    }

    /**
     * Initialize the cli and start the login phase
     */
    @Override
    public void init(){
        try {
            login();
            client.start();
        } catch (IOException e) {
            Display.println(e);
        }
    }

    /**
     * Handle the login
     * @throws IOException if there are problems with the reading
     */
    private void login() throws IOException {
        boolean connected = false;
        String choice;
        do{
            Display.println("Choose connection type: \n1)Socket\n2)RMI");
            choice = in.readLine();
        }while (!(choice.equals(SOCKET) || choice.equals(RMI)));
        connect(choice);
        try{
            while(!connected){
                connected = askCredential();
            }
            client.setUsername(username);
        }catch (ServerException e) {
            Display.println(e);
            Display.println("connection refused due to server problems");
        }
    }

    /**
     * Ask for credential to the the user
     * @return true if the credential are corrects, false otherwise
     * @throws IOException if there are problems with the reading
     */
    private boolean askCredential() throws IOException {
        String choice;
        boolean connected;
        do{
            Display.println("1)Sign in \n2)Sign up");
            choice = in.readLine();
        }while (!(choice.equals(SIGN_IN)|| choice.equals(SIGN_UP)));
        String password;

        Display.println("Insert username");
        username = in.readLine();
        Display.println("Insert password");
        password = in.readLine();
        if(choice.equals(SIGN_IN)){
            connected = client.signIn(username, password);
            if(!connected){
                Display.println("Username or password not valid");
            }

        }else {
            connected = client.signUp(username, password);
            if(!connected){
                Display.println("Username already exists");
            }
        }
        return connected;
    }

    /**
     * Try to connect to the server
     * @param choice the player's choice (socket or RMI)
     */
    private void connect(String choice){
        boolean connect = false;
        while (!connect){
            Display.println("Insert ip address");
            try {
                String ip = in.readLine();
                if(choice.equals(SOCKET)){
                    client = new SocketClient(ip, this);
                }else {
                    client = new RMIClient(ip, this);

                }
                connect = true;

            }catch (ServerException e){
                Display.println(e);
                Display.println("Port busy");
            } catch (IOException e) {
                Display.println(e);
            }
        }
    }

    /**
     * Choose how many servants to add
     * @param servants of the player
     * @return the number of servants chosen
     * @throws TimeOutException if the timeout has expired
     */
    private int chooseServants(int servants) throws TimeOutException{
        Display.println("You have " + servants + " servants left", Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
        Display.println("Choose how many servants you want to add: ");
        try {
            return getChoice(0, servants);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
        return 0;
    }

    /**
     * Convert te council privileges to resources
     * @param numberOfPrivileges the number of council privileges to convert
     * @param resources the different type of resources a council privilege can be convert into
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void convertCouncilPrivileges(int numberOfPrivileges, List<Resources> resources) throws TimeOutException{
        ArrayList<Resources> resourcesToAdd = new ArrayList<>();
        Display.println("You have " + numberOfPrivileges + " number of council privilege to convert");
        try {
            for (int i = numberOfPrivileges; i > 0; i--) {
                for (int id = 0; id < resources.size(); id++) {
                    Display.println("\n" + (id + 1) + ") Resources: ", Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.NONE);
                    Display.println("Coins: " + resources.get(id).getCoins());
                    Display.println("Wood: " + resources.get(id).getWood());
                    Display.println("Stone: " + resources.get(id).getStone());
                    Display.println("Servants: " + resources.get(id).getServants());
                    Display.println("Faith points: " + resources.get(id).getFaithPoints());
                    Display.println("Military points: " + resources.get(id).getMilitaryPoints());
                }
                int choice = getChoice(1, resources.size());
                resourcesToAdd.add(resources.get(choice - 1));
                resources.remove(choice - 1);
            }
            if (canDoAction()) {
                ClientAction clientAction = new ClientAction(NetworkProtocol.CONVERT_COUNCIL_PRIVILEGE);
                clientAction.setObjects(resourcesToAdd);
                client.sendAction(clientAction);
            }
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * Update the players' status
     * @param players the new status of all the players
     */
    @Override
    public void setPlayerStatus(List<Player> players) {
        this.playersStatus = (ArrayList<Player>) players;
    }

    /**
     * Set the client
     * @param client to set
     */
    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Update the council area
     * @param councilArea new status of the councilArea
     */
    @Override
    public void updateCouncilArea(CouncilArea councilArea) {
        field.setCouncil(councilArea);
    }

    /**
     * Update the MarketArea
     * @param marketArea new status of the MarketArea
     */
    @Override
    public void updateMarketArea(MarketArea marketArea) {
        field.setMarket(marketArea);
    }

    /**
     * Update the harvestArea
     * @param harvestArea new status of the HarvestArea
     */
    @Override
    public void updateHarvestArea(HarvestArea harvestArea) {
        field.setHarvest(harvestArea);
    }

    /**
     * Update the ProductionArea
     * @param productionArea new status of the ProductionArea
     */
    @Override
    public void updateProductionArea(ProductionArea productionArea) {
        field.setProduction(productionArea);
    }

    /**
     * Update the TowersArea
     * @param towers the new towers
     */
    @Override
    public void updateTowers(List<Tower> towers) {
        field.setTowers(towers);
    }

    /**
     * Ask the player if he wants to support the vatican or not
     * @param card excommunication card of the current period
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void onShowSupport(ExcommunicationCard card) throws TimeOutException{
        for(Player p : playersStatus) {
            if (p.getUsername().equals(this.client.getUsername())) {
                sendSupportAction(card, p);
                break;
            }
        }
    }

    /**
     * Handle the support of the vatican phase
     * @param card the excommunication card
     * @param p the player
     * @throws TimeOutException if the timeout has expired
     */
    private void sendSupportAction(ExcommunicationCard card, Player p) throws TimeOutException {
        int victoryPoints;
        if(p.getPlayerResources().getFaithPoints() > config.getFaithPathPoints().length)
            victoryPoints = config.getFaithPathPoints()[p.getPlayerResources().getFaithPoints()];
        else
            victoryPoints = config.getFaithPathPoints()[config.getFaithPathPoints().length - 1];
        Display.print("\n\nIT IS TIME TO CHOOSE IF YOU WANT TO SUPPORT THE VATICAN\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        Display.print("\nIF YOU SUPPORT IT, YOU WILL RECEIVE" +
                victoryPoints + " VICTORY POINTS\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        Display.print("IF YOU DON'T, IT WILL BE GIVEN TO YOU THIS EXCOMMUNICATION CARD\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        Display.print(card.getDescription());
        Display.print("\n\nINSERT 1 IF YOU WANT TO SUPPORT THE VATICAN, 0 OTHERWISE\n", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
        try {
            int choice = getChoice(0, 1);
            ClientAction clientAction = new ClientAction(NetworkProtocol.SHOW_SUPPORT);
            if (choice == 1) {
                if (canDoAction())
                    clientAction.setObject(true);
            } else if (canDoAction())
                clientAction.setObject(false);
            client.sendAction(clientAction);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * Let the player choose which cost he wants to pay
     * @param costs to choose from
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseCost(List<Cost> costs) throws TimeOutException{
        for(Cost cost : costs)
            Display.showCost(cost);
        try {
            int choice = getChoice(1, costs.size());
            ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_COST);
            clientAction.setObject(costs.get(choice - 1));
            client.sendAction(clientAction);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * Choose the bonus tile at the beginning of the game
     * @param bonusTiles to choose from
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseBonusTile(List<BonusTile> bonusTiles) throws TimeOutException{
        for(int i = 0; i < bonusTiles.size(); i ++) {
            Display.print("\n\n");
            Display.println((i + 1) + ") BONUS TILE: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.print("Production reward: ");
            Display.println(bonusTiles.get(i).getProductionReward().toString());
            Display.print("Harvest reward: ");
            Display.println(bonusTiles.get(i).getHarvestReward().toString());
        }
        int choice;
        try {
            choice = getChoice(1, bonusTiles.size());
            cancelTimeout();
            ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_BONUS_TILE);
            clientAction.setObject(bonusTiles.get(choice - 1));
            client.sendAction(clientAction);
            Display.println("\nWAIT WHILE OTHER PLAYERS ARE CHOOSING THE BONUS TILE\n", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * Choose the leader cards at the beginning of the game
     * @param leaderCards to choose from
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void draftLeaderCards(List<LeaderCard> leaderCards) throws TimeOutException{
        displayLeaderCards(leaderCards);
        int choice;
        try {
            choice = getChoice(1, leaderCards.size());
            ClientAction clientAction = new ClientAction(NetworkProtocol.DRAFT_LEADER_CARD);
            clientAction.setLeaderCard(leaderCards.get(choice - 1));
            client.sendAction(clientAction);
            Display.println("\nWAIT WHILE OTHER PLAYERS ARE CHOOSING THE LEADER CARDS\n", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * Show the leader cards
     * @param leaderCards to display
     */
    private void displayLeaderCards(List<LeaderCard> leaderCards){
        int index = 1;
        for(LeaderCard leaderCard : leaderCards) {
            Display.print("\n\n\n" + index +") Name: \n", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.print(leaderCard.getName() + "\n");
            Display.print("Leader requirements: \n", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            for(LeaderRequirements leaderRequirements : leaderCard.getLeaderRequirements()){
                Display.print(leaderRequirements.toString());
            }
            Display.print("Description: \n", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.print(leaderCard.getDescription() + "\n", Ansi.Attribute.UNDERLINE, Ansi.FColor.NONE, Ansi.BColor.NONE);
            index++;
        }
    }

    /**
     * Choose which cards to activate during the production phase
     * @param yellowCards of the player
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseProductionCards(List<YellowCard> yellowCards) throws TimeOutException{
        ArrayList<YellowCard> yellowCardsArrayList = new ArrayList<>(yellowCards);
        ArrayList<YellowCard> yellowCardsToReturn = new ArrayList<>();
        if(yellowCardsArrayList.isEmpty()){
            ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_PRODUCTION_CARDS);
            clientAction.setObjects(yellowCardsToReturn);
            try {
                client.sendAction(clientAction);
            } catch (ServerException e) {
                Display.println(e);
                Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
            }
            return;
        }
        Display.print("\nCHOOSE WHICH CARDS YOU WANT TO TO ACTIVATE OR PRESS 0 TO END\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        int index = 1;
        boolean choiceMade = false;
        while(!choiceMade){
            Display.print("\n" + index + ") ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            displayCards(yellowCardsArrayList);
            try {
                int choice = getChoice(0, yellowCardsArrayList.size());
                if (choice == 0) {
                    ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_PRODUCTION_CARDS);
                    clientAction.setObjects(yellowCardsToReturn);
                    client.sendAction(clientAction);
                    choiceMade = true;
                } else {
                    yellowCardsToReturn.add(yellowCardsArrayList.remove(choice - 1));
                }
                index++;
            } catch (ServerException e) {
                Display.println(e);
                Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
            }
        }
    }

    /**
     * When the player is reconnecting
     * @param field the updated field
     */
    @Override
    public void reconnect(Field field) {
        setField(field);
    }

    /**
     * Notify to the user the game has started
     * @param gameStarted a code sent by the server
     */
    @Override
    public void startGame(Object gameStarted) {
        Display.println("GAME STARTED!\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
    }

    /**
     * Notify to the user that it is his turn and interrupt the notYourTurnThread
     * @param code a code sent by the server
     */
    @Override
    public void yourTurn(String code) {
        isYourTurn = true;
        if(notYourTurnThread != null) {
            notYourTurnThread.interrupt();
        }
    }

    /**
     * After the player has chosen to activate the "Lorenzo de Medici" leader card, ask him
     * what other leader's ability he wants to copy
     * @param cards the cards the user can copy
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseLeaderCardToCopy(List<LeaderCard> cards) throws TimeOutException{
        Display.print("\nCHOOSE ANOTHER LEADER CARD YOU WANT TO COPY\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        displayLeaderCards(cards);
        try {
            int choice = getChoice(1, cards.size());
            ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_LEADER_CARD_TO_COPY);
            clientAction.setObject(cards.get(choice - 1));
            client.sendAction(clientAction);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * After the player has chosen to activate the "Federico da Montefeltro" leader card,
     * ask him which familyMember he wants to have a permanent value of 6
     * @param familyMembers the familyMembers he can choose
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseFamilyMemberToAddValue(List<FamilyMember> familyMembers) throws TimeOutException{
        Display.print("\nCHOOSE A FAMILY MEMBER TO HAVE A PERMANENT VALUE OF 6\n\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        int index = 1;
        for (FamilyMember familyMember : familyMembers){
            Display.print(index + ") " + familyMember.getDiceColor(), Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            index++;
        }
        try {
            int choice = getChoice(1, familyMembers.size());
            ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_FAMILY_TO_ADD_VALUE);
            clientAction.setObject(familyMembers.get(choice - 1));
            client.sendAction(clientAction);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * If an user is trying to connect with an username associated with an account
     * already online
     * @param code a code sent by the server
     */
    @Override
    public void alreadyOnlineWarning(String code) {
        Display.println("\nUSER ALREADY ONLINE!");
    }

    /**
     * Ask the player to choose a position to go after he received an instantRewardPick
     * @param positions the positions the user can choose
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseInstantRewardPosition(String[] positions) throws TimeOutException{
        int index = 1;
        Display.print("\n\nCHOOSE A POSITION, YOU WILL BE GIVEN THE CARD ON IT\n", Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
        for (String position : positions) {
            String toDisplay = (index + 1) + ") " + position;
            Display.print(toDisplay, Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            appendSpaces(20, toDisplay.length());
            if (index % 4 == 0)
                Display.print("\n\n");
            index++;
        }
        Display.print("\n");
        try {
            int choice = getChoice(1, positions.length);
            ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_INSTANT_REWARD_POSITION);
            clientAction.setPosition(positions[choice - 1]);
            client.sendAction(clientAction);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * Ask the player which rewards he wants to produce after he activated a yellowCard
     * with more than one reward during the production phase
     * @param yellowCard the card activated by the user
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void chooseRewardToProduce(YellowCard yellowCard) throws TimeOutException{
        Display.print("\nCHOOSE AN INSTANT REWARD TO PRODUCE\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        int index = 1;
        for (YellowReward yellowReward : yellowCard.getYellowReward()) {
            Display.print("\n" + index + ") " + yellowReward.toString() + "\n");
            index++;
        }
        try {
            int choice = getChoice(1, yellowCard.getYellowReward().size());
            ClientAction clientAction = new ClientAction(NetworkProtocol.CHOOSE_REWARD_TO_PRODUCE);
            clientAction.setObject(yellowCard.getYellowReward().get(choice - 1));
            client.sendAction(clientAction);
        } catch (ServerException e) {
            Display.println(e);
            Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
        }
    }

    /**
     * If the player has won the game
     * @param code general code
     */
    @Override
    public void youWon(String code) {
        Display.print("\n\nYOU HAVE DONE IT! YOU WON THE GAME!!!\n\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
    }

    /**
     * If the player has lost the game
     * @param s general string
     */
    @Override
    public void youLost(String s) {
        Display.print("\n\nOH NO...\n YOU LOST...\nTRY HARDER NEXT TIME!", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
    }

    /**
     * Display how much time is left to perform an action
     * @param timeLeft time left
     */
    @Override
    public void showTimeLeft(int timeLeft) {
        Display.println("\nYOU GOT " + timeLeft + " SECONDS TO PERFORM THIS ACTION");
    }

    /**
     * Show the statistics at the end of the game
     * @param ranking the statistics
     */
    @Override
    public void showRanking(String ranking) {
        Display.println(ranking);
    }

    /**
     * Display the market
     * @param marketArea to display
     */
    private void showMarket(MarketArea marketArea) {
        int id = 1;
        for(MarketPosition marketPosition : marketArea.getMarket()){
            Display.print("\n\nMARKET" + id + "\n", Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            Display.print("Minimum value required: ", Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            Display.print(marketPosition.getDiceValue() + "\n");
            displayReward(marketPosition.getReward().toString() + "\n", Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            String toDisplay =marketPosition.familyMemberToString();
            if(marketPosition.getFamilyMember()!= null && marketPosition.getFamilyMember().getDiceColor().equals(DiceColor.NEUTRAL))
                toDisplay += NEUTRAL_FAMILY_MEMBER;
            displayFamilyMember(toDisplay, Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.NONE);
            id++;
        }
    }

    /**
     * Display the reward got by an action
     * @param message from the server
     * @param attribute the font of the message
     * @param fColor the color of the message
     * @param bColor the background color of the message
     */
    private void displayReward(String message, Ansi.Attribute attribute, Ansi.FColor fColor, Ansi.BColor bColor){
        Display.print("Reward: ", attribute, fColor, bColor);
        Display.print(message);
    }

    /**
     * Display a familyMember
     * @param familyMemberToString the familyMember toString
     * @param attribute the font of the message
     * @param fColor the color of the message
     * @param bColor the background color of the message
     */
    private void displayFamilyMember(String familyMemberToString, Ansi.Attribute attribute, Ansi.FColor fColor, Ansi.BColor bColor) {
        Display.print("FamilyMember: ", attribute, fColor, bColor);
        Display.print(familyMemberToString + "\n");
    }

    /**
     * Display the minimum value to get into a position
     * @param value needed to get into the position
     * @param attribute the font of the message
     * @param fColor the color of the message
     * @param bColor the background color of the message
     */
    private void displayMinimumDiceValue(int value, Ansi.Attribute attribute, Ansi.FColor fColor, Ansi.BColor bColor) {
        Display.print("Minimum dice value required: ", attribute, fColor, bColor);
        Display.print(value + "\n");
    }

    /**
     * Show the council area
     * @param councilArea the council area to show
     */
    private void showCouncilArea(CouncilArea councilArea) {
        Display.println("\nCOUNCIL AREA\n", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        int id = 1;
        boolean toBreak = false;
        for (CouncilPosition councilPosition : councilArea.getCouncilPositions()) {
            if (councilPosition.getFamilyMember() == null) {
                Display.print(NEXT_POSITION_AVAILABLE, Ansi.Attribute.UNDERLINE, Ansi.FColor.RED, Ansi.BColor.NONE);
                toBreak = true;
            }
            Display.print(id + ") Council Position\n", Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            displayMinimumDiceValue(councilPosition.getDiceValue(), Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            String toDisplay = councilPosition.familyMemberToString();
            if(councilPosition.getFamilyMember()!= null && councilPosition.getFamilyMember().getDiceColor().equals(DiceColor.NEUTRAL))
                toDisplay += NEUTRAL_FAMILY_MEMBER;
            displayFamilyMember(toDisplay, Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            displayReward(councilArea.getBonusCouncilPosition().toString() + "\n\n", Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            id++;
            if (toBreak)
                break;
        }
        for(Period period : Period.values()){
            Display.print("\n\nExcommunication card of period " + period +
                     "(minimum " + config.getFaithPointsNeededToAvoidExcommunication()[period.toInt() - 1] + " faith points to have the choice to support the vatican)\n", Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
            Display.print(field.getExcommunicationCard(period.toInt()).getDescription() + "\n", Ansi.Attribute.UNDERLINE, Ansi.FColor.NONE, Ansi.BColor.NONE);
        }

    }

    /**
     * Print the edges of a card
     */
    private void printCardEdge(){
        for (int numCards = 0; numCards < 4; numCards++){
            for (int k = 0; k < CARD_LENGTH; k++) {
                if (k % 2 == 0)
                    Display.print(" ");
                else
                    Display.print("-");
            }
            Display.print("\t\t");
        }
        Display.print("\n");
    }

    /**
     * Show the tower area
     * @param green tower
     * @param blue tower
     * @param yellow tower
     * @param purple tower
     */
    private void showTowers(Tower green, Tower blue, Tower yellow, Tower purple) {
        ArrayList<Tower> towers = new ArrayList<>();
        towers.add(green);
        towers.add(blue);
        towers.add(yellow);
        towers.add(purple);
        for (int i = 0; i < towers.size(); i++) {
            Display.print("\n\n");
            printCardEdge();
            displayTowerPosition(towers, i + 1);
            Display.print("\n");
            displayDiceValue(towers, i);
            Display.print("\n");
            displayFamilyMemberInTowersPosition(towers, i);
            Display.print("\n\n");
            displayCardsInTowers(towers, i);
        }
    }

    /**
     * Display the attributes of the cards in all the towers at the positions at index i
     * @param towers the towers
     * @param i the index i
     */
    private void displayCardsInTowers(ArrayList<Tower> towers, int i) {
        for (Tower t : towers) {
            displayCardColor(t.getTowerPositions().get(i).getCard());
        }
        Display.print("\n");
        for (Tower t : towers) {
            displayPeriod(t.getTowerPositions().get(i).getCard());
        }
        Display.print("\n");
        for (Tower t : towers) {
            displayName(t.getTowerPositions().get(i).getCard());
        }
        Display.print("\n");
        for (Tower t : towers) {
            displayCost(t.getTowerPositions().get(i).getCard());
        }
        Display.print("\n");
        for (Tower t : towers) {
            int length = 0;
            if (t.getTowerPositions().get(i).getCard() != null) {
                Display.print("Instant reward: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
                length = 16;
            }
            appendSpaces(CARD_LENGTH, length);
        }
        Display.print("\n");
        for (Tower t : towers) {
            displayInstantReward(t.getTowerPositions().get(i).getCard());
        }
        Display.print("\n");
        for (Tower t : towers) {
            int length = 0;
            if (t.getTowerPositions().get(i).getCard() != null) {
                Display.print("Permanent reward: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
                length = 18;
            }
            appendSpaces(CARD_LENGTH, length);
        }
        Display.print("\n");
        for (Tower t : towers)
            displayPermanentReward(t.getTowerPositions().get(i).getCard());
        Display.print("\n");
        printCardEdge();
    }

    /**
     * Display, for each towerPosition, the familyMember which is occupying the position
     * @param towers all the towers
     * @param i the index of the positions to display
     */
    private void displayFamilyMemberInTowersPosition(ArrayList<Tower> towers, int i) {
        for(Tower t : towers){
            int length = 0;
            String toDisplay = "\tFamilyMember: ";
            length += toDisplay.length();
            FamilyMember familyMember = t.getTowerPositions().get(i).getFamilyMember();
            Display.print(toDisplay, Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
            if(familyMember != null) {
                toDisplay = "of player " + familyMember.getPlayerColor().toString();
                if(familyMember.getDiceColor().equals(DiceColor.NEUTRAL))
                    toDisplay += NEUTRAL_FAMILY_MEMBER;
                Display.print(toDisplay);

                length += toDisplay.length();
            }
            else {
                toDisplay = "position empty";
                Display.print(toDisplay);
                length += toDisplay.length();
            }
            appendSpaces(CARD_LENGTH, length);
        }
    }

    /**
     * Display the minimum values needed to get into a position
     * @param towers the towers
     * @param i the index of the tower positions
     */
    private void displayDiceValue(ArrayList<Tower> towers, int i) {
        for(Tower t : towers){
            int length = 0;
            String string = "\t\tMinimum dice value required: " + t.getTowerPositions().get(i).getDiceValue();
            Display.print(string, Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
            length += string.length();
            appendSpaces(CARD_LENGTH, length);
        }
    }

    /**
     * Display the tower positions
     * @param towers the towers
     * @param i index of the positions to show
     */
    private void displayTowerPosition(ArrayList<Tower> towers, int i) {
        int length = 0;
        for(Tower t : towers){
            String string = "\t\t\t\t" + t.getCardColor().toString().toUpperCase() + i;
            Display.print(string, Ansi.Attribute.BOLD, Ansi.FColor.CYAN, Ansi.BColor.NONE);
            length += string.length();
            appendSpaces(CARD_LENGTH, length);
        }
    }

    /**
     * Display the period of a card
     * @param card the card to display
     */
    private void displayPeriod(Card card) {
        int length = 0;
        if(card != null){
            length = 14;
            Display.print("Card period: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.print(card.getPeriod().toInt());
        }
        appendSpaces(CARD_LENGTH, length);
    }

    /**
     * Display the card color
     * @param card the card to display
     */
    private void displayCardColor(Card card) {
        Display.print("");
        int length = 0;
        if(card != null) {
            length = 12;
            Display.print("Card color: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.print(card.getCardColor().toString());
            length += card.getCardColor().toString().length();
        }
        appendSpaces(CARD_LENGTH, length);
    }

    /**
     * Display the name of the card
     * @param card the card to display
     */
    private void displayName(Card card){
        Display.print("");
        int length = 0;
        if(card != null){
            length = 11;
            Display.print("Card name: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            Display.print(card.getName());
            length += card.getName().length();
        }
        appendSpaces(CARD_LENGTH, length);
    }

    /**
     * Display the cost of the card
     * @param card the card to display
     */
    private void displayCost(Card card){
        int length = 0;
        if (card != null && card.getCost() != null){
            length = 11;
            Display.print("Card cost: ", Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
            for(Iterator<Cost> iterator = card.getCost().iterator(); iterator.hasNext();){
                Cost cost = iterator.next();
                Display.print(cost.toString());
                length += cost.numberOfCharacters();
                if(iterator.hasNext()) {
                    Display.print(" -  ");
                    length += 3;
                }
            }
            length -= card.emojiiUsedForCost();
        }
        appendSpaces(CARD_LENGTH, length);
    }

    /**
     * Display the instant reward of the card
     * @param card the card to display
     */
    private void displayInstantReward(Card card) {
        int length = 0;
        if (card != null && card.getInstantReward() != null) {
            int j = 0;
            for (Iterator<InstantReward> instantRewardIterator = card.getInstantReward().iterator();instantRewardIterator.hasNext();) {
                length += instantRewardIterator.next().numberOfCharacters();
                Display.print(card.getInstantReward().get(j).toString());
                if (instantRewardIterator.hasNext()) {
                    Display.print(" - ");
                    length += 3;
                }
                j++;
            }
            length -= card.emojiiUsedForInstantReward();
        }
        appendSpaces(CARD_LENGTH, length);

    }

    /**
     * Display the permanent reward of the card
     * @param card the card to display
     */
    private void displayPermanentReward(Card card){
        int length = 0;
        if (card != null){
            Display.print(card.permanentRewardToString());
            length += card.permanentRewardToString().length();
            length -= card.emojiiUsedForPermanentReward();
        }
        appendSpaces(CARD_LENGTH, length);
    }

    /**
     * Append the spaces between the visualization of two cards
     *@param maxLength spaces between a message and the other
     * @param length how long is the string
     */
    private void appendSpaces(int maxLength, int length){
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < maxLength - length; j++)
            builder.append(" ");
        Display.print(builder + " \t\t");
    }

    /**
     * Get to the starting menu after asking to the user to press 0
     * @throws IOException if there are problems with the reading
     * @throws TimeOutException if the timeout has expired
     */
    private void getToMenu() throws IOException, TimeOutException {
        String choice;
        do {
            Display.println("\n\n" + "PRESS 0 TO GO BACK TO MENU", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
            choice = in.readLine();
        } while (choice.length() == 0 || !Character.isDigit(choice.charAt(0)) || Integer.parseInt(choice) != 0);
        getToStartingMenu();
    }

    /**
     * Display a message
     * @param message received from the server.
     */
    public void showMessage(String message) {
        Display.print("\n" + message + "\n");
    }

    /**
     * Update the field
     * @param field updated
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Handle the initial menu
     * @param actionList sent by the server
     * @throws TimeOutException if the timeout has expired
     */
    @Override
    public void setActions(String[] actionList) throws TimeOutException{
        this.actionList = actionList;
        checkFamilyMember();
        getToStartingMenu();
    }

    /**
     * Check whether or not there is the PLACE_FAMILY_MEMBERS string in the action list.
     * If not, don't allow the user to position a familyMember for the rest of the turn
     */
    private void checkFamilyMember(){

        for(String anAction : actionList){
            if(anAction.equals(Actions.PLACE_FAMILY_MEMBERS)) {
                familyMemberPlaced = false;
                return;
            }
        }
        familyMemberPlaced = true;
    }

    /**
     * Starting menu
     * @throws TimeOutException if the timeout has expired
     */
    private void getToStartingMenu() throws TimeOutException {
        int choice;
        boolean actionChoice = false;
        while(!actionChoice) {
            try {
                Display.printStartingMenu(DefaultCliMenu.firstMenuChoices);
                choice = getChoice(1, DefaultCliMenu.firstMenuChoices.length);
                if (choice != DefaultCliMenu.firstMenuChoices.length)        //Actions of the default menu
                    actionsMap.get(choice).handle();
                else {
                    Display.printActionList(DefaultCliMenu.secondMenuChoices);
                    choice = getChoice(1, DefaultCliMenu.secondMenuChoices.length);
                    masterActions.get(DefaultCliMenu.secondMenuChoices[choice - 1]).handle();
                }
                actionChoice = true;
            } catch (ServerException e) {
                Display.println(e);
                Display.println(MESSAGE_FOR_SERVER_EXCEPTION);
            }
        }
    }


    /**
     * Update the positions available for the player
     * @param positions available
     */
    @Override
    public void setPositions(String[] positions) {
        // Method needed just for the GUI
    }



    /**
     * Get the choice from the user
     * @param minimum choice the user can do
     * @param maximum choice the user can do
     * @return the choice made by the user
     * @throws ServerException if there are problems with the connection
     * @throws TimeOutException if the timeout has expired
     */
    private int getChoice(int minimum, int maximum) throws TimeOutException, ServerException {
        try {
            if(minimum != maximum)
                Display.println("Make your choice (from " + minimum + " to " + maximum + ")" , Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
            else
                Display.println("Press " + maximum + " to continue..." , Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
            String choice = readLineFromUser(minimum, maximum);
            return Integer.parseInt(choice);
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    private String readLineFromUser(int minimum, int maximum) throws IOException, TimeOutException {
        String choice = null;
        boolean isANumber;
        boolean notReady;
        do {
            isANumber = false;
            notReady = true;
            if (!canDoAction()) {
                throw new TimeOutException();
            }
            if (in.ready()) {
                notReady = false;
                choice = in.readLine();
                for (int j = 0; j < choice.length(); j++)
                    if (!Character.isDigit(choice.charAt(j)))
                        isANumber = true;
            }
        }
        while (notReady || choice.length() == 0 || isANumber || Integer.parseInt(choice) < minimum || Integer.parseInt(choice) > maximum);
        return choice;
    }

    /**
     * The player chooses the familyMember to place
     * @param familyMembers available
     * @return the familyMember chosen
     * @throws ServerException if there are problems with the connection
     * @throws TimeOutException if the timeout has expired
     */
    private FamilyMember getFamilyMember(List<FamilyMember> familyMembers) throws ServerException, TimeOutException {
        Display.println(familyMembers.size());
        Display.showFamilyMembers(familyMembers);
        int choice = getChoice(1, familyMembers.size());
        return familyMembers.get(choice - 1);
    }

    /**
     * Get the turnStatus
     * @return true if it is the turn of the player, false if not
     */
    boolean isYourTurn() {
        return isYourTurn;
    }

    /**
     * Indicate to the player that is not his turn
     * @param code a code sent by the server
     */
    public void notYourTurn(String code) {
        isYourTurn = false;
        (notYourTurnThread = new Thread(new NotYourTurnThread(this))).start();
    }

    /**
     * A private interface to handle the actions with the lambda functions
     */
    @FunctionalInterface
    private interface ActionDefaultMap{
        void handle() throws TimeOutException;
    }
}
