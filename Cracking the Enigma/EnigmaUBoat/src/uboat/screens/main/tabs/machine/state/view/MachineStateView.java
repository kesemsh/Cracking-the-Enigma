package uboat.screens.main.tabs.machine.state.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import object.machine.state.MachineState;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

import java.io.IOException;
import java.util.function.Consumer;

import static uboat.connection.constants.Constants.*;
import static uboat.connection.settings.ConnectionSettings.*;

public class MachineStateView {
    @FXML private Label activeRotorsCount;
    @FXML private Label reflectorsInStorageCount;
    @FXML private Label processedMessagesCount;

    public void update(MachineState machineState) {
        activeRotorsCount.setText(String.format("%d/%d", machineState.getActiveRotorsCount(), machineState.getAvailableRotorsCount()));
        reflectorsInStorageCount.setText(String.valueOf(machineState.getReflectorsInStorageCount()));
        processedMessagesCount.setText(String.valueOf(machineState.getProcessedMessagesCount()));
    }

    public void reset() {
        activeRotorsCount.setText("");
        reflectorsInStorageCount.setText("");
        processedMessagesCount.setText("");
    }
}