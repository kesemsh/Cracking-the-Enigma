package uboat.screens.main.tabs.contest.teams.details.view;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import object.automatic.decryption.active.teams.details.ActiveTeamDetails;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static uboat.connection.settings.ConnectionSettings.*;
import static uboat.connection.constants.Constants.*;

public class TeamsDetailsView {
    private final ObservableList<ActiveTeamDetails> activeTeamDetailsObservableList;
    private final IntegerProperty totalAlliesCount;
    @FXML private Label connectedAlliesCountLabel;
    @FXML private Label totalAlliesCountLabel;
    @FXML private TableView<ActiveTeamDetails> teamsDetailsTable;
    @FXML private TableColumn<ActiveTeamDetails, String> teamNameColumn;
    @FXML private TableColumn<ActiveTeamDetails, Integer> taskSizeColumn;
    @FXML private TableColumn<ActiveTeamDetails, Integer> agentsColumn;
    private Consumer<String> onError;

    public TeamsDetailsView() {
        activeTeamDetailsObservableList = FXCollections.observableArrayList();
        totalAlliesCount = new SimpleIntegerProperty(0);
    }

    @FXML
    private void initialize() {
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("alliesName"));
        taskSizeColumn.setCellValueFactory(new PropertyValueFactory<>("taskSize"));
        agentsColumn.setCellValueFactory(new PropertyValueFactory<>("agentsCount"));
        teamsDetailsTable.setItems(activeTeamDetailsObservableList);
        connectedAlliesCountLabel.textProperty().bind(Bindings.size(activeTeamDetailsObservableList).asString());
        totalAlliesCountLabel.textProperty().bind(totalAlliesCount.asString());
    }

    public void reset() {
        activeTeamDetailsObservableList.clear();
    }

    public void setUp(Consumer<String> onError) {
        this.onError = onError;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + BATTLEFIELD_DETAILS).newBuilder();

        urlBuilder.addQueryParameter(BATTLEFIELD_DETAILS_TYPE_PARAMETER, ALLIES_COUNT);
        String finalUrl = urlBuilder.build().toString();

        sendAsyncGetRequest(finalUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> onError.accept(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    int alliesCount = GSON_INSTANCE.fromJson(responseBody, int.class);

                    Platform.runLater(() -> {
                        totalAlliesCount.set(alliesCount);
                    });
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });
    }

    public void update() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + ACTIVE_TEAMS_DETAILS).newBuilder();
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();

            if (response.isSuccessful()) {
                Type listType = new TypeToken<ArrayList<ActiveTeamDetails>>(){}.getType();
                List<ActiveTeamDetails> activeTeamDetailsList = GSON_INSTANCE.fromJson(responseBodyString, listType);

                Platform.runLater(() -> {
                    activeTeamDetailsObservableList.clear();
                    activeTeamDetailsObservableList.addAll(activeTeamDetailsList);
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
}
