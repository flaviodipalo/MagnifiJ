package controller.exceptions;

import java.io.IOException;

/**
 * This exception is thrown whenever a network error occurs
 */
public class ServerException extends IOException {

    public ServerException(String message){
        super(message);
    }

    public ServerException(String message, Throwable e){
        super(message, e);
    }

    public ServerException(Throwable e){
        super("connection refused due to server problems", e);
    }

}
