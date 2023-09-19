package exceptions.machine;

import java.io.IOException;

public class MachineSaveException extends IOException {
    private String exceptionMessage = "Could not save the machine to the specified file path! Please select a different path!";

    public MachineSaveException() {}

    public MachineSaveException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String getMessage() {
        return exceptionMessage;
    }
}
