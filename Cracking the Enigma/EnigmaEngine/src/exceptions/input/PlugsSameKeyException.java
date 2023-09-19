package exceptions.input;

public class PlugsSameKeyException extends Exception {
    private final Character duplicateKey;
    private final String EXCEPTION_MESSAGE = "Invalid plugs string entered! The character %s cannot be paired to itself!";

    public PlugsSameKeyException(Character duplicateKey) {
        this.duplicateKey = duplicateKey;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, duplicateKey);
    }
}
