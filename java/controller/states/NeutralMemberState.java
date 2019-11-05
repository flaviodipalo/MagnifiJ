package controller.states;

import java.io.Serializable;

/**
 * this interface represent the State for the method setNeutralMember
 */
@FunctionalInterface

public interface NeutralMemberState extends Serializable {
    void setNeutralMember();
}
