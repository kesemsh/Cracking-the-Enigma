package components.tabs.machine;

import components.tabs.AppTab;
import components.tabs.machine.configuration.setter.MachineConfigurationSetter;
import components.tabs.machine.state.view.MachineStateView;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import object.machine.configuration.MachineConfiguration;
import object.machine.state.MachineState;
import components.configuration.view.MachineConfigurationView;
import java.util.List;
import java.util.function.Consumer;

public class MachineTab extends AppTab {
    @FXML private MachineConfigurationView initialConfigurationViewController;
    @FXML private MachineStateView machineStateViewController;
    @FXML private MachineConfigurationSetter machineConfigurationSetterController;

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        initialConfigurationViewController.setConfigurationTitle("Initial Configuration:");
    }

    public void updateMachineDetails(MachineState machineState) {
        machineStateViewController.updateMachineState(machineState);
        machineConfigurationSetterController.updateRotorsCount(machineState.getActiveRotorsCount(), machineState.getAvailableRotorsCount());
    }

    public void updateInitialConfigurationView(MachineConfiguration initialMachineConfiguration) {
        initialConfigurationViewController.setConfiguration(initialMachineConfiguration);
    }

    public void setUpConfigurationSetter(Consumer<MachineConfiguration> machineConfigurationConsumer, EventHandler<ActionEvent> randomConfigurationEvent, Consumer<String> errorMessageConsumer) {
        machineConfigurationSetterController.setUpConfigurationSetter(machineConfigurationConsumer, randomConfigurationEvent, errorMessageConsumer);
    }

    public void setRotorsCount(int activeRotorsCount, int totalRotorsCount) {
        machineConfigurationSetterController.updateRotorsCount(activeRotorsCount, totalRotorsCount);
    }

    public void updateAllKeys(List<Character> allKeys, int rotorsCount) {
        machineConfigurationSetterController.updateAllKeys(allKeys, rotorsCount);
    }

    public void setUpReflectorIDSelector(int reflectorsInStorageCount) {
        machineConfigurationSetterController.setUpReflectorIDSelector(reflectorsInStorageCount);
    }

    public void setUpInitialConfigurationView(BooleanProperty isConfigurationSet) {
        initialConfigurationViewController.isConfigurationSetProperty().bind(isConfigurationSet);
    }

    public void resetConfigurationSetter() {
        machineConfigurationSetterController.resetConfigurationSetter();
    }

    public void resetTab() {
        machineConfigurationSetterController.resetConfigurationSetterSelection();
    }
}