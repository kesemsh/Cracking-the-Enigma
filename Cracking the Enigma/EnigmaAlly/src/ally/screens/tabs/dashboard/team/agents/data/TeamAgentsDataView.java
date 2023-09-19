package ally.screens.tabs.dashboard.team.agents.data;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import object.automatic.decryption.data.team.agents.TeamAgentsData;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

import static ally.connection.constants.Constants.GSON_INSTANCE;
import static ally.connection.settings.ConnectionSettings.*;

public class TeamAgentsDataView {
    private Consumer<String> onError;
    private ObservableList<TeamAgentsData> teamAgentsDataObservableList;
    @FXML private TableView<TeamAgentsData> teamAgentsDataTableView;
    @FXML private TableColumn<TeamAgentsData, String> agentNameColumn;
    @FXML private TableColumn<TeamAgentsData, Integer> threadCountColumn;
    @FXML private TableColumn<TeamAgentsData, Integer> taskSizeColumn;

    @FXML
    private void initialize() {
        teamAgentsDataObservableList = FXCollections.observableArrayList();
        agentNameColumn.setCellValueFactory(new PropertyValueFactory<>("agentName"));
        threadCountColumn.setCellValueFactory(new PropertyValueFactory<>("threadsCount"));
        taskSizeColumn.setCellValueFactory(new PropertyValueFactory<>("taskSize"));
        teamAgentsDataTableView.setItems(teamAgentsDataObservableList);
    }

    public void setUp(Consumer<String> onError) {
        this.onError = onError;
    }

    public void reset() {
        teamAgentsDataObservableList.clear();
    }

    public void pullTeamAgentsData() {
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
                Platform.runLater(() -> {
                    Type type = new TypeToken<List<TeamAgentsData>>() { }.getType();
                    List<TeamAgentsData> teamsAgentsDataList = GSON_INSTANCE.fromJson(responseBodyString, type);

                    teamAgentsDataObservableList.clear();
                    teamAgentsDataObservableList.addAll(teamsAgentsDataList);
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

    public IntegerBinding getAgentsCount() {
        return Bindings.size(teamAgentsDataObservableList);
    }
}
