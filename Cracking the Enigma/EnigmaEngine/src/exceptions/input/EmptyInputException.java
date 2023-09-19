package exceptions.input;

public class EmptyInputException extends Exception {
    private final String EXCEPTION_MESSAGE = "Input must not be empty!";

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
