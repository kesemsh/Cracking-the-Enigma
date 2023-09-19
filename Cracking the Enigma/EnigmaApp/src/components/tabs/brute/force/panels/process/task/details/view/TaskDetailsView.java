package components.tabs.brute.force.panels.process.task.details.view;

import components.tabs.brute.force.panels.process.task.BruteForceProcessTask;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import machine.automatic.decryption.decrypted.message.candidate.DecryptedMessageCandidate;
import object.machine.configuration.MachineConfiguration;

public class TaskDetailsView {
    @FXML
    private TableView<DecryptedMessageCandidate> decryptedMessageCandidatesTable;
    @FXML private TableColumn<DecryptedMessageCandidate, Integer> agentIDColumn;
    @FXML private TableColumn<DecryptedMessageCandidate, String> messageFoundColumn;
    @FXML private TableColumn<DecryptedMessageCandidate, MachineConfiguration> configurationFoundColumn;
    @FXML private Label tasksCompletedValueLabel;
    @FXML private Label totalTasksValueLabel;
    @FXML private Label averageTimePerTaskValueLabel;
    @FXML private Label timeElapsedValueLabel;
    @FXML private Label amountOfMessagesFoundValueLabel;
    @FXML private Label originalMessageValueLabel;
    @FXML private Label messageToDecryptValueLabel;

    @FXML
    private void initialize() {
        agentIDColumn.setCellValueFactory(new PropertyValueFactory<>("agentID"));
        messageFoundColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        configurationFoundColumn.setCellValueFactory(new PropertyValueFactory<>("machineConfiguration"));
    }

    public void bindUIToTask(BruteForceProcessTask bruteForceProcessTask) {
        tasksCompletedValueLabel.textProperty().bind(bruteForceProcessTask.amountOfTasksCompletedProperty().asString());
        averageTimePerTaskValueLabel.textProperty().bind(bruteForceProcessTask.averageTimeTakenPerTaskProperty().asString());
        amountOfMessagesFoundValueLabel.textProperty().bind(Bindings.size(bruteForceProcessTask.getDecryptedMessageCandidatesResultsList()).asString());
        totalTasksValueLabel.textProperty().bind(Bindings.selectInteger(bruteForceProcessTask.amountOfTotalTasksProperty()).asString());
        timeElapsedValueLabel.textProperty().bind(bruteForceProcessTask.timeElapsedProperty().asString());
        messageToDecryptValueLabel.textProperty().bind(bruteForceProcessTask.messageToDecryptProperty());
        originalMessageValueLabel.textProperty().bind(bruteForceProcessTask.originalMessageProperty());
        decryptedMessageCandidatesTable.setItems(bruteForceProcessTask.getDecryptedMessageCandidatesResultsList());
    }
}
