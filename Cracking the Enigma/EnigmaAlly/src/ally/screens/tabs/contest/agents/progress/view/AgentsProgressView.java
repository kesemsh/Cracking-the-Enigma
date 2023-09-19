package ally.screens.tabs.contest.agents.progress.view;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import object.automatic.decryption.active.teams.details.ActiveTeamDetails;
import object.automatic.decryption.data.agent.progress.AgentProgressData;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ally.connection.constants.Constants.GSON_INSTANCE;
import static ally.connection.settings.ConnectionSettings.*;

public class AgentsProgressView {
    private final ObservableList<AgentProgressData> agentsProgressObservableList;
    private Consumer<String> onError;
    @FXML private TableView<AgentProgressData> agentsProgressTable;
    @FXML private TableColumn<AgentProgressData, String> agentNameColumn;
    @FXML private TableColumn<AgentProgressData, Integer> tasksCompletedColumn;
    @FXML private TableColumn<AgentProgressData, Integer> totalPulledTasksColumn;
    @FXML private TableColumn<AgentProgressData, Integer> foundCandidatesColumn;

    public AgentsProgressView() {
        agentsProgressObservableList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        agentNameColumn.setCellValueFactory(new PropertyValueFactory<>("agentName"));
        tasksCompletedColumn.setCellValueFactory(new PropertyValueFactory<>("tasksCompleted"));
        totalPulledTasksColumn.setCellValueFactory(new PropertyValueFactory<>("totalPulledTasks"));
        foundCandidatesColumn.setCellValueFactory(new PropertyValueFactory<>("foundCandidates"));
        agentsProgressTable.setItems(agentsProgressObservableList);
    }

    public void setUp(Consumer<String> onError) {
        this.onError = onError;
    }

    public void pullAgentsProgressData() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + AGENTS_PROGRESS_DATA).newBuilder();
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();

            if (response.isSuccessful()) {
                Type listType = new TypeToken<ArrayList<AgentProgressData>>(){}.getType();
                List<AgentProgressData> agentProgressDataList = GSON_INSTANCE.fromJson(responseBodyString, listType);

                Platform.runLater(() -> {
                    agentsProgressObservableList.clear();
                    agentsProgressObservableList.addAll(agentProgressDataList);
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
        agentsProgressObservableList.clear();
    }
}
