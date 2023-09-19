package uboat.screens.main.tabs;

import uboat.screens.main.tabs.configuration.view.MachineConfigurationView;
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
