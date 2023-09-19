package exceptions.input;

public class PlugsDuplicateKeyException extends Exception {
    private final Character duplicateKey;
    private final String EXCEPTION_MESSAGE = "Error - Invalid plugs input entered! The character %s appears in multiple plugs!";

    public PlugsDuplicateKeyException(Character duplicateKey) {
        this.duplicateKey = duplicateKey;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, duplicateKey);
    }
}
