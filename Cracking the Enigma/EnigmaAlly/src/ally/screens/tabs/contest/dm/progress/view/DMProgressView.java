package ally.screens.tabs.contest.dm.progress.view;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import object.automatic.decryption.active.teams.details.ActiveTeamDetails;
import object.automatic.decryption.data.dm.progress.DMProgressData;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ally.connection.constants.Constants.GSON_INSTANCE;
import static ally.connection.settings.ConnectionSettings.*;

public class DMProgressView {
    private Consumer<String> onError;
    @FXML private Label totalTasksLabel;
    @FXML private Label createdTasksLabel;
    @FXML private Label tasksCompletedLabel;

    public void setUp(Consumer<String> onError) {
        this.onError = onError;
    }

    public void pullDMProgressData() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + DM_PROGRESS_DATA).newBuilder();
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();

            if (response.isSuccessful()) {
                DMProgressData dmProgressData = GSON_INSTANCE.fromJson(responseBodyString, DMProgressData.class);

                Platform.runLater(() -> {
                    totalTasksLabel.setText(String.valueOf(dmProgressData.getTotalTasksCount()));
                    createdTasksLabel.setText(String.valueOf(dmProgressData.getCreatedTasksCount()));
                    tasksCompletedLabel.setText(String.valueOf(dmProgressData.getTasksCompletedCount()));
                });
                response.close();
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
    }

    public void reset() {
        totalTasksLabel.setText("");
        createdTasksLabel.setText("");
        tasksCompletedLabel.setText("");
    }
}
