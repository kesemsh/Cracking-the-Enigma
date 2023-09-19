package components.tabs.brute.force.panels.decryption.input.encryption;

import components.tabs.brute.force.panels.decryption.input.encryption.dictionary.DictionaryPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

import java.util.Set;

public class BruteForceEncryptionPanel {
    @FXML private Button resetToInitialConfigurationButton;
    @FXML private TextField selectedMessageTextField;
    @FXML private VBox dictionaryPanel;
    @FXML private DictionaryPanel dictionaryPanelController;

    @FXML
    private void clearTextFields() {
        clearEncryptionTextField();
    }

    private void resetToInitialConfiguration(EventHandler<ActionEvent> resetConfigurationEvent) {
        resetToInitialConfigurationButton.setOnAction(e -> {
            clearEncryptionTextField();
            resetConfigurationEvent.handle(e);
        });
    }

    public void setUpBruteForceEncryptionPanel(EventHandler<ActionEvent> resetConfigurationEvent) {
        resetToInitialConfiguration(resetConfigurationEvent);
        dictionaryPanelController.setUpDictionaryPanel(this::displayChosenDictionaryWordInTextField);
    }

    private void displayChosenDictionaryWordInTextField(String dictionaryWord) {
        if (selectedMessageTextField.getText().isEmpty()) {
            selectedMessageTextField.setText(selectedMessageTextField.getText() + dictionaryWord);
        } else {
            selectedMessageTextField.setText(selectedMessageTextField.getText() + " " + dictionaryWord);
        }
    }

    public void clearEncryptionTextField() {
        selectedMessageTextField.clear();
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        dictionaryPanelController.updateAllowedWords(allowedWords);
    }

    public void resetBruteForceEncryptionPanel() {
        dictionaryPanelController.resetDictionaryPanel();
    }

    public String getSelectedMessage() {
        return selectedMessageTextField.getText();
    }
}
