package uboat.screens.main.tabs.machine.configuration.setter;

import javafx.application.Platform;
import object.machine.state.MachineState;
import okhttp3.*;
import uboat.screens.main.tabs.machine.configuration.setter.selectors.plugs.PlugsSelector;
import uboat.screens.main.tabs.machine.configuration.setter.selectors.reflector.ReflectorIDSelector;
import uboat.screens.main.tabs.machine.configuration.setter.selectors.rotors.ids.RotorIDsSelector;
import uboat.screens.main.tabs.machine.configuration.setter.selectors.rotors.start.positions.RotorStartPositionsSelector;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static uboat.connection.constants.Constants.*;
import static uboat.connection.settings.ConnectionSettings.*;

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
    private Runnable onConfigurationSet;
    private Consumer<String> onError;

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

    public void setUp(Runnable onConfigurationSet, Consumer<String> onError) {
        this.onConfigurationSet = onConfigurationSet;
        this.onError = onError;

        setConfigurationButton.setOnAction(event -> {
            if (configurationModeToggleSwitch.isSelected()) {
                setRandomConfiguration();
            }
            else {
                setConfiguration();
            }
        });
    }

    public void setRandomConfiguration() {
        sendSetConfigurationRequest(RANDOM, RequestBody.create(new byte[] {}));
    }

    public void setConfiguration() {
        try {
            RequestBody requestBody = RequestBody.create(GSON_INSTANCE.toJson(getConfiguration()).getBytes());

            sendSetConfigurationRequest(MANUAL, requestBody);
        } catch (Exception e) {
            onError.accept(e.getMessage());
        }
    }

    private void sendSetConfigurationRequest(String configurationMode, RequestBody requestBody) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + SET_CONFIGURATION).newBuilder();

        urlBuilder.addQueryParameter(CONFIGURATION_MODE_PARAMETER, configurationMode);
        String finalUrl = urlBuilder.build().toString();

        sendAsyncPostRequest(finalUrl, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    onError.accept(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        onConfigurationSet.run();
                        response.close();
                    } else {
                        try {
                            onError.accept(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
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
        plugsSelectorController.setUp(allKeys);
    }

    public void updateReflectorIDSelector(int reflectorsInStorageCount) {
        reflectorIDSelectorController.setUp(reflectorsInStorageCount);
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
    public void reset() {
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

    public void update(MachineState machineState) {
        updateAllKeys(machineState.getAllKeys(), machineState.getActiveRotorsCount());
        updateRotorsCount(machineState.getActiveRotorsCount(), machineState.getAvailableRotorsCount());
        updateReflectorIDSelector(machineState.getReflectorsInStorageCount());
    }
}