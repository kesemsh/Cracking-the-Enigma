package components.tabs.encryption;

import components.tabs.AppTab;
import components.tabs.encryption.panels.encryption.EncryptionPanel;
import components.tabs.encryption.panels.history.HistoryPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import object.machine.history.MachineHistoryPerConfiguration;
import java.util.List;
import java.util.function.Consumer;

public class EncryptionTab extends AppTab {
    @FXML private HistoryPanel historyPanelController;
    @FXML private EncryptionPanel encryptionPanelController;

    @FXML
    @Override
    protected void initialize() { super.initialize(); }

    public void displayUnprocessedAndProcessedCharacters(Character unprocessedCharacter, Character processedCharacter) {
        encryptionPanelController.displayUnprocessedAndProcessedCharacters(unprocessedCharacter, processedCharacter);
    }

    public void setUpEncryptionTab(Consumer<Character> processSingleKeyConsumer, Consumer<String> processFullTextConsumer,
                                   EventHandler<ActionEvent> resetConfigurationEvent, EventHandler<ActionEvent> insertAccumulatedMessageEvent) {
        encryptionPanelController.setUpEncryptionPanel(processSingleKeyConsumer, processFullTextConsumer, resetConfigurationEvent, insertAccumulatedMessageEvent);
    }

    public void updateAllKeys(List<Character> allKeys) {
        encryptionPanelController.updateAllKeys(allKeys);
    }

    public void displayProcessedMessage(String message) {
        encryptionPanelController.displayProcessedMessage(message);
    }

    public void updateMachineHistory(List<MachineHistoryPerConfiguration> machineHistoryList) {
        historyPanelController.updateMachineHistory(machineHistoryList);
    }

    public void resetTabCompletely() {
        encryptionPanelController.resetEncryptionPanelCompletely();
        historyPanelController.resetHistoryPanel();
    }

    public void resetOnlyEncryptionPanel() {
        encryptionPanelController.resetEncryptionPanelCompletely();
    }
}