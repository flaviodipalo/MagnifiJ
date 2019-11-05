package network.server;

import controller.Deck;
import controller.exceptions.ServerException;
import network.server.database.Database;
import network.server.remotePlayer.RemotePlayer;
import network.server.rmi.RMIServer;
import network.server.socket.SocketServer;
import util.Parser;
import view.cli.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server class is a singleton. One created it parses from json files
 * everything that the game needs to run properly.
 * It creates two threads in order to manage the rmi and socket
 * connections. When asked it creates the rooms and assign to them the model.players.
 */
public class Server {
    private static final String DIRECTORY_PATH = System.getProperty("user.dir") + "/src/main/resources/gameSave/";
    private static final String FILE_PATH_SPLIT = "saveOfGame";
    private static final String DEFAULT_FILE = ".log.txt";
    private static Server ourInstance = new Server();
    public static final int RMI_PORT = 3050;
    public static final int SOCKET_PORT = 3051;
    private ArrayList<Room> rooms;
    private Deck deck;
    private Database database;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String EXTENSION_OF_GAME_SAVING_FILE = ".ser";

    private Server() {
        database = new Database();
        rooms = new ArrayList<>();
    }

    public static Server getInstance() {
        return ourInstance;
    }

    /**
     * parses the game configurations
     * @throws IOException if there are problems with the parsing of the file
     */
    private void parsingFile() throws IOException {
        Parser parser = new Parser();
        deck = parser.createDeck();
        Display.println("File parsed!");
    }

    /**
     * This function checks the array list of rooms. It manages to create a new Room when needed
     * and insert a player to the room
     * @param player to be added
     */
    public synchronized void joinRoom(RemotePlayer player) throws IOException {
        int id = 0;
        if(!rooms.isEmpty() ) {
            if(alreadyOnline(player)){
                reconnectPlayer(player);
                return;
            }
            for (Room room : getRooms()) {
                if (room.isAvailable()) {
                    room.addPlayer(player);
                    Display.println("The player " + player.getUsername() + " has been added to room #" + room.getIdRoom());
                    return;
                }
                //autoincrement id
                if(id <= room.getIdRoom()){
                    id = room.getIdRoom() +1;
                }
            }
        }
        createRoom(id);
        joinRoom(player);
    }

    /**
     * If an user tries to connect with the nickname associated with an account which is
     * already online
     * @param player the players who tries to connect
     * @return true if the player is already online, false otherwise
     */
    private boolean alreadyOnline(RemotePlayer player){
        for(Room room : getRooms()){
            Iterator<RemotePlayer> iterator = room.getPlayers().iterator();
            while (iterator.hasNext()){
                RemotePlayer remotePlayer = iterator.next();
                if((remotePlayer.getUsername().equals(player.getUsername()) && !room.isClosed())
                        && remotePlayer.isOffline()){
                    iterator.remove();
                    return false;
                }
                else if(remotePlayer.getUsername().equals(player.getUsername())){
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Reconnect a player which is been disconnected
     * @param player the player to reconnect
     */
    private void reconnectPlayer(RemotePlayer player){
        for(Room room : getRooms()){
            for (RemotePlayer onlinePlayer : room.getPlayers()){
                if(onlinePlayer.getUsername().equals(player.getUsername()) && onlinePlayer.isOffline()){
                    room.reconnect(player);
                    return;
                }else {
                    alreadyOnlineWarning(player);

                }
            }
        }
    }

    /**
     * Tell the player that the account is already online
     * @param player the player
     */
    private void alreadyOnlineWarning(RemotePlayer player){
        try {
            player.sendAlreadyOnlineWarning();
        } catch (ServerException e) {
            Display.println(e.getMessage(), e);
        }
    }

    /**
     * Get all the current rooms
     * @return the rooms
     */
    private ArrayList<Room> getRooms() {
        return rooms;
    }

    /**
     * Get the deck
     * @return the deck of cards
     */
    private Deck getDeck() {
        return deck;
    }

    /**
     * create a room and assign a complete new Deck for the beginning of a new match
     */
    private synchronized void createRoom(int id){
        rooms.add(new Room(Deck.newInstance(getDeck()), id));
    }

    public static void main(String[] args) throws AlreadyBoundException, IOException {
        try {
            ourInstance.parsingFile();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Configuration files not found. Please check the " +
                    "correct paths", e);
            return;
        }
        //creates the threads to manage the income connections
        SocketServer socketServer = new SocketServer();
        RMIServer rMIClientHandler = new RMIServer();
        Thread rmiThread = new Thread(rMIClientHandler);
        Thread socketThread = new Thread(socketServer);
        socketThread.start();
        rmiThread.start();
        checkGameSaveFiles();
    }

    /**
     * Check whether or not there are gameFiles. If yes, read the file and start the room
     * in the recovery mode
     */
    private static void checkGameSaveFiles() {
        List<String> results = new ArrayList<>();
        File[] files = new File(DIRECTORY_PATH).listFiles();
        StringBuilder completeFilePath = new StringBuilder(DIRECTORY_PATH);
        assert files != null;
        for (File file : files) {
            if (file.isFile() && !file.getName().equals(DEFAULT_FILE))
                results.add(file.getName());
        }
        if(!results.isEmpty()){
            for(String filePath : results){
                completeFilePath.append(filePath);
                try (FileInputStream fis = new FileInputStream(completeFilePath.toString())) {
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    Room room = (Room) ois.readObject();
                    room.setAllPlayersOffline();
                    room.setRecoveryMode(true);
                    Server.getInstance().rooms.add(room);
                    fis.close();
                    ois.close();
                } catch (IOException | ClassNotFoundException e) {
                    Display.println(e);
                }

            }

        }
    }

    /**
     * Get the database
     * @return the database
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Get the directory path for the gameSave
     * @return the directory
     */
    public static String getDirectoryPath() {
        return DIRECTORY_PATH;
    }

    /**
     * The initial part of the name (before the id) of all the files of the gameSave
     * directory
     * @return the name
     */
    public static String getFilePathSplit(){
        return FILE_PATH_SPLIT;
    }

    /**
     * Get the extension of all the files in the directory gameSave
     * @return the extension
     */
    public static String getExtensionOfGameSavingFile() {
        return EXTENSION_OF_GAME_SAVING_FILE;
    }

    public static void eliminateRoom(int id){
        Server.ourInstance.rooms.removeIf(room -> room.getIdRoom() == id);
    }
}
