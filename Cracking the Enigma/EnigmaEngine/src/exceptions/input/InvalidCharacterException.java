package exceptions.input;

public class InvalidCharacterException extends Exception {
    private final Character enteredCharacter;
    private final String EXCEPTION_MESSAGE = "Error - \"%s\" is an invalid character!";

    public InvalidCharacterException(Character enteredCharacter) {
        this.enteredCharacter = enteredCharacter;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, enteredCharacter);
    }
}
