package exceptions.machine;

import java.io.IOException;

public class NotAFileException extends IOException {
    private final String EXCEPTION_MESSAGE = "Error - Path received does not represent a file, or the file doesn't exist!";

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
