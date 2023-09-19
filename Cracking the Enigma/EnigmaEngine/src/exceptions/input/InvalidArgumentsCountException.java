package exceptions.input;

public class InvalidArgumentsCountException extends Exception {
    private final int expectedAmountOfArguments;
    private final int receivedAmountOfArguments;
    private final String EXCEPTION_MESSAGE = "Error - Expected %d arguments, but received %d!";

    public InvalidArgumentsCountException(int expectedAmountOfArguments, int receivedAmountOfArguments) {
        this.expectedAmountOfArguments = expectedAmountOfArguments;
        this.receivedAmountOfArguments = receivedAmountOfArguments;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, expectedAmountOfArguments, receivedAmountOfArguments);
    }
}
