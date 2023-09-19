package exceptions.input;

public class InvalidRotorIDException extends Exception {
    private final int MINIMUM_ROTOR_ID = 1;
    private final String enteredRotorID;
    private final int maximumRotorID;
    private final String EXCEPTION_MESSAGE = "Invalid rotor ID entered: \"%s\"! Expected number between %d and %d.";

    public InvalidRotorIDException(String enteredRotorID, int maximumRotorID) {
        this.enteredRotorID = enteredRotorID;
        this.maximumRotorID = maximumRotorID;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, enteredRotorID, MINIMUM_ROTOR_ID, maximumRotorID);
    }
}
