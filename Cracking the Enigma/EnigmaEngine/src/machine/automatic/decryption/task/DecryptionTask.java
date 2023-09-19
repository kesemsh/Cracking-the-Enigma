package machine.automatic.decryption.task;

import machine.Machine;
import machine.automatic.decryption.decrypted.message.candidate.DecryptedMessageCandidate;
import machine.automatic.decryption.task.results.DecryptionTaskResults;
import machine.components.dictionary.Dictionary;
import object.machine.configuration.MachineConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DecryptionTask implements Runnable {
    private final Predicate<List<String>> areWordsInDictionary;
    private final String messageToDecrypt;
    private final List<MachineConfiguration> machineConfigurationsToCheckList;
    private final List<DecryptedMessageCandidate> decryptedMessageCandidatesResultList;
    private final BlockingQueue<DecryptionTaskResults> resultsQueue;
    private final Map<Integer, Machine> agentIDToMachine;
    private final Runnable checkIfPaused;

    public DecryptionTask(Predicate<List<String>> areWordsInDictionary, String messageToDecrypt, List<MachineConfiguration> machineConfigurationsToCheckList,
                          BlockingQueue<DecryptionTaskResults> resultsQueue, Map<Integer, Machine> agentIDToMachine, Runnable checkIfPaused) {
        this.areWordsInDictionary = areWordsInDictionary;
        this.messageToDecrypt = messageToDecrypt;
        this.machineConfigurationsToCheckList = machineConfigurationsToCheckList;
        this.resultsQueue = resultsQueue;
        this.decryptedMessageCandidatesResultList = new ArrayList<>();
        this.agentIDToMachine = agentIDToMachine;
        this.checkIfPaused = checkIfPaused;
    }

    @Override
    public void run() {
        long startTime, endTime;
        int currentThreadID = Math.toIntExact(Thread.currentThread().getId() % agentIDToMachine.size() + 1);
        Machine machine = agentIDToMachine.get(currentThreadID);

        checkIfPaused.run();
        startTime = System.nanoTime();
        machineConfigurationsToCheckList.forEach(currConfiguration -> {
            machine.setConfiguration(currConfiguration, false);
            String processedMessage = machine.processInput(messageToDecrypt, false, false);

            if (areWordsInDictionary.test(Arrays.asList(processedMessage.split(" ")))) {
                decryptedMessageCandidatesResultList.add(new DecryptedMessageCandidate(processedMessage, currentThreadID, currConfiguration));
            }
        });
        endTime = System.nanoTime();

        resultsQueue.add(new DecryptionTaskResults(decryptedMessageCandidatesResultList, endTime - startTime));
    }
}
