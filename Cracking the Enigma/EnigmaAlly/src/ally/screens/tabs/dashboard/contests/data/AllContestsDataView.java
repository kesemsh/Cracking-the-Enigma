package ally.screens.tabs.dashboard.contests.data;

import ally.screens.tabs.contest.data.JoinedContestDataView;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import object.automatic.decryption.active.teams.details.ActiveTeamDetails;
import object.automatic.decryption.data.contest.ContestData;
import object.automatic.decryption.difficulty.DecryptionDifficulty;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ally.connection.constants.Constants.*;
import static ally.connection.settings.ConnectionSettings.*;

public class AllContestsDataView {
    private Consumer<String> onError;
    private BooleanProperty gameJoined;
    private StringProperty gameTitle;
    private ObservableList<ContestData> allContestsDataObservableList;
    @FXML private TableView<ContestData> teamAgentsDataTableView;
    @FXML private TableColumn<ContestData, String> gameTitleColumn;
    @FXML private TableColumn<ContestData, String> uBoatNameColumn;
    @FXML private TableColumn<ContestData, Boolean> gameInProgressColumn;
    @FXML private TableColumn<ContestData, DecryptionDifficulty> decryptionDifficultyColumn;
    @FXML private TableColumn<ContestData, Integer> allAgentsCount;
    @FXML private TableColumn<ContestData, Integer> currentAgentsCount;
    @FXML private VBox joinedContestDataView;
    @FXML private JoinedContestDataView joinedContestDataViewController;
    @FXML private Button continueToGameButton;

    @FXML
    private void initialize() {
        allContestsDataObservableList = FXCollections.observableArrayList();
        gameTitleColumn.setCellValueFactory(new PropertyValueFactory<>("gameTitle"));
        uBoatNameColumn.setCellValueFactory(new PropertyValueFactory<>("uBoatName"));
        gameInProgressColumn.setCellValueFactory(new PropertyValueFactory<>("gameInProgress"));
        decryptionDifficultyColumn.setCellValueFactory(new PropertyValueFactory<>("decryptionDifficulty"));
        allAgentsCount.setCellValueFactory(new PropertyValueFactory<>("totalAlliesCount"));
        currentAgentsCount.setCellValueFactory(new PropertyValueFactory<>("currentAlliesCount"));
        teamAgentsDataTableView.setItems(allContestsDataObservableList);
        continueToGameButton.setDisable(true);
    }

    public void setUp(Consumer<String> onError, BooleanProperty gameChosen, StringProperty gameTitle) {
        this.onError = onError;
        this.gameTitle = gameTitle;
        gameJoined = gameChosen;
        joinedContestDataViewController.setUp(onError, gameTitle);
        teamAgentsDataTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                continueToGameButton.setDisable(false);
                this.gameTitle.set(newValue.getGameTitle());
                joinedContestDataViewController.pullJoinedContestData();
            }
        });
    }

    public boolean getGameJoined() {
        return gameJoined.get();
    }

    public void reset() {
        continueToGameButton.setDisable(true);
        allContestsDataObservableList.clear();
        joinedContestDataViewController.reset();
    }

    @FXML
    private void onContinueToGameButton() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + BATTLEFIELD_REGISTER).newBuilder();

        urlBuilder.addQueryParameter(BATTLEFIELD_NAME_PARAMETER, gameTitle.getValue());
        String finalUrl = urlBuilder.build().toString();

        sendAsyncPostRequest(finalUrl, RequestBody.create(new byte[] {}), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    onError.accept(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    Platform.runLater(() -> {
                        gameJoined.set(true);
                    });
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> {
                        onError.accept(errorMessage);
                    });
                }
            }
        });
    }

    public void pullAllContestsData() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CONTESTS_DATA).newBuilder();

        urlBuilder.addQueryParameter(BATTLEFIELD_NAME_PARAMETER, ALL_BATTLEFIELDS);
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
                    Type type = new TypeToken<List<ContestData>>() { }.getType();
                    List<ContestData> allContestDataList = GSON_INSTANCE.fromJson(responseBodyString, type);

                    allContestsDataObservableList.clear();
                    allContestsDataObservableList.addAll(allContestDataList);
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
