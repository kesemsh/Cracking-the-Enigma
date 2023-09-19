package components.tabs.encryption.panels.encryption;

import components.tabs.encryption.panels.encryption.modes.full.text.FullTextEncryption;
import components.tabs.encryption.panels.encryption.modes.single.key.SingleKeyEncryption;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import java.util.List;
import java.util.function.Consumer;

public class EncryptionPanel extends VBox{
    @FXML private Button resetToInitialConfigurationButton;
    @FXML private ToggleSwitch encryptionModeToggleSwitch;
    @FXML private VBox fullTextEncryption;
    @FXML private FullTextEncryption fullTextEncryptionController;
    @FXML private VBox singleKeyEncryption;
    @FXML private SingleKeyEncryption singleKeyEncryptionController;

    @FXML
    private void initialize() {
        fullTextEncryption.visibleProperty().bind(encryptionModeToggleSwitch.selectedProperty().not());
        singleKeyEncryption.visibleProperty().bind(encryptionModeToggleSwitch.selectedProperty());
        encryptionModeToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            resetEncryptionPanelModesOnly();
        });
    }

    private void resetToInitialConfiguration(EventHandler<ActionEvent> resetConfigurationEvent) {
        resetToInitialConfigurationButton.setOnAction(e -> {
            resetEncryptionPanelCompletely();
            resetConfigurationEvent.handle(e);
        });
    }

    public void displayProcessedMessage(String message) {
        fullTextEncryptionController.displayProcessedMessage(message);
    }

    public void setUpEncryptionPanel(Consumer<Character> processSingleKeyConsumer, Consumer<String> processFullTextConsumer,
                                     EventHandler<ActionEvent> resetConfigurationEvent, EventHandler<ActionEvent> insertAccumulatedMessageEvent) {
        fullTextEncryptionController.setUpFullTextEncryption(processFullTextConsumer);
        singleKeyEncryptionController.setUpEncryptionKeyboardPanel(processSingleKeyConsumer, insertAccumulatedMessageEvent);
        resetToInitialConfiguration(resetConfigurationEvent);
    }

    public void displayUnprocessedAndProcessedCharacters(Character unprocessedCharacter, Character processedCharacter) {
        singleKeyEncryptionController.displayUnprocessedAndProcessedCharacters(unprocessedCharacter, processedCharacter);
    }

    public void updateAllKeys(List<Character> allKeys) {
        singleKeyEncryptionController.updateAllKeys(allKeys);
    }

    public void resetEncryptionPanelCompletely() {
        encryptionModeToggleSwitch.setSelected(false);
        fullTextEncryptionController.resetFullTextEncryption();
        singleKeyEncryptionController.resetSingleKeyEncryption();
    }

    public void resetEncryptionPanelModesOnly() {
        fullTextEncryptionController.resetFullTextEncryption();
        singleKeyEncryptionController.resetSingleKeyEncryption();
    }
}
