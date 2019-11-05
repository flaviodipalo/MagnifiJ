package network.client.socket;

import controller.exceptions.ServerException;
import view.Client;
import view.ControllerUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains all the methods used to communicate through the socket connection
 *
 */
public class SocketClient extends Client{
    private static final int PORT = 3051;
    private transient Socket socket;
    private static final String SIGN_IN = "1";
    private static final String SIGN_UP = "2";
    private final String ip;
    private transient ObjectInputStream objectInputStream;
    private transient ObjectOutputStream objectOutputStream;
    private static final transient  Logger LOGGER = Logger.getLogger(SocketClient.class.getName());
    private transient Thread t1;

    public SocketClient(String ip, ControllerUI controllerUI) throws ServerException{
        super(controllerUI);
        this.ip = ip;
        connect();
    }

    /**
     * Attempts the connection
     * @throws ServerException if the port is busy
     */

    private void connect() throws ServerException {

        try {
            socket = new Socket(ip, PORT);
            openIO();
        }catch (IOException e){
            throw new ServerException(e);
        }
    }

    /**
     * Open the input and output socket streams
     * @throws IOException if there's any problem opening the input and output stream (connection lost)
     */
    private void openIO() throws IOException {
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Try to sign in.
     * @param username given by the user
     * @param password given by the user
     * @return true if has success
     * @throws ServerException if there's any problem with the network
     */
    @Override
    public boolean signIn(String username, String password) throws ServerException {
        try{
            return sendCredentials(username, password, SIGN_IN);
        }catch (IOException e){
            throw new ServerException(e);
        }

    }

    /**
     * Send to the server the credentials given to the user
     * @param username given by the user
     *
     * @param password given by the user
     * @param choice between sign in or sign up
     * @return returns true if the user has successfully logged in else false
     * @throws IOException if there's any network problem
     */
    private boolean sendCredentials(String username, String password, String choice) throws IOException{
        objectOutputStream.writeObject(username);
        objectOutputStream.flush();
        objectOutputStream.writeObject(password);
        objectOutputStream.flush();
        objectOutputStream.writeObject(choice);
        objectOutputStream.flush();
        objectOutputStream.reset();
        try {
            String response = (String) objectInputStream.readObject();
            return "Logged in".equals(response);
        }catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }

    }

    /**
     * Try to sign up
     * @param username given by the user
     * @param password given by the user
     * @return true if the username does not exists and log on.
     * @throws ServerException if there is any network problem
     */
    @Override
    public boolean signUp(String username, String password) throws ServerException{
        try{
            return sendCredentials(username, password, SIGN_UP);
        }catch (IOException e){
            throw new ServerException(e);
        }
    }


    /**
     * Create the turn handler thread
     */
    @Override
    public void start() {
        getControllerUI().setClient(this);
        TurnHandler turnHandler = new TurnHandler(this);
        t1 = new Thread(turnHandler);
        t1.start();
    }

    /**
     * Interrupt the TurnHandler and close the connection with the player
     */
    @Override
    public void onClose(){
        t1.interrupt();
        closeIO();
    }

    /**
     * Close the input and output socket streams
     */
    void closeIO(){
        try {
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.FINEST, e.getMessage(), e);
        }
    }

    /**
     * Send an action to the server
     * @param object the object to send
     * @throws ServerException if there are problems with the connection with the server
     */
    public void sendAction(Object object) throws ServerException{
        try {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Get the InputStreamChannel
     * @return the inputStream
     */
    ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }
}
