package exceptions.machine;

public class ConfigurationNotSetException extends Exception {
    private final String EXCEPTION_MESSAGE = "Machine configuration has not been set!";

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
