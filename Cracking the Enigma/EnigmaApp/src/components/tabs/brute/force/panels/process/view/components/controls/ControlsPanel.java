package components.tabs.brute.force.panels.process.view.components.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ControlsPanel {
    @FXML private Button pauseProcessButton;
    @FXML private Button resumeProcessButton;
    @FXML private Button stopProcessButton;

    public void setUpControlsPanel(EventHandler<ActionEvent> pauseProcessEvent, EventHandler<ActionEvent> resumeProcessEvent, EventHandler<ActionEvent> stopProcessEvent) {
        pauseProcessButton.setOnAction(pauseProcessEvent);
        resumeProcessButton.setOnAction(resumeProcessEvent);
        stopProcessButton.setOnAction(stopProcessEvent);
    }
}
