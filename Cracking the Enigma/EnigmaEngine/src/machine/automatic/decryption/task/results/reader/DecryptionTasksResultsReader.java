package machine.automatic.decryption.task.results.reader;

import javafx.beans.property.BooleanProperty;
import machine.automatic.decryption.task.results.DecryptionTaskResults;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DecryptionTasksResultsReader extends Thread {
    private final BlockingQueue<DecryptionTaskResults> resultsQueue;
    private final Consumer<DecryptionTaskResults> onResultsReceived;
    private final ThreadPoolExecutor agentsThreadPool;
    private final int amountOfTotalTasks;

    public DecryptionTasksResultsReader(BlockingQueue<DecryptionTaskResults> resultsQueue, Consumer<DecryptionTaskResults> onResultsReceived,
                                        ThreadPoolExecutor agentsThreadPool, int amountOfTotalTasks) {
        setName("Decryption Tasks Results Reader");
        setDaemon(true);
        this.resultsQueue = resultsQueue;
        this.onResultsReceived = onResultsReceived;
        this.agentsThreadPool = agentsThreadPool;
        this.amountOfTotalTasks = amountOfTotalTasks;
    }

    @Override
    public void run() {
        int tasksReceivedCounter = 0;

        try {
            while (tasksReceivedCounter < amountOfTotalTasks && !isInterrupted()) {
                DecryptionTaskResults decryptionTaskResults = resultsQueue.take();

                onResultsReceived.accept(decryptionTaskResults);
                tasksReceivedCounter++;
            }
        } catch (InterruptedException ignored) { }

        if (!isInterrupted()) {
            agentsThreadPool.shutdown();
            try {
                agentsThreadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) { }
        }
    }
}
