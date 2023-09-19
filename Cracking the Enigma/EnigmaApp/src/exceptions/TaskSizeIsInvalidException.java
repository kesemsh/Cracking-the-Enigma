package exceptions;

public class TaskSizeIsInvalidException extends RuntimeException{
    private final String EXCEPTION_MESSAGE = "Please select a task size above 0!";

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
