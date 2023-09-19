package machine.automatic.decryption.manager;

import machine.Machine;
import machine.automatic.decryption.decrypted.message.candidate.DecryptedMessageCandidate;
import machine.automatic.decryption.difficulty.DecryptionDifficulty;
import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.task.creator.DecryptionTasksCreator;
import machine.automatic.decryption.task.results.DecryptionTaskResults;
import machine.automatic.decryption.task.results.reader.DecryptionTasksResultsReader;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class DecryptionManager {
    private final DecryptionTasksResultsReader decryptionTasksResultsReader;
    private final BlockingQueue<DecryptionTaskResults> resultsQueue;
    private final DecryptionTasksCreator decryptionTasksCreator;
    private final BlockingQueue<Runnable> tasksQueue;
    private final Map<Integer, Machine> agentIDToMachine;
    private final Machine machineCopy;
    private final int agentsCount;
    private final int tasksQueueSize = 1000;
    private final ThreadPoolExecutor automaticDecryptionThreadPool;
    private final Consumer<DecryptionTaskResults> onDecryptionTaskResultsReceived;
    private final int amountOfTotalTasks;
    private final DecryptionInputData decryptionInputData;
    private volatile boolean decryptionPaused = false;

    public DecryptionManager(DecryptionInputData decryptionInputData, Machine machineCopy) {
        this.decryptionInputData = decryptionInputData;
        this.onDecryptionTaskResultsReceived = decryptionInputData.getOnDecryptionTaskResultsReceived();
        this.agentsCount = decryptionInputData.getAgentsCount();
        tasksQueue = new ArrayBlockingQueue<>(tasksQueueSize);
        resultsQueue = new LinkedBlockingQueue<>();
        this.machineCopy = machineCopy;
        agentIDToMachine = createAllMachineCopiesForAgents();
        automaticDecryptionThreadPool = new ThreadPoolExecutor(agentsCount, agentsCount, Integer.MAX_VALUE, TimeUnit.SECONDS, tasksQueue, r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
        decryptionTasksCreator = new DecryptionTasksCreator(decryptionInputData, tasksQueue, resultsQueue, machineCopy, agentIDToMachine, this::checkIfPaused);
        amountOfTotalTasks = decryptionTasksCreator.getAmountOfTotalTasks();
        decryptionTasksResultsReader = new DecryptionTasksResultsReader(resultsQueue, onDecryptionTaskResultsReceived, automaticDecryptionThreadPool, amountOfTotalTasks);
    }

    private Map<Integer, Machine> createAllMachineCopiesForAgents() {
        Map<Integer, Machine> result = new HashMap<>();

        for (int i = 1; i <= agentsCount; i++) {
            result.put(i, machineCopy.clone());
        }

        return result;
    }

    public void startAutomaticDecryption() {
        automaticDecryptionThreadPool.prestartAllCoreThreads();
        decryptionTasksCreator.start();
        decryptionTasksResultsReader.start();
    }

    public void pauseAutomaticDecryption() {
        if (!decryptionPaused) {
            decryptionPaused = true;
        }
    }

    public void resumeAutomaticDecryption() {
        if (decryptionPaused) {
            decryptionPaused = false;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    public void stopAutomaticDecryption() {
        automaticDecryptionThreadPool.shutdown();
        automaticDecryptionThreadPool.shutdownNow();
        decryptionTasksCreator.interrupt();
        decryptionTasksResultsReader.interrupt();
    }

    public void checkIfPaused() {
        if (decryptionPaused) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) { }
            }
        }
    }

    public int getAmountOfTotalTasks() {
        return amountOfTotalTasks;
    }

    public boolean isDecryptedMessageCorrect(DecryptedMessageCandidate decryptedMessageCandidate) {
        return decryptedMessageCandidate.getMessage().equals(decryptionInputData.getOriginalMessage()) && decryptedMessageCandidate.getMachineConfiguration().toString().equals(machineCopy.getInitialMachineConfiguration().toString());
    }
}
