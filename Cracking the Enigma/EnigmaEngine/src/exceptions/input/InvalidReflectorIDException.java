package exceptions.input;

public class InvalidReflectorIDException extends Exception {
    private final String enteredReflectorID;
    private final String EXCEPTION_MESSAGE = "Invalid rotor ID entered: \"%s\"! You must enter a number from the list!";

    public InvalidReflectorIDException(String enteredReflectorID) {
        this.enteredReflectorID = enteredReflectorID;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, enteredReflectorID);
    }
}
