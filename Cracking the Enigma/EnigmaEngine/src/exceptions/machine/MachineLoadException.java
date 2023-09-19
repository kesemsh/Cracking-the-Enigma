package exceptions.machine;

public class MachineLoadException extends Exception {
    private String exceptionMessage = "Error - Could not load a machine from the specified file path!";

    public MachineLoadException() {}

    public MachineLoadException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String getMessage() {
        return exceptionMessage;
    }
}
