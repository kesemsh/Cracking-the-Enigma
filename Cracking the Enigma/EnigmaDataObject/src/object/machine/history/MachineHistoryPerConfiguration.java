package object.machine.history;

import object.machine.configuration.MachineConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MachineHistoryPerConfiguration implements Serializable {
    private final List<String> unprocessedInput = new ArrayList<>();
    private final List<String> processedInput = new ArrayList<>();
    private final List<Long> nanoSecondsPerMessage = new ArrayList<>();
    private final MachineConfiguration machineConfiguration;
    private final int configurationNumber;

    public MachineHistoryPerConfiguration(MachineConfiguration machineConfiguration, int configurationNumber) {
        this.machineConfiguration = machineConfiguration;
        this.configurationNumber = configurationNumber;
    }

    public void addUnprocessedInput(String unprocessedInput) {
        this.unprocessedInput.add(unprocessedInput);
    }

    public void addProcessedInput(String processedInput) {
        this.processedInput.add(processedInput);
    }

    public void addTimeTakenPerMessage(Long timeTaken) { this.nanoSecondsPerMessage.add(timeTaken); }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        final String newLine = System.lineSeparator();

        result.append(String.format("Configuration #%d: ", configurationNumber)).append(machineConfiguration).append(newLine);
        for (int i = 0; i < unprocessedInput.size(); i++) {
            result.append(String.format("%d. <%s> --> <%s> (%,d nano-seconds)", i + 1, unprocessedInput.get(i), processedInput.get(i), nanoSecondsPerMessage.get(i)));
            result.append(newLine);
        }
        result.append(newLine);
        return result.toString();
    }
}