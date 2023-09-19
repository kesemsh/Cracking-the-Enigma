package machine.automatic.decryption.pre.decryption.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.collections.ObservableList;
import machine.automatic.decryption.decrypted.message.candidate.DecryptedMessageCandidate;

public class PreDecryptionData {
    private final int amountOfTotalTasks;
    private final String originalMessage;
    private final String messageToDecrypt;

    public PreDecryptionData(int amountOfTotalTasks, String originalMessage, String messageToDecrypt) {
        this.amountOfTotalTasks = amountOfTotalTasks;
        this.originalMessage = originalMessage;
        this.messageToDecrypt = messageToDecrypt;
    }

    public int getAmountOfTotalTasks() {
        return amountOfTotalTasks;
    }

    public String getMessageToDecrypt() {
        return messageToDecrypt;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }
}
