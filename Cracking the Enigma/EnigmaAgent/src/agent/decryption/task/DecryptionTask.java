package agent.decryption.task;

import javafx.application.Platform;
import javafx.concurrent.Task;
import machine.Machine;
import object.automatic.decryption.data.task.details.DecryptionTaskDetails;
import object.automatic.decryption.results.DecryptionTaskResults;
import object.automatic.decryption.message.candidate.DecryptedMessageCandidate;
import object.machine.configuration.MachineConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DecryptionTask extends Task<Boolean> implements Runnable {
    private final Predicate<List<String>> areWordsInDictionary;
    private final String messageToDecrypt;
    private final DecryptionTaskDetails decryptionTaskDetails;
    private final Consumer<DecryptionTaskResults> onResultsReceived;
    private final Map<Integer, Machine> threadIDToMachine;
    private final String allyName;
    private final String agentName;
    private final Consumer<DecryptionTask> addTask;
    private List<DecryptedMessageCandidate> decryptedMessageCandidatesResultList;
    private List<Character> allKeys;
    private double tasksCheckedCounter = 0;

    public DecryptionTask(Predicate<List<String>> areWordsInDictionary, String messageToDecrypt, DecryptionTaskDetails decryptionTaskDetails,
                          Consumer<DecryptionTaskResults> onResultsReceived, Map<Integer, Machine> threadIDToMachine,
                          String allyName, String agentName, Consumer<DecryptionTask> addTask) {
        updateTitle(decryptionTaskDetails.toString());
        this.areWordsInDictionary = areWordsInDictionary;
        this.messageToDecrypt = messageToDecrypt;
        this.decryptionTaskDetails = decryptionTaskDetails;
        this.onResultsReceived = onResultsReceived;
        this.threadIDToMachine = threadIDToMachine;
        this.allyName = allyName;
        this.agentName = agentName;
        this.addTask = addTask;
    }

    @Override
    public void run() {
        Platform.runLater(() -> addTask.accept(this));
        int currentThreadID = Math.toIntExact(Thread.currentThread().getId() % threadIDToMachine.size() + 1);
        Machine machine = threadIDToMachine.get(currentThreadID);

        decryptedMessageCandidatesResultList = new ArrayList<>();
        allKeys = machine.getAllKeys();
        MachineConfiguration endConfiguration = decryptionTaskDetails.getEndConfiguration();
        MachineConfiguration currentConfiguration = decryptionTaskDetails.getStartConfiguration();

        while (!currentConfiguration.equals(endConfiguration)) {
            checkConfiguration(machine, currentConfiguration);
            currentConfiguration = getNextConfiguration(currentConfiguration);
        }

        checkConfiguration(machine, currentConfiguration);

        onResultsReceived.accept(new DecryptionTaskResults(decryptedMessageCandidatesResultList));
        cancel();
    }

    private MachineConfiguration getNextConfiguration(MachineConfiguration machineConfiguration) {
        return new MachineConfiguration(new ArrayList<>(machineConfiguration.getRotorIDsInOrder()), getNextStartPositions(machineConfiguration), machineConfiguration.getReflectorID());
    }

    private List<Character> getNextStartPositions(MachineConfiguration machineConfiguration) {
        List<Character> startPositions = new ArrayList<>(machineConfiguration.getRotorStartPositionsByChar());
        int currentIndex = startPositions.size() - 1;

        while (currentIndex != -1) {
            Character nextKey = getNextKey(startPositions.get(currentIndex));

            startPositions.set(currentIndex, nextKey);
            if (nextKey.equals(allKeys.get(0))) {
                currentIndex--;
            } else {
                currentIndex = -1;
            }
        }

        return startPositions;
    }

    private Character getNextKey(Character character) {
        return allKeys.indexOf(character) == allKeys.size() - 1 ? allKeys.get(0) : allKeys.get(allKeys.indexOf(character) + 1);
    }

    private void checkConfiguration(Machine machine, MachineConfiguration machineConfiguration) {
        updateMessage("Checking configuration: " + machineConfiguration);
        machine.setConfiguration(machineConfiguration, false);
        String processedMessage = machine.processInput(messageToDecrypt, false, false);

        if (areWordsInDictionary.test(Arrays.asList(processedMessage.split(" ")))) {
            decryptedMessageCandidatesResultList.add(new DecryptedMessageCandidate(processedMessage, allyName, agentName, machineConfiguration.clone(), decryptionTaskDetails));
        }

        updateProgress(++tasksCheckedCounter, decryptionTaskDetails.getTaskSize() + 0.01);
        updateMessage("Finished checking configuration: " + machineConfiguration);
    }

    @Override
    protected Boolean call() throws Exception {
        run();

        return Boolean.TRUE;
    }
}
