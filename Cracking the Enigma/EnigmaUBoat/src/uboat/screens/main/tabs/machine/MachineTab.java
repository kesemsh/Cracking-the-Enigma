package uboat.screens.main.tabs.machine;

import object.machine.state.MachineState;
import uboat.screens.main.tabs.AppTab;
import uboat.screens.main.tabs.machine.configuration.setter.MachineConfigurationSetter;
import uboat.screens.main.tabs.machine.state.view.MachineStateView;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import object.machine.configuration.MachineConfiguration;
import uboat.screens.main.tabs.configuration.view.MachineConfigurationView;

import java.util.List;
import java.util.function.Consumer;

public class MachineTab extends AppTab {
    @FXML private MachineConfigurationView initialConfigurationViewController;
    @FXML private MachineStateView machineStateViewController;
    @FXML private MachineConfigurationSetter machineConfigurationSetterController;
    private Consumer<String> onError;

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        initialConfigurationViewController.setConfigurationTitle("Initial Configuration:");
    }

    public void setUp(Consumer<String> onError) {
        this.onError = onError;
    }

    public void updateInitialConfigurationView(MachineConfiguration initialMachineConfiguration) {
        initialConfigurationViewController.setConfiguration(initialMachineConfiguration);
    }

    public void setUpConfigurationSetter(Runnable onConfigurationSet, Consumer<String> onError) {
        machineConfigurationSetterController.setUp(onConfigurationSet, onError);
    }

    public void setRotorsCount(int activeRotorsCount, int totalRotorsCount) {
        machineConfigurationSetterController.updateRotorsCount(activeRotorsCount, totalRotorsCount);
    }

    public void updateAllKeys(List<Character> allKeys, int rotorsCount) {
        machineConfigurationSetterController.updateAllKeys(allKeys, rotorsCount);
    }

    public void setUpReflectorIDSelector(int reflectorsInStorageCount) {
        machineConfigurationSetterController.updateReflectorIDSelector(reflectorsInStorageCount);
    }

    public void setUpInitialConfigurationView(BooleanProperty isConfigurationSet) {
        initialConfigurationViewController.isConfigurationSetProperty().bind(isConfigurationSet);
    }

    public void resetConfigurationSetter() {
        machineConfigurationSetterController.reset();
    }

    public void reset() {
        machineStateViewController.reset();
        machineConfigurationSetterController.resetConfigurationSetterSelection();
    }

    public void updateMachineDetails(MachineState machineState) {
        machineStateViewController.update(machineState);
        machineConfigurationSetterController.update(machineState);

        /*HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + MACHINE_DETAILS).newBuilder();

        String finalUrl = urlBuilder.build().toString();

        sendAsyncGetRequest(finalUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> onError.accept(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();

                    Platform.runLater(() -> {
                        MachineState machineState = GSON_INSTANCE.fromJson(body, MachineState.class);


                    });
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });*/
    }
}