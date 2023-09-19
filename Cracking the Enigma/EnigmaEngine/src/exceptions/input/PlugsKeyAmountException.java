package exceptions.input;

public class PlugsKeyAmountException extends Exception {
    private final int enteredKeysAmount;
    private final int maximumKeysAmount;
    private final String EXCEPTION_MESSAGE = "Error - Invalid plugs string entered! Expected 0 to %d keys in an even amount, but received %d!";

    public PlugsKeyAmountException(int enteredKeysAmount, int maximumKeysAmount) {
        this.enteredKeysAmount = enteredKeysAmount;
        this.maximumKeysAmount = maximumKeysAmount;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, maximumKeysAmount, enteredKeysAmount);
    }
}
