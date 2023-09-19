package object.machine.state;

import object.machine.configuration.MachineConfiguration;

import java.util.List;
import java.util.Set;

public class MachineState {
    private final int availableRotorsCount;
    private final int activeRotorsCount;
    private final int reflectorsInStorageCount;
    private final int processedMessagesCount;
    private final MachineConfiguration initialConfiguration;
    private final MachineConfiguration currentConfiguration;
    private final List<Character> allKeys;
    private final Set<String> allowedWords;

    public MachineState(int availableRotorsCount, int activeRotorsCount, int reflectorsInStorageCount, int processedMessagesCount, List<Character> allKeys, Set<String> allowedWords) {
        this.availableRotorsCount = availableRotorsCount;
        this.activeRotorsCount = activeRotorsCount;
        this.reflectorsInStorageCount = reflectorsInStorageCount;
        this.processedMessagesCount = processedMessagesCount;
        this.allKeys = allKeys;
        this.allowedWords = allowedWords;
        initialConfiguration = null;
        currentConfiguration = null;
    }

    public MachineState(int availableRotorsCount, int activeRotorsCount, int reflectorsInStorageCount, int processedMessagesCount, List<Character> allKeys, Set<String> allowedWords, MachineConfiguration initialConfiguration, MachineConfiguration currentConfiguration) {
        this.availableRotorsCount = availableRotorsCount;
        this.activeRotorsCount = activeRotorsCount;
        this.reflectorsInStorageCount = reflectorsInStorageCount;
        this.processedMessagesCount = processedMessagesCount;
        this.allKeys = allKeys;
        this.allowedWords = allowedWords;
        this.initialConfiguration = initialConfiguration;
        this.currentConfiguration = currentConfiguration;
    }

    public int getAvailableRotorsCount() {
        return availableRotorsCount;
    }

    public int getActiveRotorsCount() {
        return activeRotorsCount;
    }

    public int getReflectorsInStorageCount() {
        return reflectorsInStorageCount;
    }

    public int getProcessedMessagesCount() {
        return processedMessagesCount;
    }

    public List<Character> getAllKeys() {
        return allKeys;
    }

    public Set<String> getAllowedWords() {
        return allowedWords;
    }

    public MachineConfiguration getInitialConfiguration() {
        return initialConfiguration;
    }

    public MachineConfiguration getCurrentConfiguration() {
        return currentConfiguration;
    }

    @Override
    public String toString() {
        StringBuilder messageToReturn = new StringBuilder();
        final String newLine = System.lineSeparator();

        messageToReturn.append("Machine State: ").append(newLine);
        messageToReturn.append(String.format("Active rotors count / Available rotors count: %d / %d", activeRotorsCount, availableRotorsCount)).append(newLine);
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