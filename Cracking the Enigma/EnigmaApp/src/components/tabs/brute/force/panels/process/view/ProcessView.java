package components.tabs.brute.force.panels.process.view;

import components.tabs.brute.force.panels.process.task.BruteForceProcessTask;
import components.tabs.brute.force.panels.process.view.components.controls.ControlsPanel;
import components.tabs.brute.force.panels.process.view.components.progress.ProgressPanel;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;
import machine.automatic.decryption.task.results.DecryptionTaskResults;
import machine.engine.MachineEngine;

import java.util.function.Consumer;

public class ProcessView {
    private Thread bruteForceProcessThread;
    private BruteForceProcessTask bruteForceProcessTask;
    private PreDecryptionData preDecryptionData;
    private MachineEngine machineEngine;
    @FXML private ProgressPanel progressPanelController;
    @FXML private ControlsPanel controlsPanelController;
    private Consumer<BruteForceProcessTask> onTaskFinish;

    @FXML
    private void initialize() {
        controlsPanelController.setUpControlsPanel(e-> pauseAutomaticDecryption(), e -> resumeAutomaticDecryption(), e -> stopAutomaticDecryption());
    }
    
    public void setUpProcessView(Consumer<BruteForceProcessTask> onTaskFinish) {
        this.onTaskFinish = onTaskFinish;
    }

    public void startAutomaticDecryption() {
        bruteForceProcessTask = new BruteForceProcessTask(preDecryptionData, machineEngine, onTaskFinish);
        bruteForceProcessThread = new Thread(bruteForceProcessTask, "Brute Force Process Thread");
        bruteForceProcessThread.setDaemon(true);
        progressPanelController.bindUIToTask(bruteForceProcessTask);
        bruteForceProcessThread.start();
    }

    private void pauseAutomaticDecryption() {
        bruteForceProcessTask.pauseAutomaticDecryption();
    }

    private void resumeAutomaticDecryption() {
        bruteForceProcessTask.resumeAutomaticDecryption();
    }

    private void stopAutomaticDecryption() {
        bruteForceProcessTask.cancel(true);
    }

    public void onDecryptionTaskResultsReceived(DecryptionTaskResults decryptionTaskResults) {
        Platform.runLater(() -> {
            bruteForceProcessTask.onDecryptionTaskResultsReceived(decryptionTaskResults);
        });
    }

    public void updatePreDecryptionData(PreDecryptionData preDecryptionData, MachineEngine machineEngine) {
        this.preDecryptionData = preDecryptionData;
        this.machineEngine = machineEngine;
    }
}
