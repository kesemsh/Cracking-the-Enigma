package components.tabs.brute.force.panels.process.view.components.progress;

import components.tabs.brute.force.panels.process.task.BruteForceProcessTask;
import components.tabs.brute.force.panels.process.task.details.view.TaskDetailsView;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import machine.automatic.decryption.decrypted.message.candidate.DecryptedMessageCandidate;
import object.machine.configuration.MachineConfiguration;
import org.controlsfx.control.TaskProgressView;

public class ProgressPanel {
    @FXML private TaskProgressView<BruteForceProcessTask> bruteForceTaskProgressView;
    @FXML private TaskDetailsView taskDetailsViewController;

    public void bindUIToTask(BruteForceProcessTask bruteForceProcessTask) {
        taskDetailsViewController.bindUIToTask(bruteForceProcessTask);
        bruteForceTaskProgressView.getTasks().add(bruteForceProcessTask);
    }
}
