package machine.automatic.decryption.input.data;

import machine.automatic.decryption.difficulty.DecryptionDifficulty;
import machine.automatic.decryption.task.results.DecryptionTaskResults;

import java.util.function.Consumer;

public class DecryptionInputData {
    private final DecryptionDifficulty decryptionDifficulty;
    private final int agentsCount;
    private final int tasksSize;
    private final Consumer<DecryptionTaskResults> onDecryptionTaskResultsReceived;
    private String originalMessage;
    private String messageToDecrypt;

    public DecryptionInputData(DecryptionDifficulty decryptionDifficulty, String originalMessage, int agentsCount, int tasksSize, Consumer<DecryptionTaskResults> onDecryptionTaskResultsReceived) {
        this.decryptionDifficulty = decryptionDifficulty;
        this.originalMessage = originalMessage;
        this.agentsCount = agentsCount;
        this.tasksSize = tasksSize;
        this.onDecryptionTaskResultsReceived = onDecryptionTaskResultsReceived;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public void setMessageToDecrypt(String messageToDecrypt) {
        this.messageToDecrypt = messageToDecrypt;
    }

    public DecryptionDifficulty getDecryptionDifficulty() {
        return decryptionDifficulty;
    }

    public String getMessageToDecrypt() {
        return messageToDecrypt;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public int getAgentsCount() {
        return agentsCount;
    }

    public int getTasksSize() {
        return tasksSize;
    }

    public Consumer<DecryptionTaskResults> getOnDecryptionTaskResultsReceived() {
        return onDecryptionTaskResultsReceived;
    }
}
