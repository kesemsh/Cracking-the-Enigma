package agent.screens.main.progress.view;

import agent.decryption.task.DecryptionTask;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.TaskProgressView;

public class ProgressView {
    @FXML private Label tasksInQueueLabel;
    @FXML private Label totalPulledTasksLabel;
    @FXML private Label totalFinishedTasksLabel;
    @FXML private Label totalCandidatesFoundLabel;
    @FXML private TaskProgressView<DecryptionTask> taskProgressView;

    public void setUp(IntegerProperty tasksInQueue, IntegerProperty totalPulledTasks, IntegerProperty totalFinishedTasks, IntegerProperty totalCandidatesFound) {
        tasksInQueueLabel.textProperty().bind(tasksInQueue.asString());
        totalPulledTasksLabel.textProperty().bind(totalPulledTasks.asString());
        totalFinishedTasksLabel.textProperty().bind(totalFinishedTasks.asString());
        totalCandidatesFoundLabel.textProperty().bind(totalCandidatesFound.asString());
    }

    public void addTask(DecryptionTask decryptionTask) {
        taskProgressView.getTasks().add(decryptionTask);
    }

    public void resetTasksQueue() {
        taskProgressView.getTasks().clear();
    }
}
