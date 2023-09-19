package components.tabs.brute.force;

import components.tabs.AppTab;
import components.tabs.brute.force.panels.process.task.BruteForceProcessTask;
import components.tabs.brute.force.panels.process.view.ProcessView;
import components.tabs.brute.force.panels.decryption.input.DecryptionInputPanel;
import components.tabs.brute.force.panels.summary.SummaryPanel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import java.util.Set;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;
import machine.engine.MachineEngine;

public class BruteForceTab extends AppTab {
    @FXML private ScrollPane decryptionInputPanel;
    @FXML private DecryptionInputPanel decryptionInputPanelController;
    @FXML private ScrollPane processView;
    @FXML private ProcessView processViewController;
    @FXML private ScrollPane summaryPanel;
    @FXML private SummaryPanel summaryPanelController;
    @FXML private HBox currentConfigurationView;
    private final ObjectProperty<Node> selectedPanel;

    public BruteForceTab() {
        selectedPanel = new SimpleObjectProperty<>(null);
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        decryptionInputPanel.visibleProperty().bind(Bindings.equal(selectedPanel, decryptionInputPanel));
        processView.visibleProperty().bind(Bindings.equal(selectedPanel, processView));
        summaryPanel.visibleProperty().bind(Bindings.equal(selectedPanel, summaryPanel));
        selectedPanel.set(decryptionInputPanel);
    }

    public void updatePreDecryptionData(PreDecryptionData preDecryptionData, MachineEngine machineEngine) {
        decryptionInputPanelController.updatePreDecryptionData(preDecryptionData);
        processViewController.updatePreDecryptionData(preDecryptionData, machineEngine);
    }

    public void setUpBruteForceTab(Consumer<DecryptionInputData> decryptionInputDataConsumer, EventHandler<ActionEvent> resetConfigurationEvent, Consumer<String> onErrorReceived) {
        processViewController.setUpProcessView(this::onTaskFinish);
        summaryPanelController.setUpSummaryPanel(e -> selectedPanel.set(decryptionInputPanel));
        decryptionInputPanelController.setUpDecryptionInputPanel(decryptionInputDataConsumer, resetConfigurationEvent, processViewController::onDecryptionTaskResultsReceived, onErrorReceived, e -> {
            selectedPanel.set(processView);
            processViewController.startAutomaticDecryption();
        });
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        decryptionInputPanelController.updateAllowedWords(allowedWords);
    }

    public void resetTab() {
        decryptionInputPanelController.resetBruteForceEncryptionPanel();
        selectedPanel.set(decryptionInputPanel);
    }

    public void updateAgentsCount(int agentsCount) {
        decryptionInputPanelController.updateAgentsCount(agentsCount);
    }

    private void onTaskFinish(BruteForceProcessTask bruteForceProcessTask) {
        summaryPanelController.updateSummaryPanel(bruteForceProcessTask);
        selectedPanel.set(summaryPanel);
        decryptionInputPanelController.resetBruteForceEncryptionPanel();
    }
}
