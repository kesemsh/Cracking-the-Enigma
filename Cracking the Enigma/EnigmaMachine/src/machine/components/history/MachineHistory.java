package machine.components.history;

import object.machine.configuration.MachineConfiguration;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.history.SingleProcessHistory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineHistory implements Serializable {
    private final List<MachineConfiguration> machineConfigurationsInOrder = new ArrayList<>();
    private final List<MachineHistoryPerConfiguration> machineHistoryList = new ArrayList<>();
    private final Map<MachineConfiguration, MachineHistoryPerConfiguration>  machineConfigurationToHistory = new HashMap<>();
    private final Map<MachineConfiguration, SingleProcessHistory>  machineConfigurationToSavedProcessHistory = new HashMap<>();
    private int processedMessagesCount = 0;
    private int configurationsCount = 0;

    public int getProcessedMessagesCount() { return processedMessagesCount; }

    public void addConfigurationToHistory(MachineConfiguration machineConfiguration) {
        if (!machineConfigurationsInOrder.contains(machineConfiguration)) {
            machineConfigurationToHistory.put(machineConfiguration, new MachineHistoryPerConfiguration(machineConfiguration, ++configurationsCount));
            machineConfigurationsInOrder.add(machineConfiguration);
            machineHistoryList.add(machineConfigurationToHistory.get(machineConfiguration));
        }
    }

    public void addMessageToHistory(MachineConfiguration machineConfiguration, String unprocessedMessage, String processedMessage, Long timeTaken) {
        machineConfigurationToHistory.get(machineConfiguration).addSingleProcessHistory(unprocessedMessage, processedMessage, timeTaken);
        processedMessagesCount++;
    }

    public void saveMessageForLater(MachineConfiguration machineConfiguration, String unprocessedMessage, String processedMessage, Long timeTaken) {
        if (machineConfigurationToSavedProcessHistory.get(machineConfiguration) == null) {
            machineConfigurationToSavedProcessHistory.put(machineConfiguration, new SingleProcessHistory(unprocessedMessage, processedMessage, timeTaken));
        } else {
            SingleProcessHistory currentHistory = machineConfigurationToSavedProcessHistory.get(machineConfiguration);

            machineConfigurationToSavedProcessHistory.put(machineConfiguration, new SingleProcessHistory(currentHistory.getUnprocessedInput() + unprocessedMessage,
                    currentHistory.getProcessedInput() + processedMessage, currentHistory.getTimeTaken() + timeTaken));
        }
    }

    public void insertAccumulatedMessageToHistory() {
        List<MachineConfiguration> insertedConfigurations = new ArrayList<>();

        machineConfigurationToSavedProcessHistory.keySet().forEach(machineConfiguration -> {
            SingleProcessHistory savedHistory = machineConfigurationToSavedProcessHistory.get(machineConfiguration);

            addMessageToHistory(machineConfiguration, savedHistory.getUnprocessedInput(), savedHistory.getProcessedInput(), savedHistory.getTimeTaken());
            insertedConfigurations.add(machineConfiguration);
        });

        insertedConfigurations.forEach(machineConfigurationToSavedProcessHistory::remove);
    }

    public List<MachineHistoryPerConfiguration> exportMachineHistory() {
        return machineHistoryList;
    }
}