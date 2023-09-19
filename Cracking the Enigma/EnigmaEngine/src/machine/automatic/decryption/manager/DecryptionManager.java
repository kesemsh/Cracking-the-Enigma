package machine.automatic.decryption.manager;

import machine.Machine;
import object.automatic.decryption.data.input.DecryptionInputData;
import machine.automatic.decryption.task.creator.DecryptionTasksCreator;
import object.automatic.decryption.data.task.details.DecryptionTaskDetails;
import object.automatic.decryption.results.DecryptionTaskResults;
import machine.automatic.decryption.task.results.reader.DecryptionTasksResultsReader;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class DecryptionManager {
//    private final DecryptionTasksResultsReader decryptionTasksResultsReader;
//    private final BlockingQueue<DecryptionTaskResults> resultsQueue;
//    private final Consumer<DecryptionTaskResults> onDecryptionTaskResultsReceived;

    private final int amountOfTotalTasks;
    private final DecryptionTasksCreator decryptionTasksCreator;
    private final BlockingQueue<DecryptionTaskDetails> tasksQueue;
    private final int tasksQueueSize = 1000;

    public DecryptionManager(DecryptionInputData decryptionInputData, Machine machine, Runnable onTaskCreated) {
        tasksQueue = new ArrayBlockingQueue<>(tasksQueueSize);
        decryptionTasksCreator = new DecryptionTasksCreator(decryptionInputData, machine, tasksQueue, onTaskCreated);
        amountOfTotalTasks = decryptionTasksCreator.getAmountOfTotalTasks();
//        resultsQueue = new LinkedBlockingQueue<>();
//        decryptionTasksResultsReader = new DecryptionTasksResultsReader(resultsQueue, onDecryptionTaskResultsReceived, automaticDecryptionThreadPool, amountOfTotalTasks);
    }

    public void startAutomaticDecryption() {
        decryptionTasksCreator.start();
        //decryptionTasksResultsReader.start();
    }

    public List<DecryptionTaskDetails> getTasks(int tasksAmount) {
        List<DecryptionTaskDetails> tasksList = new ArrayList<>();

        try {
            for (int i = 0; i < tasksAmount; i++) {
                synchronized (tasksQueue) {
                    tasksList.add(tasksQueue.remove());
                }
            }
        } catch (Exception e) {}

        return tasksList;
    }

    public int getAmountOfTotalTasks() {
        return amountOfTotalTasks;
    }

    /*public boolean isDecryptedMessageCorrect(DecryptedMessageCandidate decryptedMessageCandidate) {
        return decryptedMessageCandidate.getMessage().equals(decryptionInputData.getOriginalMessage()) && decryptedMessageCandidate.getMachineConfiguration().toString().equals(machineCopy.getInitialMachineConfiguration().toString());
    }*/
}
