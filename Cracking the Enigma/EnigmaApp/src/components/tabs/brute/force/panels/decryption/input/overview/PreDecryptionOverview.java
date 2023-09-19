package components.tabs.brute.force.panels.decryption.input.overview;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;


public class PreDecryptionOverview {
    @FXML private Label amountOfTotalTasksLabel;
    @FXML private Label originalMessageLabel;
    @FXML private Label messageToDecryptLabel;

    public void updatePreDecryptionData(PreDecryptionData preDecryptionData) {
        amountOfTotalTasksLabel.setText(String.valueOf(preDecryptionData.getAmountOfTotalTasks()));
        originalMessageLabel.setText(String.valueOf(preDecryptionData.getOriginalMessage()));
        messageToDecryptLabel.setText(String.valueOf(preDecryptionData.getMessageToDecrypt()));
    }

    public void resetOverviewPanel() {
        amountOfTotalTasksLabel.setText("");
        originalMessageLabel.setText("");
        messageToDecryptLabel.setText("");
    }
}