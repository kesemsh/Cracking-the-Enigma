package exceptions.input;

public class InvalidWordException extends Exception {
    private final String enteredWord;
    private final String EXCEPTION_MESSAGE = "The word \"%s\" is not in the dictionary!";

    public InvalidWordException(String enteredWord) {
        this.enteredWord = enteredWord;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, enteredWord);
    }
}
