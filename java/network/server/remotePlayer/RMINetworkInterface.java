package network.server.remotePlayer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMIInterface to let the user to singIn, singUp and joining a room
 */
public interface RMINetworkInterface extends Remote, Serializable{

    boolean singIn(String username, String password) throws RemoteException;

    boolean singUp(String username, String password) throws RemoteException;

    void joinRoom(RemotePlayer remotePlayer) throws RemoteException;

}
