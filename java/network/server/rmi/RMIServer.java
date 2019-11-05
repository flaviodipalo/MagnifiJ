package network.server.rmi;

import network.server.Server;
import network.server.database.Database;
import network.server.remotePlayer.RMINetworkInterface;
import network.server.remotePlayer.RemotePlayer;
import view.cli.Display;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Create the RMIServer, responsible for all the players connected with RMI
 */
public class RMIServer extends UnicastRemoteObject implements Runnable, RMINetworkInterface {
    private transient Server server;
    private Database database;

    /**
     * Instantiate the RMIServer
     * @throws RemoteException if there are problems to instantiate the server
     * @throws AlreadyBoundException if another server is already instantiate
     */
    public RMIServer() throws RemoteException, AlreadyBoundException {
        server = Server.getInstance();
        Display.println("Binding registry... ");
        Registry registry = LocateRegistry.createRegistry(Server.RMI_PORT);
        registry.bind("RMIServer", this);
        Display.println("RMIServer ready!");
        this.database = server.getDatabase();
    }

    @Override
    public void run(){
        //needed just for socket
    }

    /**
     * Check the correct username and password provided to sign in
     * @param username the username of the player
     * @param password the password of the player
     * @return false if the username or password provided are not correct
     */
    @Override
    public boolean singIn(String username, String password) throws RemoteException{
        return database.signIn(username, password);

    }

    /**
     * check the correct username and password provided to sign up
     * it uses the server database to check if the user already exists
     * @param username the username of the player
     * @param password the password of the player
     * @return false if the user already exists
     * @throws RemoteException problems with the connection
     */
    @Override
    public boolean singUp(String username, String password) throws RemoteException{
        return database.signUp(username,password);
    }

    /**
     * This method allow the player to join to a room
     * @param remotePlayer the player who wants to join
     */
    @Override
    public void joinRoom(RemotePlayer remotePlayer) throws RemoteException{
        try {
            server.joinRoom(remotePlayer);
        } catch (IOException e) {
            Display.println(e);
            remotePlayer.setOffline(true);
        }
    }
}
