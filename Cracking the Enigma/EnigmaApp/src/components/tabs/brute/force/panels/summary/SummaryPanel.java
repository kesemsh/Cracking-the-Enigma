package components.tabs.brute.force.panels.summary;

import components.tabs.brute.force.panels.process.task.BruteForceProcessTask;
import components.tabs.brute.force.panels.process.task.details.view.TaskDetailsView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SummaryPanel {
    @FXML private Label taskStatusValueLabel;
    @FXML private VBox winningAgentInfoVBox;
    @FXML private Label winningAgentValueLabel;
    @FXML private TaskDetailsView taskDetailsViewController;
    @FXML private Button goBackButton;

    public void setUpSummaryPanel(EventHandler<ActionEvent> goBackEvent) {
        goBackButton.setOnAction(goBackEvent);
    }

    public void updateSummaryPanel(BruteForceProcessTask bruteForceProcessTask) {
        taskDetailsViewController.bindUIToTask(bruteForceProcessTask);
        if (bruteForceProcessTask.isCancelled()) {
            taskStatusValueLabel.setText("Cancelled!");
        } else {
            taskStatusValueLabel.setText("Completed!");
        }

        if (bruteForceProcessTask.didAgentWin()) {
            winningAgentInfoVBox.setVisible(true);
            winningAgentValueLabel.setText(String.format("Agent %d found the correct configuration!", bruteForceProcessTask.getWinningAgentID()));
        } else {
            winningAgentInfoVBox.setVisible(false);
        }
    }
}
