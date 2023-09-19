package exceptions.machine;

import java.io.IOException;

public class InvalidMachinePathException extends IOException {
    private final String EXCEPTION_MESSAGE = "Input is not a valid machine file path!";

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
