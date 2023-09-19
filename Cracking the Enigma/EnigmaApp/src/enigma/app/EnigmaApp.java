package enigma.app;

import components.file.manager.FileManager;
import exceptions.input.InvalidWordException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;
import machine.engine.MachineEngine;
import machine.engine.MachineEngineImpl;
import components.tabs.manager.TabsManager;
import object.machine.configuration.MachineConfiguration;
import object.machine.state.MachineState;

import java.nio.file.Paths;

public class EnigmaApp {
    private Stage primaryStage;
    private final MachineEngine machineEngine;
    private final IntegerProperty machinesLoadedCounter;
    private final IntegerProperty configurationsSetCounter;
    private final IntegerProperty actionsDoneCounter;
    private final BooleanProperty isMachineLoaded;
    private final BooleanProperty isConfigurationSet;
    @FXML private FileManager fileManagerController;
    @FXML private VBox tabsManager;
    @FXML private TabsManager tabsManagerController;

    public EnigmaApp() {
        machineEngine = new MachineEngineImpl();
        machinesLoadedCounter = new SimpleIntegerProperty(0);
        configurationsSetCounter = new SimpleIntegerProperty(0);
        actionsDoneCounter = new SimpleIntegerProperty(0);
        isMachineLoaded = new SimpleBooleanProperty(false);
        isConfigurationSet = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        isMachineLoaded.bind(Bindings.equal(machinesLoadedCounter, 0).not());
        isConfigurationSet.bind(Bindings.equal(configurationsSetCounter, 0).not());
        fileManagerController.setUpFileManager(this::loadMachineFromXMLFile, this::loadMachineFromMAGICFile, this::saveMachineToMAGICFile, isMachineLoaded);
        tabsManager.disableProperty().bind(isMachineLoaded.not());
        tabsManagerController.setUpTabsDisableProperties(isMachineLoaded, isConfigurationSet);
        tabsManagerController.setUpConfigurationSetter(this::setConfiguration, e -> setRandomConfiguration(), this::displayErrorMessage);
        tabsManagerController.setUpConfigurationViews(isConfigurationSet);
        tabsManagerController.setUpEncryptionTab(this::processSingleKeyInput, this::processFullTextInput, e -> resetConfiguration(), e -> insertAccumulatedMessageToHistory());
        tabsManagerController.setUpBruteForceTab(this::getPreDecryptionData, e -> resetConfiguration(), this::displayErrorMessage);
        machinesLoadedCounter.addListener((observable, oldValue, newValue) -> handleNewMachineLoad());
        configurationsSetCounter.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != 0) {
                handleNewConfigurationSet();
            }
        });
        actionsDoneCounter.addListener((observable, oldValue, newValue) -> handleActionDone());
    }

    private void handleNewMachineLoad() {
        updateTabs();
        tabsManagerController.goToMainTab();
        if (machineEngine.isConfigurationSet()) {
            addOneToCounter(configurationsSetCounter);
        }
        else {
            configurationsSetCounter.set(0);
        }
    }

    private void handleNewConfigurationSet() {
        resetConfigurationSetter();
        updateConfigurationViews();
        updateMachineHistory();
    }

    private void handleActionDone() {
        updateConfigurationViews();
        updateMachineHistory();
        updateMachineDetails();
    }

    private void updateMachineHistory() {
        try {
            tabsManagerController.updateMachineHistory(machineEngine.getHistoryAndStatistics());
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    public void updateTabs() {
        updateMachineDetails();
        updateConfigurationSetter();
        tabsManagerController.resetTabsCompletely();
    }

    private void updateConfigurationViews() {
        try {
            tabsManagerController.updateConfigurationViews(machineEngine.getMachineState());
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void addOneToCounter(IntegerProperty counterToIncrease) {
        counterToIncrease.set(counterToIncrease.get() + 1);
    }

    private void processFullTextInput(String messageToProcess) {
        try {
            tabsManagerController.displayProcessedMessage(machineEngine.processInput(messageToProcess, true, false));
            addOneToCounter(actionsDoneCounter);
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void processSingleKeyInput(Character characterToProcess) {
        try {
            tabsManagerController.displayUnprocessedAndProcessedCharacters(characterToProcess, machineEngine.processInput(String.valueOf(characterToProcess), false, true).charAt(0));
            addOneToCounter(actionsDoneCounter);
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void insertAccumulatedMessageToHistory() {
        machineEngine.insertAccumulatedMessageToHistory();
        addOneToCounter(actionsDoneCounter);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        fileManagerController.setPrimaryStage(primaryStage);
    }

    private void updateMachineDetails() {
        try {
            tabsManagerController.updateMachineDetails(machineEngine.getMachineState());
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void updateConfigurationSetter() {
        try {
            MachineState machineState = machineEngine.getMachineState();
            tabsManagerController.updateAllKeys(machineEngine.getAllKeys(), machineState.getActiveRotorsCount());
            tabsManagerController.updateAllowedWords(machineEngine.getDictionary().getAllowedWords());
            tabsManagerController.updateRotorsCount(machineState.getActiveRotorsCount(), machineState.getAvailableRotorsCount());
            tabsManagerController.updateReflectorIDSelector(machineState.getReflectorsInStorageCount());
            tabsManagerController.updateAgentsCount(machineEngine.getAgentsCount());
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void resetConfigurationSetter() {
        tabsManagerController.resetConfigurationSetter();
    }

    private void loadMachineFromXMLFile(String filePath) {
        try {
            machineEngine.loadMachineFromXMLFile(filePath);
            fileManagerController.showLoadedFilePath();
            addOneToCounter(machinesLoadedCounter);
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void loadMachineFromMAGICFile(String filePath) {
        try {
            machineEngine.loadMachineFromMAGICFile(filePath);
            fileManagerController.showLoadedFilePath();
            addOneToCounter(machinesLoadedCounter);
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void saveMachineToMAGICFile(String filePath) {
        try {
            machineEngine.saveMachineToMAGICFile(filePath);
            String fileName = Paths.get(filePath).getFileName().toString();

            displaySuccessMessage(String.format("Machine successfully saved to file: \"%s\"!", fileName));
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void displayErrorMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("An error has occurred!");
        showAlertWithDefaultAlertSettings(alert, errorMessage);
    }

    private void displaySuccessMessage(String successMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Success");
        alert.setHeaderText("Success!");
        showAlertWithDefaultAlertSettings(alert, successMessage);
    }

    private void showAlertWithDefaultAlertSettings(Alert alertToShow, String messageToDisplay) {
        Label messageToDisplayLabel = new Label(messageToDisplay);

        alertToShow.initOwner(primaryStage);
        alertToShow.initStyle(StageStyle.DECORATED);
        messageToDisplayLabel.setWrapText(true);
        alertToShow.getDialogPane().setContent(messageToDisplayLabel);
        alertToShow.showAndWait();
    }

    public void setConfiguration(MachineConfiguration machineConfiguration) {
        try {
            machineEngine.setConfiguration(machineConfiguration);
            addOneToCounter(configurationsSetCounter);
            addOneToCounter(actionsDoneCounter);
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    public void setRandomConfiguration() {
        try {
            machineEngine.setRandomConfiguration();
            addOneToCounter(configurationsSetCounter);
            addOneToCounter(actionsDoneCounter);
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    public void resetConfiguration() {
        try {
            machineEngine.resetConfiguration();
            addOneToCounter(configurationsSetCounter);
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    public void getPreDecryptionData(DecryptionInputData decryptionInputData) {
        try {
            PreDecryptionData preDecryptionData = machineEngine.getPreDecryptionData(decryptionInputData);
            tabsManagerController.updatePreDecryptionData(preDecryptionData, machineEngine);
            addOneToCounter(actionsDoneCounter);
        } catch (InvalidWordException e) {
            displayErrorMessage(e.getMessage());
        }
    }
}