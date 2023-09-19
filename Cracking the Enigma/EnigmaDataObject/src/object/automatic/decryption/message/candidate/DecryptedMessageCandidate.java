package object.automatic.decryption.message.candidate;

import object.automatic.decryption.data.task.details.DecryptionTaskDetails;
import object.machine.configuration.MachineConfiguration;

public class DecryptedMessageCandidate {
    private final String alliesName;
    private final String agentName;
    private final String message;
    private final MachineConfiguration machineConfiguration;
    private final DecryptionTaskDetails decryptionTaskDetails;

    public DecryptedMessageCandidate(String message, String alliesName, String agentName, MachineConfiguration machineConfiguration, DecryptionTaskDetails decryptionTaskDetails) {
        this.alliesName = alliesName;
        this.agentName = agentName;
        this.message = message;
        this.machineConfiguration = machineConfiguration;
        this.decryptionTaskDetails = decryptionTaskDetails;
    }

    public String getAlliesName() {
        return alliesName;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getMessage() {
        return message;
    }

    public MachineConfiguration getMachineConfiguration() {
        return machineConfiguration;
    }

    public String getDecryptionTaskDetails() {
        return decryptionTaskDetails.toString();
    }
}
