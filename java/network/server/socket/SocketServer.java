package network.server.socket;

import network.server.Server;
import network.server.database.Database;
import network.server.remotePlayer.RemotePlayer;
import view.cli.Display;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Responsible for all the players connected with socket
 */
public class SocketServer implements Runnable {
    private ServerSocket serverSocket;
    private final   Server server;
    private static final int PORT = Server.SOCKET_PORT;
    private Database database;
    private boolean closed;

    /**
     * Instantiate the SocketServer
     * @throws IOException if there are problems with the instantiation of the server
     */
    public SocketServer()throws IOException{
        this.server = Server.getInstance();
        this.serverSocket = new ServerSocket(PORT);
        this.database = server.getDatabase();
        Display.println("SocketServer ready");

    }

    /**
     * Accept every players trying to connect to the server and create a
     * socketClientHandler to him
     */
    @Override
    public void run() {
        while(!closed){
            try {
                Socket clientSocket = serverSocket.accept();
                SocketClientHandler socketClientHandler = new SocketClientHandler(this, clientSocket);
                Thread t1 = new Thread(socketClientHandler);
                t1.start();
            } catch (IOException e) {
                Display.println(e);
            }
        }
    }

    /**
     * close the socket server
     */
    public void close(){
        closed = true;
    }

    /**
     * Handle the singIn of the user
     * @param username the username of the player
     * @param password the password of the player
     * @return true if the signIn was successful, false otherwise
     */
    boolean singIn(String username, String password) {
        return database.signIn(username,password);
    }

    /**
     * Handle the singUp of the user
     * @param username the username of the player
     * @param password the password of the player
     * @return true if the signUp was successful, false otherwise
     */
    boolean singUp(String username, String password) {
        return database.signUp(username,password);
    }

    /**
     * Let the player to join a room
     * @param remotePlayer the player who wants to join
     * @throws IOException if there are problems with the operation of joining
     */
    void joinRoom(RemotePlayer remotePlayer) throws IOException{
        server.joinRoom(remotePlayer);
    }

}
