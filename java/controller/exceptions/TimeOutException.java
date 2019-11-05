package controller.exceptions;

/**
 * This exception is thrown when the time runs out
 */
public class TimeOutException extends Exception {
    public TimeOutException(Throwable e){
        super(e);
    }

    public TimeOutException(){
        //
    }
}
