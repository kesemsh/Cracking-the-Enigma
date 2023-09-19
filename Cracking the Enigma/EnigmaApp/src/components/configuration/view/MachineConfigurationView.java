package components.configuration.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import object.machine.configuration.MachineConfiguration;

public class MachineConfigurationView {
    @FXML private Label configurationTitleLabel;
    @FXML private Label configurationNotSetLabel;
    @FXML private Label configurationStringLabel;
    private MachineConfiguration machineConfiguration;
    private final StringProperty configurationTitle;
    private final StringProperty configurationString;
    private final BooleanProperty isConfigurationSet;

    public MachineConfigurationView() {
        configurationTitle = new SimpleStringProperty();
        configurationString = new SimpleStringProperty();
        isConfigurationSet = new SimpleBooleanProperty(false);
        machineConfiguration = null;
    }

    @FXML
    private void initialize() {
        configurationTitleLabel.textProperty().bind(configurationTitle);
        configurationStringLabel.textProperty().bind(configurationString);
        configurationNotSetLabel.visibleProperty().bind(isConfigurationSet.not());
        configurationStringLabel.visibleProperty().bind(configurationNotSetLabel.visibleProperty().not());
    }

    public void setConfigurationTitle(String configurationTitle) {
        this.configurationTitle.set(configurationTitle);
    }

    public void setConfiguration(MachineConfiguration machineConfiguration) {
        if (machineConfiguration != null) {
            configurationString.set(machineConfiguration.toString());
            this.machineConfiguration = machineConfiguration;
        }
    }

    public void bindToMainConfigurationView(MachineConfigurationView machineConfigurationView) {
        configurationTitle.bind(machineConfigurationView.configurationTitle);
        configurationString.bind(machineConfigurationView.configurationString);
        isConfigurationSet.bind(machineConfigurationView.isConfigurationSet);
    }

    public BooleanProperty isConfigurationSetProperty() {
        return isConfigurationSet;
    }
}