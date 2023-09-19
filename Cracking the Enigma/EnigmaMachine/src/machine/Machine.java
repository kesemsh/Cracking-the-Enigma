package machine;

import machine.components.dictionary.Dictionary;
import machine.components.storage.MachineStorage;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.configuration.MachineConfiguration;
import object.machine.state.MachineState;

import java.io.Serializable;
import java.util.List;

public interface Machine extends Serializable, Cloneable {
    int getActiveRotorsCount();

    int getAllRotorsInStorageCount();

    int getAllReflectorsInStorageCount();

    int getKeyCount();

    Character getKeyForIndex(int index);

    MachineState exportState();

    void setConfiguration(MachineConfiguration machineConfiguration, boolean saveToHistory);

    String processInput(String stringToProcess, boolean addMessageToHistory, boolean saveMessageForLater);

    void insertAccumulatedMessageToHistory();

    void resetConfiguration();

    boolean isCharacterInKeyboard(Character charToCheck);

    List<MachineHistoryPerConfiguration> getHistoryAndStatistics();

    MachineConfiguration getCurrentMachineConfiguration();

    MachineConfiguration getInitialMachineConfiguration();

    List<Character> getAllKeys();

    boolean isConfigurationSet();

    MachineStorage getMachineStorage();

    Dictionary getDictionary();

    Machine clone();

    int getAgentsCount();
}