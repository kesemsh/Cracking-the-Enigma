package components.tabs.manager;

import components.configuration.view.MachineConfigurationView;
import components.tabs.brute.force.BruteForceTab;
import components.tabs.encryption.EncryptionTab;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;
import machine.engine.MachineEngine;
import object.machine.configuration.MachineConfiguration;
import object.machine.history.MachineHistoryPerConfiguration;
import object.machine.state.MachineState;
import components.tabs.machine.MachineTab;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TabsManager {
    @FXML public Button machineTabButton;
    @FXML public Button encryptionTabButton;
    @FXML public Button bruteForceTabButton;
    @FXML private ScrollPane machineTab;
    @FXML private MachineTab machineTabController;
    @FXML private ScrollPane encryptionTab;
    @FXML private EncryptionTab encryptionTabController;
    @FXML private ScrollPane bruteForceTab;
    @FXML private BruteForceTab bruteForceTabController;
    private final ObjectProperty<Node> selectedPanel;
    private final MachineConfigurationView mainCurrentConfigurationViewController;

    public TabsManager() {
        selectedPanel = new SimpleObjectProperty<>(null);
        mainCurrentConfigurationViewController = new MachineConfigurationView();
        mainCurrentConfigurationViewController.setConfigurationTitle("Current Configuration:");
    }

    @FXML
    private void initialize() {
        machineTab.visibleProperty().bind(Bindings.equal(selectedPanel, machineTab));
        encryptionTab.visibleProperty().bind(Bindings.equal(selectedPanel, encryptionTab));
        bruteForceTab.visibleProperty().bind(Bindings.equal(selectedPanel, bruteForceTab));
        selectedPanel.set(machineTab);
        selectedPanel.addListener((observable, oldValue, newValue) -> resetTabsButNotHistory());
        machineTabController.bindToMainConfigurationView(mainCurrentConfigurationViewController);
        encryptionTabController.bindToMainConfigurationView(mainCurrentConfigurationViewController);
        bruteForceTabController.bindToMainConfigurationView(mainCurrentConfigurationViewController);
    }

    public void updateMachineDetails(MachineState machineState) {
        machineTabController.updateMachineDetails(machineState);
    }

    public void setUpConfigurationSetter(Consumer<MachineConfiguration> machineConfigurationConsumer, EventHandler<ActionEvent> randomConfigurationEvent, Consumer<String> errorMessageConsumer) {
        machineTabController.setUpConfigurationSetter(machineConfigurationConsumer, randomConfigurationEvent, errorMessageConsumer);
    }

    public void updateRotorsCount(int activeRotorsCount, int totalRotorsCount) {
        machineTabController.setRotorsCount(activeRotorsCount, totalRotorsCount);
    }

    public void updateAllKeys(List<Character> allKeys, int rotorsCount) {
        machineTabController.updateAllKeys(allKeys, rotorsCount);
        encryptionTabController.updateAllKeys(allKeys);
    }

    public void updateReflectorIDSelector(int reflectorsInStorageCount) {
        machineTabController.setUpReflectorIDSelector(reflectorsInStorageCount);
    }

    @FXML
    private void selectMachineTab(ActionEvent actionEvent) {
        selectedPanel.set(machineTab);
    }

    @FXML
    private void selectEncryptionTab(ActionEvent actionEvent) {
        selectedPanel.set(encryptionTab);
    }

    @FXML
    private void selectBruteForceTab(ActionEvent actionEvent) {
        selectedPanel.set(bruteForceTab);
    }

    public void setUpEncryptionTab(Consumer<Character> processSingleKeyConsumer, Consumer<String> processFullTextConsumer,
                                   EventHandler<ActionEvent> resetConfigurationEvent, EventHandler<ActionEvent> insertAccumulatedMessageEvent) {
        encryptionTabController.setUpEncryptionTab(processSingleKeyConsumer, processFullTextConsumer, resetConfigurationEvent, insertAccumulatedMessageEvent);
    }

    public void setUpBruteForceTab(Consumer<DecryptionInputData> decryptionInputDataConsumer, EventHandler<ActionEvent> resetConfigurationEvent, Consumer<String> onErrorReceived) {
        bruteForceTabController.setUpBruteForceTab(decryptionInputDataConsumer, resetConfigurationEvent, onErrorReceived);
    }

    public void displayUnprocessedAndProcessedCharacters(Character unprocessedCharacter, Character processedCharacter) {
        encryptionTabController.displayUnprocessedAndProcessedCharacters(unprocessedCharacter, processedCharacter);
    }

    public void setUpTabsDisableProperties(BooleanProperty isMachineLoaded, BooleanProperty isConfigurationSet) {
        machineTabButton.disableProperty().bind(isMachineLoaded.not());
        encryptionTabButton.disableProperty().bind(isConfigurationSet.not());
        bruteForceTabButton.disableProperty().bind(isConfigurationSet.not());
    }

    public void setUpConfigurationViews(BooleanProperty isConfigurationSet) {
        mainCurrentConfigurationViewController.isConfigurationSetProperty().bind(isConfigurationSet);
        machineTabController.setUpInitialConfigurationView(isConfigurationSet);
    }

    public void updateConfigurationViews(MachineState machineState) {
        machineTabController.updateInitialConfigurationView(machineState.getInitialConfiguration());
        mainCurrentConfigurationViewController.setConfiguration(machineState.getCurrentConfiguration());
    }

    public void displayProcessedMessage(String message) {
        encryptionTabController.displayProcessedMessage(message);
    }

    public void goToMainTab() {
        selectMachineTab(null);
    }

    public void resetConfigurationSetter() {
        machineTabController.resetConfigurationSetter();
    }

    public void updateMachineHistory(List<MachineHistoryPerConfiguration> machineHistoryList) {
        encryptionTabController.updateMachineHistory(machineHistoryList);
    }

    public void resetTabsCompletely() {
        machineTabController.resetTab();
        encryptionTabController.resetTabCompletely();
        bruteForceTabController.resetTab();
    }

    public void resetTabsButNotHistory() {
        machineTabController.resetTab();
        encryptionTabController.resetOnlyEncryptionPanel();
    }

    public void updatePreDecryptionData(PreDecryptionData preDecryptionData, MachineEngine machineEngine) {
        bruteForceTabController.updatePreDecryptionData(preDecryptionData, machineEngine);
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        bruteForceTabController.updateAllowedWords(allowedWords);
    }

    public void updateAgentsCount(int agentsCount) {
        bruteForceTabController.updateAgentsCount(agentsCount);
    }
}