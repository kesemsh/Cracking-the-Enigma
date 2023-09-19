package components.tabs.machine.state.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import object.machine.state.MachineState;

public class MachineStateView {
    @FXML private Label activeRotorsCount;
    @FXML private Label reflectorsInStorageCount;
    @FXML private Label processedMessagesCount;

    public void updateMachineState(MachineState machineState) {
        activeRotorsCount.setText(String.format("%d/%d", machineState.getActiveRotorsCount(), machineState.getAvailableRotorsCount()));
        reflectorsInStorageCount.setText(String.valueOf(machineState.getReflectorsInStorageCount()));
        processedMessagesCount.setText(String.valueOf(machineState.getProcessedMessagesCount()));
    }
}