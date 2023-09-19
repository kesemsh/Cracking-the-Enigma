package object.machine.history;

import java.io.Serializable;

public class SingleProcessHistory implements Serializable {
    private final String unprocessedInput;
    private final String processedInput;
    private final Long timeTaken;

    public SingleProcessHistory(String unprocessedInput, String processedInput, Long timeTaken) {
        this.unprocessedInput = unprocessedInput;
        this.processedInput = processedInput;
        this.timeTaken = timeTaken;
    }

    public String getUnprocessedInput() {
        return unprocessedInput;
    }

    public String getProcessedInput() {
        return processedInput;
    }

    public long getTimeTaken() {
        return timeTaken;
    }
}