package controller;

import network.server.Room;
import network.server.Server;
import view.cli.Display;

import java.io.*;

/**
 * Class for handle the persistence of a game
 */
class PersistenceHandler implements Serializable{
    private String gameFilePath;
    private transient ObjectOutputStream oos;
    private Master master;
    private File file;
    private transient FileOutputStream fos;

    PersistenceHandler(Master master, int id){
        this.gameFilePath = Server.getDirectoryPath() + Server.getFilePathSplit() + id + Server.getExtensionOfGameSavingFile();
        this.master = master;
    }

    /**
     * Create the file with the id of the specific game
     */
    void createFile(){
        file = new File(gameFilePath);
        initializeStreams();
    }


    /**
     * Initialize the streams and create the file
     */
    private void initializeStreams(){
        try {
            fos = new FileOutputStream(file, false);
            oos = new ObjectOutputStream(fos);
        } catch (IOException e) {
            Display.println(e);
        }
    }

    /**
     * Delete the file after the finish of the game
     * @return true if the file was successfully deleted, false otherwise
     */
    boolean deleteGameFile() {
        File file = new File(gameFilePath);
        try {
            fos.close();
        } catch (IOException e) {
            Display.println(e);
        }
        return file.delete();
    }

    /**
     * Save the game status
     * @param room the actual status of the room
     * @param recoveryMode if the game is been recovered after a crash of the server
     */
    void saveGameStatus(Room room, boolean recoveryMode) {
        try {
            if(recoveryMode) {
                initializeStreams();
                master.setRecoveryMode(false);
            }
            new FileOutputStream(file).close();
            initializeStreams();
            oos.writeObject(room);
            oos.reset();
        } catch (IOException e) {
            Display.println(e);
        }
    }
}
