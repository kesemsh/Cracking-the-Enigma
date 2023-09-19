package exceptions;

public class DecryptionDifficultyNotSelectedException extends RuntimeException {
    private final String EXCEPTION_MESSAGE = "Please select a decryption difficulty!";

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
