package components.tabs.encryption.panels.encryption.modes.full.text;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class FullTextEncryption extends VBox {
    @FXML private Button processTextButton;
    @FXML private TextField unprocessedTextField;
    @FXML private TextField processedTextField;

    @FXML
    private void clearTextFields() {
        clearEncryptionTextFields();
    }

    public void setUpFullTextEncryption(Consumer<String> processFullTextConsumer) {
        processTextButton.setOnAction(e -> processFullTextConsumer.accept(unprocessedTextField.getText()));
    }

    public void displayProcessedMessage(String message) {
        processedTextField.textProperty().set(message);
    }

    public void clearEncryptionTextFields() {
        unprocessedTextField.clear();
        processedTextField.clear();
    }

    public void resetFullTextEncryption() {
        clearEncryptionTextFields();
    }
}
