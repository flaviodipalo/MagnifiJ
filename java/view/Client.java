package view;

import controller.exceptions.ServerException;

import java.io.Serializable;

/**
 * The generic client representing the user
 */
public abstract class Client implements Serializable{
    protected ControllerUI controllerUI;
    private String username;


    public Client(ControllerUI controllerUI){
        this.controllerUI = controllerUI;
    }

    public abstract boolean signIn(String username, String password) throws ServerException;

    public abstract boolean signUp(String username, String password) throws ServerException;

    public ControllerUI getControllerUI() {
        return controllerUI;
    }

    public abstract void start();

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public abstract void sendAction(Object action) throws ServerException;

    public abstract void onClose();


}
