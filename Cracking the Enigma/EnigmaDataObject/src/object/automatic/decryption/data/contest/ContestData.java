package object.automatic.decryption.data.contest;

import object.automatic.decryption.difficulty.DecryptionDifficulty;

public class ContestData {
    private final String gameTitle;
    private final String uBoatName;
    private final boolean gameInProgress;
    private final DecryptionDifficulty decryptionDifficulty;
    private final int totalAlliesCount;
    private final int currentAlliesCount;
    private final String messageToDecrypt;

    public ContestData(String gameTitle, String uBoatName, boolean gameInProgress, DecryptionDifficulty decryptionDifficulty, int totalAlliesCount, int currentAlliesCount, String messageToDecrypt) {
        this.gameTitle = gameTitle;
        this.uBoatName = uBoatName;
        this.gameInProgress = gameInProgress;
        this.decryptionDifficulty = decryptionDifficulty;
        this.totalAlliesCount = totalAlliesCount;
        this.currentAlliesCount = currentAlliesCount;
        this.messageToDecrypt = messageToDecrypt;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public String getUBoatName() {
        return uBoatName;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public DecryptionDifficulty getDecryptionDifficulty() {
        return decryptionDifficulty;
    }

    public int getTotalAlliesCount() {
        return totalAlliesCount;
    }

    public int getCurrentAlliesCount() {
        return currentAlliesCount;
    }

    public String getMessageToDecrypt() {
        return messageToDecrypt;
    }
}
