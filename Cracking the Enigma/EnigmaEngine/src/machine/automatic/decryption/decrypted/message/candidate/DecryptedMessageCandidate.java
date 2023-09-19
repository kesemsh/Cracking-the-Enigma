package machine.automatic.decryption.decrypted.message.candidate;

import object.machine.configuration.MachineConfiguration;

public class DecryptedMessageCandidate {
    private final String message;
    private final int agentID;
    private final MachineConfiguration machineConfiguration;

    public DecryptedMessageCandidate(String message, int agentID, MachineConfiguration machineConfiguration) {
        this.message = message;
        this.agentID = agentID;
        this.machineConfiguration = machineConfiguration;
    }

    public String getMessage() {
        return message;
    }

    public int getAgentID() {
        return agentID;
    }

    public MachineConfiguration getMachineConfiguration() {
        return machineConfiguration;
    }
}
