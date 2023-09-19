package uboat.screens.main.tabs.contest.overview;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import object.automatic.decryption.data.pre.decryption.PreDecryptionData;


public class PreDecryptionOverview {
    @FXML private Label originalMessageLabel;
    @FXML private Label originalMessageWithoutExcludedCharsLabel;
    @FXML private Label messageToDecryptLabel;

    public void update(PreDecryptionData preDecryptionData) {
        originalMessageLabel.setText(String.valueOf(preDecryptionData.getOriginalMessage()));
        originalMessageWithoutExcludedCharsLabel.setText(String.valueOf(preDecryptionData.getOriginalMessageWithoutExcludedChars()));
        messageToDecryptLabel.setText(String.valueOf(preDecryptionData.getMessageToDecrypt()));
    }

    public void reset() {
        originalMessageLabel.setText("");
        originalMessageWithoutExcludedCharsLabel.setText("");
        messageToDecryptLabel.setText("");
    }
}