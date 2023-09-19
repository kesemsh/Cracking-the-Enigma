package exceptions;

public class IncompletePlugPairSelectedException extends RuntimeException {
    private final String EXCEPTION_MESSAGE = "Incomplete plug pair selected! Please complete the plug selection, or remove the incomplete plug!";

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
