package ally.screens.tabs.contest.create.agent;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import object.automatic.decryption.data.team.agents.TeamAgentsData;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static ally.connection.constants.Constants.GSON_INSTANCE;
import static ally.connection.settings.ConnectionSettings.*;

public class CreateAgentPanel {
    private Consumer<String> onError;
    private IntegerBinding agentsCount;
    private StringProperty allyName;
    @FXML private Slider threadsAmountSlider;
    @FXML private TextField pulledTasksAmountTextField;

    @FXML
    private void initialize() {
        setThreadsAmountSliderSettings();
    }

    public void setUp(Consumer<String> onError, IntegerBinding agentsCount, StringProperty allyName) {
        this.onError = onError;
        this.agentsCount = agentsCount;
        this.allyName = allyName;
    }

    private void setThreadsAmountSliderSettings() {
        threadsAmountSlider.setShowTickMarks(true);
        threadsAmountSlider.setShowTickLabels(true);
        threadsAmountSlider.setMin(1);
        threadsAmountSlider.setMax(4);
        threadsAmountSlider.setMajorTickUnit(1);
        threadsAmountSlider.setSnapToTicks(true);
        threadsAmountSlider.setMinorTickCount(0);
    }

    @FXML
    private void onCreateAgentButtonClicked() {
        if (!checkPulledTasksAmountSelected()) {
            onError.accept("You must enter a positive number for the pulled tasks amount!");
        } else {
            startAgentProcess();
        }
    }

    private void startAgentProcess() {
        List<String> command = Arrays.asList("runAgent.bat",
                String.format("%s-agent-%d", allyName.get(), getAgentsCount() + 1),
                allyName.get(),
                String.valueOf((int) threadsAmountSlider.getValue()),
                pulledTasksAmountTextField.getText());
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        try {
            processBuilder.start();
        } catch (Exception e) {
            onError.accept(e.getMessage());
        }
    }

    public int getAgentsCount() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + TEAM_AGENTS_DATA).newBuilder();
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();

            if (response.isSuccessful()) {
                Type type = new TypeToken<List<TeamAgentsData>>() { }.getType();
                List<TeamAgentsData> teamsAgentsDataList = GSON_INSTANCE.fromJson(responseBodyString, type);

                response.close();

                return teamsAgentsDataList.size();
            } else {
                Platform.runLater(() -> onError.accept(responseBodyString));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> onError.accept(e.getMessage()));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exception) {
                throw new RuntimeException(e);
            }
        }

        return 0;
    }

    private boolean checkPulledTasksAmountSelected() {
        try {
            int pulledTasksAmount = Integer.parseInt(pulledTasksAmountTextField.getText());

            return pulledTasksAmount > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
