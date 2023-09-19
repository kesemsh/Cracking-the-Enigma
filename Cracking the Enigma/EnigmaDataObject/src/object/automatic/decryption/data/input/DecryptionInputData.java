package object.automatic.decryption.data.input;

import object.automatic.decryption.difficulty.DecryptionDifficulty;
import object.automatic.decryption.results.DecryptionTaskResults;

import java.util.function.Consumer;

public class DecryptionInputData {
    private final DecryptionDifficulty decryptionDifficulty;
    private final int agentsCount;
    private final int tasksSize;
    //private final String originalMessage;
    private final String messageToDecrypt;
    //private final Consumer<DecryptionTaskResults> onDecryptionTaskResultsReceived;

    public DecryptionInputData(DecryptionDifficulty decryptionDifficulty, int agentsCount, int tasksSize, String messageToDecrypt) {
        this.decryptionDifficulty = decryptionDifficulty;
        this.agentsCount = agentsCount;
        this.tasksSize = tasksSize;
        //this.originalMessage = originalMessage;
        this.messageToDecrypt = messageToDecrypt;
        //this.onDecryptionTaskResultsReceived = onDecryptionTaskResultsReceived;
    }

    public DecryptionDifficulty getDecryptionDifficulty() {
        return decryptionDifficulty;
    }

    public int getAgentsCount() {
        return agentsCount;
    }

    public int getTasksSize() {
        return tasksSize;
    }

//    public String getOriginalMessage() {
//        return originalMessage;
//    }

    public String getMessageToDecrypt() {
        return messageToDecrypt;
    }

/*    public Consumer<DecryptionTaskResults> getOnDecryptionTaskResultsReceived() {
        return onDecryptionTaskResultsReceived;
    }*/
}
