package controller.states;

/**
 * State caused by the activation of SistoIV  overrides the method getSupportBonus.
 */
public class SistoIVShowSupportBonusState implements ShowSupportBonusState{
   ShowSupportBonusState state;
   public SistoIVShowSupportBonusState(ShowSupportBonusState state)
   {this.state = state;}
    public int getSupportBonus(){
        return 5;
    }
}
