package components.tabs.machine.configuration.setter;

import components.tabs.machine.configuration.setter.selectors.plugs.PlugsSelector;
import components.tabs.machine.configuration.setter.selectors.reflector.ReflectorIDSelector;
import components.tabs.machine.configuration.setter.selectors.rotors.ids.RotorIDsSelector;
import components.tabs.machine.configuration.setter.selectors.rotors.start.positions.RotorStartPositionsSelector;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import object.machine.configuration.MachineConfiguration;
import object.numbering.RomanNumber;
import org.controlsfx.control.ToggleSwitch;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MachineConfigurationSetter {
    @FXML private Button resetRotorIDsSelectorButton;
    @FXML private Button resetRotorStartPositionsSelectorButton;
    @FXML private Button resetReflectorIDSelectorButton;
    @FXML private Button resetPlugsSelectorButton;
    @FXML private VBox rotorIDsSelector;
    @FXML private RotorIDsSelector rotorIDsSelectorController;
    @FXML private VBox rotorStartPositionsSelector;
    @FXML private RotorStartPositionsSelector rotorStartPositionsSelectorController;
    @FXML private VBox reflectorIDSelector;
    @FXML private ReflectorIDSelector reflectorIDSelectorController;
    @FXML private VBox plugsSelector;
    @FXML private PlugsSelector plugsSelectorController;
    @FXML private HBox configurationPanel;
    @FXML private ToggleSwitch configurationModeToggleSwitch;
    @FXML private Button setConfigurationButton;
    private final ObjectProperty<Node> selectedPanel;

    public MachineConfigurationSetter() {
        selectedPanel = new SimpleObjectProperty<>(null);
    }

    @FXML
    private void initialize() {
        configurationPanel.disableProperty().bind(configurationModeToggleSwitch.selectedProperty());
        rotorIDsSelector.visibleProperty().bind(Bindings.equal(selectedPanel, rotorIDsSelector));
        rotorStartPositionsSelector.visibleProperty().bind(Bindings.equal(selectedPanel, rotorStartPositionsSelector));
        reflectorIDSelector.visibleProperty().bind(Bindings.equal(selectedPanel, reflectorIDSelector));
        plugsSelector.visibleProperty().bind(Bindings.equal(selectedPanel, plugsSelector));
        resetRotorIDsSelectorButton.disableProperty().bind(rotorIDsSelectorController.isSelectorResettableProperty().not());
        resetRotorStartPositionsSelectorButton.disableProperty().bind(rotorStartPositionsSelectorController.isSelectorResettableProperty().not());
        resetReflectorIDSelectorButton.disableProperty().bind(reflectorIDSelectorController.isSelectorResettableProperty().not());
        resetPlugsSelectorButton.disableProperty().bind(plugsSelectorController.isSelectorResettableProperty().not());
        selectRotorIDs();
    }

    public void setUpConfigurationSetter(Consumer<MachineConfiguration> machineConfigurationConsumer, EventHandler<ActionEvent> randomConfigurationEvent, Consumer<String> errorMessageConsumer) {
        setConfigurationButton.setOnAction(event -> {
            if (configurationModeToggleSwitch.isSelected()) {
                randomConfigurationEvent.handle(event);
            }
            else {
                try {
                    machineConfigurationConsumer.accept(getConfiguration());
                } catch (Exception e) {
                    errorMessageConsumer.accept(e.getMessage());
                }
            }
        });
    }

    public MachineConfiguration getConfiguration() {
        List<Integer> selectedRotorIDs = rotorIDsSelectorController.getSelectedRotorIDs();
        List<Character> selectedRotorStartPositions = rotorStartPositionsSelectorController.getSelectedRotorStartPositions();
        RomanNumber selectedReflectorID = reflectorIDSelectorController.getSelectedReflectorID();
        Map<Character, Character> selectedPlugs = plugsSelectorController.getSelectedPlugs();

        return new MachineConfiguration(selectedRotorIDs, selectedRotorStartPositions, selectedReflectorID, selectedPlugs);
    }

    public void updateRotorsCount(int activeRotorsCount, int totalRotorsCount) {
        rotorIDsSelectorController.updateRotorsCount(activeRotorsCount, totalRotorsCount);
    }

    public void updateAllKeys(List<Character> allKeys, int rotorsCount) {
        rotorStartPositionsSelectorController.updateAllKeys(allKeys, rotorsCount);
        plugsSelectorController.setUpPlugsSelector(allKeys);
    }

    public void setUpReflectorIDSelector(int reflectorsInStorageCount) {
        reflectorIDSelectorController.setUpReflectorIDSelector(reflectorsInStorageCount);
    }

    @FXML
    private void selectRotorIDs() {
        selectedPanel.set(rotorIDsSelector);
    }

    @FXML
    private void selectRotorStartPositions() {
        selectedPanel.set(rotorStartPositionsSelector);
    }

    @FXML
    public void selectReflectorID(ActionEvent actionEvent) {
        selectedPanel.set(reflectorIDSelector);
    }

    @FXML
    public void selectPlugs(ActionEvent actionEvent) {
        selectedPanel.set(plugsSelector);
    }

    @FXML
    public void resetConfigurationSetter() {
        resetRotorIDsSelector();
        resetRotorStartPositionsSelector();
        resetReflectorIDSelector();
        resetPlugsSelector();
    }

    @FXML
    public void resetRotorIDsSelector() {
        rotorIDsSelectorController.resetSelector();
    }

    @FXML
    public void resetRotorStartPositionsSelector() {
        rotorStartPositionsSelectorController.resetSelector();
    }

    @FXML
    public void resetReflectorIDSelector() {
        reflectorIDSelectorController.resetSelector();
    }

    @FXML
    public void resetPlugsSelector() {
        plugsSelectorController.resetSelector();
    }

    public void resetConfigurationSetterSelection() {
        configurationModeToggleSwitch.setSelected(false);
        selectRotorIDs();
    }
}