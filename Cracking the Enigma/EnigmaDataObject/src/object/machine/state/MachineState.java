package object.machine.state;

import object.machine.configuration.MachineConfiguration;

public class MachineState {
    private final int possibleRotorsCount;
    private final int activeRotorsCount;
    private final int reflectorsInStorageCount;
    private final int processedMessagesCount;
    private final MachineConfiguration initialConfiguration;
    private final MachineConfiguration currentConfiguration;

    public MachineState(int possibleRotorsCount, int activeRotorsCount, int reflectorsInStorageCount, int processedMessagesCount) {
        this.possibleRotorsCount = possibleRotorsCount;
        this.activeRotorsCount = activeRotorsCount;
        this.reflectorsInStorageCount = reflectorsInStorageCount;
        this.processedMessagesCount = processedMessagesCount;
        initialConfiguration = null;
        currentConfiguration = null;
    }

    public MachineState(int possibleRotorsCount, int activeRotorsCount, int reflectorsInStorageCount, int processedMessagesCount, MachineConfiguration initialConfiguration, MachineConfiguration currentConfiguration) {
        this.possibleRotorsCount = possibleRotorsCount;
        this.activeRotorsCount = activeRotorsCount;
        this.reflectorsInStorageCount = reflectorsInStorageCount;
        this.processedMessagesCount = processedMessagesCount;
        this.initialConfiguration = initialConfiguration;
        this.currentConfiguration = currentConfiguration;
    }

    @Override
    public String toString() {
        StringBuilder messageToReturn = new StringBuilder();
        final String newLine = System.lineSeparator();

        messageToReturn.append("Machine State: ").append(newLine);
        messageToReturn.append(String.format("Active rotors count / Possible rotors count: %d / %d", activeRotorsCount, possibleRotorsCount)).append(newLine);
        messageToReturn.append(String.format("Amount of reflectors in storage: %d", reflectorsInStorageCount)).append(newLine);
        messageToReturn.append(String.format("Amount of messages processed: %d", processedMessagesCount)).append(newLine);
        if (initialConfiguration != null) {
            messageToReturn.append("Initial Machine Configuration: ").append(newLine);
            messageToReturn.append(initialConfiguration).append(newLine);
            messageToReturn.append("Current Machine Configuration: ").append(newLine);
            messageToReturn.append(currentConfiguration).append(newLine);
        }
        else {
            messageToReturn.append("No configuration has been set.").append(newLine);
        }
        return messageToReturn.toString();
    }
}