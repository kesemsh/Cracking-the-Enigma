package machine;

import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.configuration.MachineConfiguration;
import object.machine.state.MachineState;

import java.io.Serializable;
import java.util.List;

public interface Machine extends Serializable {
    int getActiveRotorsCount();

    int getAllRotorsInStorageCount();

    int getAllReflectorsInStorageCount();

    int getKeyCount();

    Character getKeyForIndex(int index);

    MachineState exportState();

    void setConfiguration(MachineConfiguration machineConfiguration);

    String processInput(String stringToProcess);

    void resetConfiguration();

    boolean isCharacterInKeyboard(Character charToCheck);

    List<MachineHistoryPerConfiguration> getHistoryAndStatistics();

    MachineConfiguration getCurrentMachineConfiguration();

    List<Character> getAllKeys();

    boolean isConfigurationSet();
}