package exceptions;

public class ReflectorNotSelectedException extends RuntimeException {
    private final String EXCEPTION_MESSAGE = "Please select a reflector to use!";

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
