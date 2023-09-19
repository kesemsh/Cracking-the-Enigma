package object.machine.history;

import object.machine.configuration.MachineConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MachineHistoryPerConfiguration implements Serializable {
    private final List<SingleProcessHistory> singleProcessHistories = new ArrayList<>();
    private final MachineConfiguration machineConfiguration;
    private final int configurationNumber;

    public MachineHistoryPerConfiguration(MachineConfiguration machineConfiguration, int configurationNumber) {
        this.machineConfiguration = machineConfiguration;
        this.configurationNumber = configurationNumber;
    }

    public void addSingleProcessHistory(String unprocessedInput, String processedInput, Long timeTaken) {
        singleProcessHistories.add(new SingleProcessHistory(unprocessedInput, processedInput, timeTaken));
    }

    public MachineConfiguration getMachineConfiguration() {
        return machineConfiguration;
    }

    public List<SingleProcessHistory> getSingleProcessHistories() {
        return singleProcessHistories;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        final String newLine = System.lineSeparator();

        result.append(String.format("Configuration #%d: ", configurationNumber)).append(machineConfiguration).append(newLine);
        for (int i = 0; i < singleProcessHistories.size(); i++) {
            result.append(String.format("%d. <%s> --> <%s> (%,d nano-seconds)", i + 1, singleProcessHistories.get(i).getUnprocessedInput(), singleProcessHistories.get(i).getProcessedInput(), singleProcessHistories.get(i).getTimeTaken()));
            result.append(newLine);
        }
        result.append(newLine);
        return result.toString();
    }
}