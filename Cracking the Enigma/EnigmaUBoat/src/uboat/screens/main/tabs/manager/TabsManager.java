package uboat.screens.main.tabs.manager;

import object.automatic.decryption.winner.ContestWinner;
import uboat.screens.main.tabs.configuration.view.MachineConfigurationView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
/*import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;
import machine.engine.MachineEngine;*/
import object.machine.state.MachineState;
import uboat.screens.main.tabs.contest.ContestTab;
import uboat.screens.main.tabs.machine.MachineTab;

import java.util.List;
import java.util.function.Consumer;

public class TabsManager {
    @FXML public Button machineTabButton;
    @FXML public Button contestTabButton;
    @FXML private ScrollPane machineTab;
    @FXML private MachineTab machineTabController;
    @FXML private ScrollPane contestTab;
    @FXML private ContestTab contestTabController;
    private final ObjectProperty<Node> selectedPanel;
    private final MachineConfigurationView mainCurrentConfigurationViewController;
    private BooleanProperty machineLoaded;

    public TabsManager() {
        selectedPanel = new SimpleObjectProperty<>(null);
        mainCurrentConfigurationViewController = new MachineConfigurationView();
        mainCurrentConfigurationViewController.setConfigurationTitle("Current Configuration:");
    }

    @FXML
    private void initialize() {
        machineTab.visibleProperty().bind(Bindings.equal(selectedPanel, machineTab));
        contestTab.visibleProperty().bind(Bindings.equal(selectedPanel, contestTab));
        selectedPanel.set(machineTab);
        selectedPanel.addListener((observable, oldValue, newValue) -> resetTabsButNotHistory());
        machineTabController.bindToMainConfigurationView(mainCurrentConfigurationViewController);
        contestTabController.bindToMainConfigurationView(mainCurrentConfigurationViewController);
    }

    public void setUpContestTab(Runnable onConfigurationSet, Runnable onActionDone, Consumer<ContestWinner> onGameFinished, Runnable onReady, Consumer<String> onError, BooleanProperty ready) {
        machineTabButton.disableProperty().bind(machineLoaded.not().or(ready));
        contestTabController.setUp(onConfigurationSet, onActionDone, onGameFinished, onReady, onError, ready, machineLoaded);
    }

    /*public void updateMachineDetails() {
        machineTabController.updateMachineDetails();
    }*/

    public void setUpConfigurationSetter(Runnable onConfigurationSet, Consumer<String> onError) {
        machineTabController.setUpConfigurationSetter(onConfigurationSet, onError);
    }

    /*public void updateConfigurationSetter() {
        machineTabController.updateConfigurationSetter();
    }*/

    public void updateRotorsCount(int activeRotorsCount, int totalRotorsCount) {
        machineTabController.setRotorsCount(activeRotorsCount, totalRotorsCount);
    }

    public void updateAllKeys(List<Character> allKeys, int rotorsCount) {
        machineTabController.updateAllKeys(allKeys, rotorsCount);
        //encryptionTabController.updateAllKeys(allKeys);
    }

    public void updateReflectorIDSelector(int reflectorsInStorageCount) {
        machineTabController.setUpReflectorIDSelector(reflectorsInStorageCount);
    }

    @FXML
    private void selectMachineTab(ActionEvent actionEvent) {
        selectedPanel.set(machineTab);
    }

    @FXML
    private void selectContestTab(ActionEvent actionEvent) {
        selectedPanel.set(contestTab);
    }

    /*public void setUpEncryptionTab(Consumer<Character> processSingleKeyConsumer, Consumer<String> processFullTextConsumer,
                                   EventHandler<ActionEvent> resetConfigurationEvent, EventHandler<ActionEvent> insertAccumulatedMessageEvent) {
        encryptionTabController.setUpEncryptionTab(processSingleKeyConsumer, processFullTextConsumer, resetConfigurationEvent, insertAccumulatedMessageEvent);
    }

    public void displayUnprocessedAndProcessedCharacters(Character unprocessedCharacter, Character processedCharacter) {
        encryptionTabController.displayUnprocessedAndProcessedCharacters(unprocessedCharacter, processedCharacter);
    }*/

    public void setUpTabsDisableProperties(BooleanProperty machineLoaded, BooleanProperty isConfigurationSet) {
        this.machineLoaded = machineLoaded;
        contestTabButton.disableProperty().bind(isConfigurationSet.not());
    }

    public void setUpConfigurationViews(BooleanProperty isConfigurationSet) {
        mainCurrentConfigurationViewController.isConfigurationSetProperty().bind(isConfigurationSet);
        machineTabController.setUpInitialConfigurationView(isConfigurationSet);
    }

    public void updateConfigurationViews(MachineState machineState) {
        machineTabController.updateInitialConfigurationView(machineState.getInitialConfiguration());
        mainCurrentConfigurationViewController.setConfiguration(machineState.getCurrentConfiguration());

        /*HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + MACHINE_DETAILS).newBuilder();

        String finalUrl = urlBuilder.build().toString();

        sendAsyncGetRequest(finalUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> onError.accept(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();

                    Platform.runLater(() -> {
                        MachineState machineState = GSON_INSTANCE.fromJson(body, MachineState.class);


                    });
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });*/
    }

    /*public void displayProcessedMessage(String message) {
        encryptionTabController.displayProcessedMessage(message);
    }*/

    public void goToMainTab() {
        selectMachineTab(null);
    }

    public void resetConfigurationSetter() {
        machineTabController.resetConfigurationSetter();
    }

    /*public void updateMachineHistory(List<MachineHistoryPerConfiguration> machineHistoryList) {
        encryptionTabController.updateMachineHistory(machineHistoryList);
    }*/

    public void resetTabsCompletely() {
        machineTabController.reset();
        contestTabController.reset();
        /*encryptionTabController.resetTabCompletely();
        bruteForceTabController.resetTab();*/
    }

    public void resetTabsButNotHistory() {
        machineTabController.reset();
        //encryptionTabController.resetOnlyEncryptionPanel();
    }

    public void updateMachineDetails(MachineState machineState) {
        machineTabController.updateMachineDetails(machineState);
        contestTabController.updateAllowedWords(machineState.getAllowedWords());
    }

    public void logout() {
        contestTabController.logout();
    }

    public void setUpTeamsDetailsView() {
        contestTabController.setUpTeamsDetailsView();
    }

    /*public void updatePreDecryptionData(PreDecryptionData preDecryptionData, MachineEngine machineEngine) {
        bruteForceTabController.updatePreDecryptionData(preDecryptionData, machineEngine);
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        bruteForceTabController.updateAllowedWords(allowedWords);
    }

    public void updateAgentsCount(int agentsCount) {
        bruteForceTabController.updateAgentsCount(agentsCount);
    }*/
}