package machine.components.history;

import object.machine.configuration.MachineConfiguration;
import object.machine.history.MachineHistoryPerConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineHistory implements Serializable {
    private final List<MachineConfiguration> machineConfigurationsInOrder = new ArrayList<>();
    private final Map<MachineConfiguration, MachineHistoryPerConfiguration>  machineConfigurationToHistory = new HashMap<>();
    private int processedMessagesCount = 0;
    private int configurationsCount = 0;

    public int getProcessedMessagesCount() { return processedMessagesCount; }

    public void addConfigurationToHistory(MachineConfiguration machineConfiguration) {
        if (!machineConfigurationsInOrder.contains(machineConfiguration)) {
            machineConfigurationsInOrder.add(machineConfiguration);
            machineConfigurationToHistory.put(machineConfiguration, new MachineHistoryPerConfiguration(machineConfiguration, ++configurationsCount));
        }
    }

    public void addMessageToHistory(MachineConfiguration machineConfiguration, String unprocessedMessage, String processedMessage, Long timeTaken) {
        machineConfigurationToHistory.get(machineConfiguration).addUnprocessedInput(unprocessedMessage);
        machineConfigurationToHistory.get(machineConfiguration).addProcessedInput(processedMessage);
        machineConfigurationToHistory.get(machineConfiguration).addTimeTakenPerMessage(timeTaken);
        processedMessagesCount++;
    }

    public List<MachineHistoryPerConfiguration> exportMachineHistory() {
        List<MachineHistoryPerConfiguration> machineHistory = new ArrayList<>();

        machineConfigurationsInOrder.forEach(x -> machineHistory.add(machineConfigurationToHistory.get(x)));

        return machineHistory;
    }
}