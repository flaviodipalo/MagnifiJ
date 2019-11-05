package network.protocol;

import model.players.FamilyMember;
import model.cards.leadercards.LeaderCard;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to allow the player to send an action to the server
 * It has multiple parameters and for every occasion.
 * Depending on the code which is unique, the server knows which
 * parameter get
 */
public class ClientAction implements Serializable{
    private String code;
    private FamilyMember familyMember;
    private LeaderCard leaderCard;
    private int servants;
    private String position;
    private List<? extends Object> objects;
    private Object object;

    public ClientAction(String code){
        this.code = code;

    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object card) {
        this.object = card;
    }

    public void setObjects(List<? extends Object> objects) {
        this.objects = objects;
    }

    public List<? extends Object> getObjects() {
        return objects;
    }

    public String getCode() {
        return code;
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(FamilyMember familyMember) {
        this.familyMember = familyMember;
    }

    public LeaderCard getLeaderCard() {
        return leaderCard;
    }

    public void setLeaderCard(LeaderCard leaderCard) {
        this.leaderCard = leaderCard;
    }

    public int getServants() {
        return servants;
    }

    public void setServants(int servants) {
        this.servants = servants;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
