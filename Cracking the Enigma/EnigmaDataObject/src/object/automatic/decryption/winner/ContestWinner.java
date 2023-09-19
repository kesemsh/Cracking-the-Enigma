package object.automatic.decryption.winner;

import object.machine.configuration.MachineConfiguration;

public class ContestWinner {
    private final String allyName;
    private final String agentName;
    private final MachineConfiguration machineConfiguration;
    private final String originalMessage;

    public ContestWinner(String allyName, String agentName, MachineConfiguration machineConfiguration, String originalMessage) {
        this.allyName = allyName;
        this.agentName = agentName;
        this.machineConfiguration = machineConfiguration;
        this.originalMessage = originalMessage;
    }

    public String getAllyName() {
        return allyName;
    }

    public String getAgentName() {
        return agentName;
    }

    public MachineConfiguration getMachineConfiguration() {
        return machineConfiguration;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }
}
