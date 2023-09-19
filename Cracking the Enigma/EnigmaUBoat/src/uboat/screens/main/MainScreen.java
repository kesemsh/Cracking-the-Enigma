package uboat.screens.main;

import object.automatic.decryption.winner.ContestWinner;
import object.machine.state.MachineState;
import okhttp3.*;
import uboat.screens.main.file.manager.FileManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uboat.screens.main.tabs.manager.TabsManager;

import java.util.function.Consumer;

import static uboat.connection.constants.Constants.*;
import static uboat.connection.settings.ConnectionSettings.*;

public class MainScreen {
    private final IntegerProperty machinesLoadedCounter;
    private final IntegerProperty configurationsSetCounter;
    private final IntegerProperty actionsDoneCounter;
    private final BooleanProperty machineLoaded;
    private final BooleanProperty configurationSet;
    private Stage primaryStage;
    private Consumer<String> onSuccess;
    private Consumer<String> onError;
    @FXML private FileManager fileManagerController;
    @FXML private VBox tabsManager;
    @FXML private TabsManager tabsManagerController;

    public MainScreen() {
        machinesLoadedCounter = new SimpleIntegerProperty(0);
        configurationsSetCounter = new SimpleIntegerProperty(0);
        actionsDoneCounter = new SimpleIntegerProperty(0);
        machineLoaded = new SimpleBooleanProperty(false);
        configurationSet = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        machineLoaded.bind(Bindings.equal(machinesLoadedCounter, 0).not());
        configurationSet.bind(Bindings.equal(configurationsSetCounter, 0).not());
        tabsManager.disableProperty().bind(machineLoaded.not());
        //tabsManagerController.setUpEncryptionTab(this::processSingleKeyInput, this::processFullTextInput, e -> resetConfiguration(), e -> insertAccumulatedMessageToHistory());
        machinesLoadedCounter.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != 0) {
                handleNewMachineLoad();
            }
        });
        configurationsSetCounter.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != 0) {
                handleNewConfigurationSet();
            }
        });
        actionsDoneCounter.addListener((observable, oldValue, newValue) -> handleActionDone());
    }

    private void onSuccessfulMachineLoad() {
        addOneToCounter(machinesLoadedCounter);
    }

    private void handleNewMachineLoad() {
        try {
            MachineState machineState = getMachineState();

            updateMachineDetails(machineState);
            tabsManagerController.resetTabsCompletely();
            tabsManagerController.goToMainTab();
            tabsManagerController.setUpTeamsDetailsView();
            if (machineState.getInitialConfiguration() != null) {
                addOneToCounter(configurationsSetCounter);
            } else {
                configurationsSetCounter.set(0);
            }
        } catch (Exception e) {
            onError.accept(e.getMessage());
        }
    }

    private void handleNewConfigurationSet() {
        try {
            MachineState machineState = getMachineState();

            resetConfigurationSetter();
            updateConfigurationViews(machineState);
            //updateMachineHistory();
        } catch (Exception e) {
            onError.accept(e.getMessage());
        }
    }

    private void handleActionDone() {
        try {
            MachineState machineState = getMachineState();

            updateConfigurationViews(machineState);
            //updateMachineHistory();
            updateMachineDetails(machineState);
            //tabsManagerController.resetTabsCompletely();
        } catch (Exception e) {
            onError.accept(e.getMessage());
        }
    }

    private MachineState getMachineState() throws Exception {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + MACHINE_DETAILS).newBuilder();
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();

            if (response.isSuccessful()) {
                return GSON_INSTANCE.fromJson(responseBodyString, MachineState.class);
            } else {
                throw new Exception(responseBodyString);
            }
        }
    }

    /*private void updateMachineHistory() {
        try {
            tabsManagerController.updateMachineHistory(machineEngine.getHistoryAndStatistics());
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }*/

    /*public void updateTabs() {
        tabsManagerController.updateMachineDetails();
        tabsManagerController.resetTabsCompletely();
    }*/

    private void updateConfigurationViews(MachineState machineState) {
        tabsManagerController.updateConfigurationViews(machineState);
    }

    private void addOneToCounter(IntegerProperty counterToIncrease) {
        counterToIncrease.set(counterToIncrease.get() + 1);
    }

    /*private void processFullTextInput(String messageToProcess) {
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
    }*/

    /*private void insertAccumulatedMessageToHistory() {
        machineEngine.insertAccumulatedMessageToHistory();
        addOneToCounter(actionsDoneCounter);
    }*/

    private void updateMachineDetails(MachineState machineState) {
        tabsManagerController.updateMachineDetails(machineState);
    }

    /*private void updateConfigurationSetter() {
        tabsManagerController.updateConfigurationSetter();
    }*/

    private void resetConfigurationSetter() {
        tabsManagerController.resetConfigurationSetter();
    }

    public void setPrimaryStage(Stage primaryStage) {
        fileManagerController.setPrimaryStage(primaryStage);
    }

    public void setUp(Consumer<String> onSuccess, Consumer<String> onError, Consumer<ContestWinner> onGameFinished, Runnable onReady, BooleanProperty ready) {
        this.onSuccess = onSuccess;
        this.onError = onError;
        fileManagerController.setUpFileManager(onError, this::onSuccessfulMachineLoad);
        tabsManagerController.setUpTabsDisableProperties(machineLoaded, configurationSet);
        tabsManagerController.setUpContestTab(() -> addOneToCounter(configurationsSetCounter), () -> addOneToCounter(actionsDoneCounter), onGameFinished, onReady, onError, ready);
        tabsManagerController.setUpConfigurationSetter(() -> addOneToCounter(configurationsSetCounter), onError);
        tabsManagerController.setUpConfigurationViews(configurationSet);
    }

    public void reset() {
        fileManagerController.reset();
        machinesLoadedCounter.set(0);
        configurationsSetCounter.set(0);
        tabsManagerController.resetTabsCompletely();
        tabsManagerController.goToMainTab();
    }

    public void logout() {
        tabsManagerController.logout();
    }

    /*public void getPreDecryptionData(DecryptionInputData decryptionInputData) {
        try {
            PreDecryptionData preDecryptionData = machineEngine.getPreDecryptionData(decryptionInputData);
            tabsManagerController.updatePreDecryptionData(preDecryptionData, machineEngine);
            addOneToCounter(actionsDoneCounter);
        } catch (InvalidWordException e) {
            displayErrorMessage(e.getMessage());
        }
    }*/
}