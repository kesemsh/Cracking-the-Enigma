package exceptions;

public class NotEnoughRotorStartPositionsSelectedException extends RuntimeException {
    private final int expectedAmountOfRotorStartPositions;
    private final int selectedAmountOfRotorStartPositions;
    private final String EXCEPTION_MESSAGE = "Please select %d more rotor start positions!";

    public NotEnoughRotorStartPositionsSelectedException(int expectedAmountOfRotorStartPositions, int selectedAmountOfRotorStartPositions) {
        this.expectedAmountOfRotorStartPositions = expectedAmountOfRotorStartPositions;
        this.selectedAmountOfRotorStartPositions = selectedAmountOfRotorStartPositions;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, expectedAmountOfRotorStartPositions - selectedAmountOfRotorStartPositions);
    }
}
