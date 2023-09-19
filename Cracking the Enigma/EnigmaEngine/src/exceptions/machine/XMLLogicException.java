package exceptions.machine;

public class XMLLogicException extends Exception{
    private final String EXCEPTION_MESSAGE;

    public XMLLogicException(String message) { EXCEPTION_MESSAGE = message; }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
