package object.automatic.decryption.data.pre.decryption;

import object.machine.configuration.MachineConfiguration;

public class PreDecryptionData {
    private final String originalMessage;
    private final String originalMessageWithoutExcludedChars;
    private final String messageToDecrypt;
    private final MachineConfiguration machineConfiguration;

    public PreDecryptionData(String originalMessage, String originalMessageWithoutExcludedChars, String messageToDecrypt, MachineConfiguration machineConfiguration) {
        this.originalMessageWithoutExcludedChars = originalMessageWithoutExcludedChars;
        this.originalMessage = originalMessage;
        this.messageToDecrypt = messageToDecrypt;
        this.machineConfiguration = machineConfiguration;
    }

    public String getOriginalMessageWithoutExcludedChars() {
        return originalMessageWithoutExcludedChars;
    }

    public String getMessageToDecrypt() {
        return messageToDecrypt;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public MachineConfiguration getMachineConfiguration() {
        return machineConfiguration;
    }
}
