package network.protocol;

import java.io.Serializable;

/**
 * The actions sent by the server
 */
public class ServerAction implements Serializable{
    private String code;
    private String ranking;
    private Object firstObject;
    private Object secondObject;
    private String[] positions;
    private int timeout;

    public ServerAction(String code){
        this.code = code;
    }

    public String[] getPositions() {
        return positions;
    }

    public void setPositions(String[] positions) {
        this.positions = positions;
    }

    public String getCode() {
        return code;
    }

    public Object getFirstObject() {
        return firstObject;
    }

    public void setFirstObject(Object firstObject) {
        this.firstObject = firstObject;
    }

    public Object getSecondObject() {
        return secondObject;
    }

    public void setSecondObject(Object secondObject) {
        this.secondObject = secondObject;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }
}
