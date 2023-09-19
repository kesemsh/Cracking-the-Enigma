package exceptions.input;

public class RotorsDuplicateIDException extends Exception {
    private final int duplicateID;
    private final String EXCEPTION_MESSAGE = "Invalid rotor IDs input entered! The ID %d appears multiple times!";

    public RotorsDuplicateIDException(int duplicateID) {
        this.duplicateID = duplicateID;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, duplicateID);
    }
}
