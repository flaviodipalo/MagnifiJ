package controller.exceptions;

/**
 * This exception is thrown if the client place a family member in the same
 * tower or in a similar zone where he should not go
 */
public class ThereIsAnotherFamilyMemberOnSimilarPosition extends Exception{
    public ThereIsAnotherFamilyMemberOnSimilarPosition() {
        super("There is another family member of the same color in a position of the same area");
    }
}
