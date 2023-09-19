package object.automatic.decryption.data.task.details;

import object.machine.configuration.MachineConfiguration;

public class DecryptionTaskDetails {
    private final MachineConfiguration startConfiguration;
    private final MachineConfiguration endConfiguration;
    private final int taskSize;

    public DecryptionTaskDetails(MachineConfiguration startConfiguration, MachineConfiguration endConfiguration, int taskSize) {
        this.startConfiguration = startConfiguration;
        this.endConfiguration = endConfiguration;
        this.taskSize = taskSize;
    }

    public MachineConfiguration getStartConfiguration() {
        return startConfiguration;
    }

    public MachineConfiguration getEndConfiguration() {
        return endConfiguration;
    }

    public int getTaskSize() {
        return taskSize;
    }

    @Override
    public String toString() {
        return startConfiguration + " - " + endConfiguration;
    }
}
