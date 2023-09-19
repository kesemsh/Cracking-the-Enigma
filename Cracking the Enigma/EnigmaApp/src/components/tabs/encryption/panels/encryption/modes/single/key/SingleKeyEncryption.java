package components.tabs.encryption.panels.encryption.modes.single.key;

import components.tabs.encryption.panels.encryption.modes.single.key.keyboards.KeyboardPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class SingleKeyEncryption extends VBox {
    @FXML private Button doneSingleKeyButton;
    @FXML private TextField unprocessedTextField;
    @FXML private TextField processedTextField;
    @FXML private KeyboardPanel keyboardPanelController;

    @FXML
    private void initialize() {
        doneSingleKeyButton.disableProperty().bind(unprocessedTextField.textProperty().isEmpty());
    }

    private void setUpSingleKeyEncryption(EventHandler<ActionEvent> insertAccumulatedMessageEvent) {
        doneSingleKeyButton.setOnAction(e -> {
            clearEncryptionTextFields();
            keyboardPanelController.resetLitCharacterOnKeyboard();
            insertAccumulatedMessageEvent.handle(e);
        });
    }

    public void resetSingleKeyEncryption() {
        if (!doneSingleKeyButton.isDisabled()) {
            doneSingleKeyButton.getOnAction().handle(null);
        }

        resetSingleKeyEncryptionKeyboards();
    }

    public void setUpEncryptionKeyboardPanel(Consumer<Character> processSingleKeyConsumer, EventHandler<ActionEvent> insertAccumulatedMessageEvent) {
        keyboardPanelController.setUpEncryptionKeyboardPanel(processSingleKeyConsumer);
        setUpSingleKeyEncryption(insertAccumulatedMessageEvent);
        unprocessedTextField.setOnKeyTyped(e -> {
            if (e.getCharacter().length() == 1) {
                processSingleKeyConsumer.accept(e.getCharacter().charAt(0));
            }
        });
    }

    public void displayUnprocessedAndProcessedCharacters(Character unprocessedCharacter, Character processedCharacter) {
        displayProcessedCharacterInTextFields(unprocessedCharacter, processedCharacter);
        displayProcessedCharacterOnLightsCharacterKeyboard(processedCharacter);
    }

    private void displayProcessedCharacterOnLightsCharacterKeyboard(Character processedCharacter) {
        keyboardPanelController.displayProcessedCharacterOnLightsCharacterKeyboard(processedCharacter);
    }

    private void displayProcessedCharacterInTextFields(Character unprocessedCharacter, Character processedCharacter) {
        unprocessedTextField.setText(unprocessedTextField.getText() + unprocessedCharacter);
        processedTextField.setText(processedTextField.getText() + processedCharacter);
    }

    public void updateAllKeys(List<Character> allKeys) {
        keyboardPanelController.updateAllKeys(allKeys);
    }

    private void resetCharacterKeyboards() {
        keyboardPanelController.resetCharacterKeyboards();
    }

    private void createCharacterKeyboards() {
        keyboardPanelController.createCharacterKeyboards();
    }

    public void resetSingleKeyEncryptionKeyboards() {
        resetCharacterKeyboards();
        createCharacterKeyboards();
    }

    public void clearEncryptionTextFields() {
        unprocessedTextField.clear();
        processedTextField.clear();
    }
}
