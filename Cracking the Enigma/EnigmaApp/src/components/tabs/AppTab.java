package components.tabs;

import components.configuration.view.MachineConfigurationView;
import javafx.fxml.FXML;

public class AppTab {
    @FXML private MachineConfigurationView currentConfigurationViewController;

    protected void initialize() {
        currentConfigurationViewController.setConfigurationTitle("Current Configuration:");
    }

    public void bindToMainConfigurationView(MachineConfigurationView machineConfigurationView) {
        currentConfigurationViewController.bindToMainConfigurationView(machineConfigurationView);
    }
}
