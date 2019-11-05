package network.server;

import controller.Deck;
import controller.GameConfiguration;
import controller.Master;
import controller.exceptions.ServerException;
import network.server.remotePlayer.RemotePlayer;
import model.players.Player;
import view.cli.Display;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * The class which is responsible for everything happening before the game starts
 */
public class Room implements Serializable{
    static final long serialVersionUID = 42L;
    private static final int MAXSIZE =4;
    private ArrayList<RemotePlayer> players;
    private int roomTimerTemp;
    private Deck deck;
    private transient Timer timer;
    private boolean isClosed;
    private static final int MIN_PLAYER = 2;
    private final int idRoom;
    private Master master;
    private boolean recoveryMode;
    private static final int MIN_PLAYERS_AFTER_RECOVERY = 2;

    /**
     * Instantiate the room
     * @param deck the cards
     * @param idRoom the roomId
     */
    Room(Deck deck, int idRoom){
        this.players = new ArrayList<>();
        this.deck = deck;
        this.isClosed = false;
        this.idRoom = idRoom;
    }

    public Room(int idRoom){
        this.players = new ArrayList<>();
        this.idRoom = idRoom;
    }

    /**
     * Set the deck
     * @param deck the deck of cards
     */
    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    /**
     * If a room is available
     * @return true if it is available, false otherwise
     */
    boolean isAvailable(){
        return players.size() < MAXSIZE && !isClosed();
    }

    /**
     * If the room is closed
     * @return true if the room is closed, false otherwise
     */
    boolean isClosed(){
        return isClosed;
    }

    /**
     * Send a broadcast message to all the users
     * @param message the message to send
     */
    public void broadcast(String message) {
        if(isClosed()){
            broadcastOnGameStarted(message);
        }
        else {
            broadcastRoomNotClosed(message);
        }

    }

    /**
     * Send a broadcast message to all the users (called only when the game is not
     * started yet)
     * @param message the message to send
     */
    private void broadcastRoomNotClosed(String message){
        Iterator<RemotePlayer> iterator = players.iterator();
        RemotePlayer p;
        while (iterator.hasNext()) {
            p = iterator.next();
            if(!p.isOffline()) {
                try {
                    p.sendMessage(message);
                } catch (IOException e) {
                    Display.println(e);
                    String username = p.getUsername();
                    p.setOffline(true);
                    if(!isClosed)
                        iterator.remove();
                    checkRoom();
                    broadcast("The player " + username + " has left the room");
                }
            }
        }
    }

    /**
     * Send a broadcast message to all the users (called only when the game is already
     * started)
     * @param message the message to send
     */
    private void broadcastOnGameStarted(String message){
        for (Player player : master.getField().getPlayersOrder()){
            RemotePlayer p = player.getRemotePlayer();
            if(!p.isOffline()){
                try {
                    p.sendMessage(message);
                }catch (IOException e){
                    Display.println(e);
                    p.setOffline(true);
                    broadcastOnGameStarted("The player " + p.getUsername() + " has left the room");
                }
            }

        }
    }

    /**
     * Cancel the timer if the players are less then the minimum number to play a game
     */
    private void checkRoom(){
        if(players.size() < MIN_PLAYER){
            timer.cancel();
        }
    }

    /**
     * Add a player to the room
     * @param player the player to add
     */
    void addPlayer(RemotePlayer player) {
        this.players.add(player);
        Iterator<RemotePlayer> iterator = players.iterator();
        RemotePlayer p;
        while(iterator.hasNext()) {
            p = iterator.next();
            try {
                if (p.equals(player))
                    p.sendMessage("Welcome player");
                else
                    p.sendMessage("The player " + player.getUsername() + " has joined your room!");
            }catch(ServerException e){
                Display.println(e);
                p.setOffline(true);
                if(!isClosed)
                    iterator.remove();
                broadcast("The player " + p.getUsername() + " has left the room");
            }
        }
        //Starting the countdown
        if (players.size() >= MIN_PLAYER) {
            this.broadcast("Countdown has just started!");
            this.countDown(this);
            //Closing the room
        }
    }

    /**
     * Close the room
     */
    private void closeRoom(){
        this.broadcast("The room is now closed!");
        this.broadcast("The game is about to begin");
        this.isClosed = true;


        master = new Master(this);
        master.startGame();
        this.broadcast("Game Started");
    }

    /**
     * Start the timer
     * @param room the reference to the room itself
     */
    private void countDown(final Room room){
        int delay = 0;
        int period = 1000;
        timer = new Timer();
        roomTimerTemp = GameConfiguration.getInstance().getRoomTimer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                room.roomTimerTemp = setInterval(room, roomTimerTemp);
                if(roomTimerTemp % 5 == 0 && roomTimerTemp > 0)
                    broadcast(roomTimerTemp + " seconds left!");
            }
        }, delay, period);
    }

    /**
     * Decrease every "period" milliseconds the timer of one
     * @param room the room itself
     * @param roomTimerTemp the actual timer
     * @return the new timer
     */
    private static int  setInterval(Room room, int roomTimerTemp){
        int temp = roomTimerTemp;
        if (roomTimerTemp == 1) {
            room.timer.cancel();
            room.broadcast("Time has run out!");
            //Closing the room
            if(room.getPlayers().size() >= 2)
                room.closeRoom();
            else
                room.broadcast("Not enough model.players");
        }
        return --temp;
    }

    /**
     * Get the deck
     * @return the deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Get the id of the room
     * @return the id
     */
    public int getIdRoom() {
        return idRoom;
    }

    /**
     * The number of the players currently in the room
     * @return the number of the players in the room
     */
    public int getNumberOfPlayers(){
        return players.size();

    }

    /**
     * Get the players currently in the room
     * @return the players
     */
    public ArrayList<RemotePlayer> getPlayers() {
        return players;
    }

    /**
     * Reconnect a player after a disconnection if this player is trying to login
     * @param player the player who is trying to reconnect
     */
    public void reconnect(RemotePlayer player) {
        for (Player currentPlayer : master.getField().getPlayersOrder()){
            if(currentPlayer.getRemotePlayer().getUsername().equals(player.getUsername())){
                currentPlayer.setRemotePlayer(player);
                currentPlayer.getRemotePlayer().setOffline(false);
            }
        }

        try {
            player.sendMessage("Welcome back");
            player.reconnect(master.getField());
        } catch (ServerException e) {
            Display.println(e);
            player.setOffline(true);
        }
        broadcast("Info: " + player.getUsername() + " is playing again!");
        (new Thread(new RecoveryModeThread())).start();
    }

    /**
     * Start the game
     */
    public void startGame() {
        for (RemotePlayer player : getPlayers()){
            try {
                player.startGame();
            } catch (ServerException e) {
                Display.println(e);
                player.setOffline(true);
            }
        }
    }

    /**
     * Set all the players in the room offline (called when in recovery mode)
     */
    void setAllPlayersOffline(){
        for(RemotePlayer player : players)
            player.setOffline(true);
    }

    /**
     * Set the recovery mode
     * @param recoveryMode true if the recovery mode is started, else otherwise
     */
    void setRecoveryMode(boolean recoveryMode){
        this.recoveryMode = recoveryMode;
    }

    /**
     * Check how many players are online
     * @return the number of players currently online
     */
    private int getNumberOfPlayersOnline(){
        int numberOfPlayers = 0;
        for(Player player : master.getField().getPlayersOrder())
            if(!player.getRemotePlayer().isOffline())
                numberOfPlayers++;
        return numberOfPlayers;
    }

    private class RecoveryModeThread implements Runnable{

        @Override
        public void run() {
            if(recoveryMode && getNumberOfPlayersOnline() >= MIN_PLAYERS_AFTER_RECOVERY){
                setRecoveryMode(false);
                master.startAfterRecovery();
            }
        }
    }
}
