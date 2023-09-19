package components.tabs.brute.force.panels.process.task;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import machine.automatic.decryption.decrypted.message.candidate.DecryptedMessageCandidate;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;
import machine.automatic.decryption.task.results.DecryptionTaskResults;
import machine.engine.MachineEngine;

import java.util.Date;
import java.util.function.Consumer;

public class BruteForceProcessTask extends Task<Boolean> {
    private final PreDecryptionData preDecryptionData;
    private final MachineEngine machineEngine;
    private final IntegerProperty amountOfTasksCompleted;
    private final DoubleProperty amountOfTotalTasks;
    private final ObservableList<DecryptedMessageCandidate> decryptedMessageCandidatesResultsList = FXCollections.observableArrayList();
    private final LongProperty accumulatedTimeTakenForAllTasks;
    private final LongProperty averageTimeTakenPerTask;
    private final BooleanProperty decryptionInProgress;
    private final LongProperty timeElapsed;
    private final StringProperty originalMessage;
    private final StringProperty messageToDecrypt;
    private final IntegerProperty winningAgentID;
    private final Consumer<BruteForceProcessTask> onTaskFinish;
    private boolean didAgentWin = false;

    public BruteForceProcessTask(PreDecryptionData preDecryptionData, MachineEngine machineEngine, Consumer<BruteForceProcessTask> onTaskFinish) {
        updateTitle("Brute Force Decryption");
        this.preDecryptionData = preDecryptionData;
        this.machineEngine = machineEngine;
        amountOfTasksCompleted = new SimpleIntegerProperty(0);
        amountOfTotalTasks = new SimpleDoubleProperty(preDecryptionData.getAmountOfTotalTasks());
        accumulatedTimeTakenForAllTasks = new SimpleLongProperty(0);
        averageTimeTakenPerTask = new SimpleLongProperty(0);
        decryptionInProgress = new SimpleBooleanProperty(false);
        timeElapsed = new SimpleLongProperty(0);
        originalMessage = new SimpleStringProperty(preDecryptionData.getOriginalMessage());
        messageToDecrypt = new SimpleStringProperty(preDecryptionData.getMessageToDecrypt());
        decryptionInProgress.bind(Bindings.notEqual(amountOfTasksCompleted, amountOfTotalTasks));
        averageTimeTakenPerTask.bind(Bindings.createLongBinding(() -> amountOfTasksCompleted.get() == 0 ? 0L : accumulatedTimeTakenForAllTasks.get() / amountOfTasksCompleted.get(), amountOfTasksCompleted, accumulatedTimeTakenForAllTasks));
        amountOfTasksCompleted.addListener((observable, oldValue, newValue) -> updateProgress(amountOfTasksCompleted.get(), amountOfTotalTasks.get()));
        winningAgentID = new SimpleIntegerProperty(0);
        this.onTaskFinish = onTaskFinish;
    }

    @Override
    protected Boolean call() throws Exception {
        updateMessage("Starting decryption process..");
        machineEngine.startAutomaticDecryption();
        while (decryptionInProgress.get() && !isCancelled()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }

            machineEngine.checkIfPaused();
            Platform.runLater(() -> timeElapsed.set(timeElapsed.get() + 1));
        }

        updateMessage("Process finished!");

        return Boolean.TRUE;
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        machineEngine.stopAutomaticDecryption();
        succeeded();
    }

    @Override
    protected void succeeded() {
        if (!isCancelled()) {
            super.succeeded();
        }

        for (DecryptedMessageCandidate decryptedMessageCandidate : decryptedMessageCandidatesResultsList) {
            if (machineEngine.isDecryptedMessageCorrect(decryptedMessageCandidate)) {
                winningAgentID.set(decryptedMessageCandidate.getAgentID());
                didAgentWin = true;
                break;
            }
        }

        onTaskFinish.accept(this);
    }

    public void pauseAutomaticDecryption() {
        machineEngine.pauseAutomaticDecryption();
    }

    public void resumeAutomaticDecryption() {
        machineEngine.resumeAutomaticDecryption();
    }

    public boolean didAgentWin() {
        return didAgentWin;
    }

    public int getWinningAgentID() {
        return winningAgentID.get();
    }

    public IntegerProperty amountOfTasksCompletedProperty() {
        return amountOfTasksCompleted;
    }

    public DoubleProperty amountOfTotalTasksProperty() {
        return amountOfTotalTasks;
    }

    public ObservableList<DecryptedMessageCandidate> getDecryptedMessageCandidatesResultsList() {
        return decryptedMessageCandidatesResultsList;
    }

    public LongProperty averageTimeTakenPerTaskProperty() {
        return averageTimeTakenPerTask;
    }

    public LongProperty timeElapsedProperty() {
        return timeElapsed;
    }

    public StringProperty originalMessageProperty() {
        return originalMessage;
    }

    public StringProperty messageToDecryptProperty() {
        return messageToDecrypt;
    }

    public void onDecryptionTaskResultsReceived(DecryptionTaskResults decryptionTaskResults) {
        accumulatedTimeTakenForAllTasks.set(accumulatedTimeTakenForAllTasks.get() + decryptionTaskResults.getTimeTaken());
        amountOfTasksCompleted.set(amountOfTasksCompleted.get() + 1);
        decryptedMessageCandidatesResultsList.addAll(decryptionTaskResults.getDecryptedMessageCandidatesResultList());
        int messagesFoundCount = decryptionTaskResults.getDecryptedMessageCandidatesResultList().size();

        if (messagesFoundCount > 0) {
            int agentID = decryptionTaskResults.getDecryptedMessageCandidatesResultList().get(0).getAgentID();

            updateMessage(String.format("Agent %d found %d %s!", agentID, messagesFoundCount, messagesFoundCount == 1 ? "message" : "messages"));
        }
    }
}
