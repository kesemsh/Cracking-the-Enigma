package exceptions;

public class NotEnoughRotorsSelectedException extends RuntimeException {
    private final int expectedAmountOfRotors;
    private final int selectedAmountOfRotors;
    private final String EXCEPTION_MESSAGE = "Please select %d more rotors!";

    public NotEnoughRotorsSelectedException(int expectedAmountOfRotors, int selectedAmountOfRotors) {
        this.expectedAmountOfRotors = expectedAmountOfRotors;
        this.selectedAmountOfRotors = selectedAmountOfRotors;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, expectedAmountOfRotors - selectedAmountOfRotors);
    }
}
