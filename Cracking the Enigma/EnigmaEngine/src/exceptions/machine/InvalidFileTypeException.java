package exceptions.machine;

import java.io.IOException;

public class InvalidFileTypeException extends IOException {
    private final String ACCEPTED_FILE_EXTENSION = ".xml";
    private final String fileNameReceived;
    private final String EXCEPTION_MESSAGE = "Error - Invalid file type entered! Expected file of type \"%s\", But received file: \"%s\"!";

    public InvalidFileTypeException(String fileNameReceived) {
        this.fileNameReceived = fileNameReceived;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, ACCEPTED_FILE_EXTENSION, fileNameReceived);
    }
}
