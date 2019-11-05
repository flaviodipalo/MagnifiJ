package network.server.socket;

import controller.exceptions.ServerException;
import view.cli.Display;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class which has the job to handle a specific client connected with socket
 */
public class SocketClientHandler implements Runnable {
    private Socket socket;
    private SocketServer socketServer;
    private SocketRemotePlayer socketPlayer;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private static final Logger LOGGER = Logger.getLogger(SocketClientHandler.class.getName());

    /**
     * Create the socketClientHandler
     * @param socketServer the pointer to the socketServer
     * @param socket the pointer to the socket of the client
     */
    SocketClientHandler(SocketServer socketServer, Socket socket){
        this.socket = socket;
        this.socketServer = socketServer;
    }

    /**
     * Open the input and output stream
     * @throws IOException if there are problems with the opening of the streams
     */
    private void openIO() throws IOException {

        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Read an object with a timeout
     * @param time time to wait
     * @param scale of the time (seconds, milliseconds, etc)
     * @return the object read
     * @throws ServerException if there is a problem during the reading phase
     * @throws SocketTimeoutException if the timeout has expired
     */
    Object readObject(int time, int scale) throws ServerException, SocketTimeoutException{
        try {
            socket.setSoTimeout(time * scale);
            return objectInputStream.readObject();
        } catch (IOException e) {
            Display.println(e);
            throw new SocketTimeoutException();
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServerException(e);
        }
    }

    /**
     * Send an object to the client
     * @param object the object to send
     * @throws ServerException if there is a problem during the sending phase
     */
    void sendObject(Object object) throws ServerException {
        try {
            objectOutputStream.reset();
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();

        }catch (IOException e){
            throw new ServerException(e);
        }

    }

    /**
     * Get username, password and type of connection from the client
     * @return The string read
     * @throws ServerException if there is a problem during the communication
     */
    public String getMessage() throws ServerException{
        try{
            return (String) objectInputStream.readObject();
        }catch (IOException e) {
            throw new ServerException(e);
        }catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServerException(e);
        }

    }

    /**
     * Handle the login phase
     * @return true if the login is successful, false otherwise
     */
    private boolean login() {
        try {
            String username = getMessage();
            String password = getMessage();
            String choice = getMessage();
            if ("1".equals(choice)) {
                if(socketServer.singIn(username, password)) {
                    sendObject("Logged in");
                    this.socketPlayer = new SocketRemotePlayer(username, this);
                    return true;
                }else{
                    sendObject("Invalid username or password");
                    return false;
                }
            }
            else {
                if(socketServer.singUp(username, password)){
                    sendObject("Logged in");
                    this.socketPlayer = new SocketRemotePlayer(username, this);
                    return true;
                }else{
                    sendObject("Username already exists");
                    return false;
                }

            }
        }catch(Exception e) {
            Display.println(e);
            return false;
        }
    }

    /**
     * Start the thread and let the user to login
     */
    @Override
    public void run() {
        try {
            openIO();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        boolean login;
        do{
            login = login();
        }
        while(!login);
        try {
            socketServer.joinRoom(socketPlayer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            socketPlayer.setOffline(true);
        }
    }

}
