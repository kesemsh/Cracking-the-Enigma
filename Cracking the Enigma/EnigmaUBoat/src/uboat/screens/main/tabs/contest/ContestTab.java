package uboat.screens.main.tabs.contest;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import object.automatic.decryption.data.pre.decryption.PreDecryptionData;
import object.automatic.decryption.status.ContestStatus;
import object.automatic.decryption.winner.ContestWinner;
import okhttp3.*;
import uboat.screens.main.tabs.AppTab;
import uboat.screens.main.tabs.contest.candidates.view.CandidatesView;
import uboat.screens.main.tabs.contest.encryption.EncryptionPanel;
import uboat.screens.main.tabs.contest.overview.PreDecryptionOverview;
import uboat.screens.main.tabs.contest.teams.details.view.TeamsDetailsView;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static uboat.connection.constants.Constants.GSON_INSTANCE;
import static uboat.connection.settings.ConnectionSettings.*;

public class ContestTab extends AppTab {
    private final ObjectProperty<Node> shownPanel;
    private final BooleanProperty gameInProgress;
    private ContestStatus contestStatus;
    private Consumer<String> onError;
    private Runnable onReady;
    private Timer activeTeamsDetailsTimer;
    private Timer gameStatusTimer;
    private Timer candidatesDataTimer;
    private BooleanProperty ready;
    @FXML private VBox encryptionPanel;
    @FXML private EncryptionPanel encryptionPanelController;
    @FXML private VBox postEncryptionPanelVBox;
    @FXML private PreDecryptionOverview preDecryptionOverviewController;
    @FXML private Button returnButton;
    @FXML private Button readyButton;
    @FXML private TeamsDetailsView teamsDetailsViewController;
    @FXML private ScrollPane candidatesView;
    @FXML private CandidatesView candidatesViewController;

    public ContestTab() {
        shownPanel = new SimpleObjectProperty<>(null);
        gameInProgress = new SimpleBooleanProperty(false);
        contestStatus = null;
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        encryptionPanel.visibleProperty().bind(Bindings.equal(shownPanel, encryptionPanel));
        postEncryptionPanelVBox.visibleProperty().bind(Bindings.equal(shownPanel, postEncryptionPanelVBox));
        shownPanel.set(encryptionPanel);
        candidatesView.setDisable(true);
    }

    public void setUp(Runnable onConfigurationSet, Runnable onActionDone, Consumer<ContestWinner> onGameFinished, Runnable onReady, Consumer<String> onError, BooleanProperty ready, BooleanProperty machineLoaded) {
        this.ready = ready;
        this.onError = onError;
        this.onReady = () -> {
            pullGameStatus();
            onReady.run();
        };
        readyButton.disableProperty().bind(ready);
        returnButton.disableProperty().bind(ready);
        machineLoaded.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                pullActiveTeamsDetails();
            }
        });
        gameInProgress.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                onGameFinished.accept(contestStatus.getContestWinner());
                sendResetContestRequest();
                gameStatusTimer.cancel();
                gameStatusTimer.purge();
                gameStatusTimer = null;
                pullActiveTeamsDetails();
            } else {
                candidatesView.setDisable(false);
                pullCandidatesData();
            }
        });
        encryptionPanelController.setUp(onConfigurationSet, onActionDone, onError, this::updatePreDecryptionData);
        candidatesViewController.setUp(onError);
    }

    private void pullGameStatus() {
        TimerTask getGameStatusRequestTask = new TimerTask() {
            @Override
            public void run() {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CONTEST_STATUS).newBuilder();
                String finalUrl = urlBuilder.build().toString();
                Request request = new Request.Builder()
                        .url(finalUrl)
                        .build();
                Call call = HTTP_CLIENT.newCall(request);

                try (Response response = call.execute()) {
                    ResponseBody responseBody = response.body();
                    String responseBodyString = responseBody.string();

                    if (response.isSuccessful()) {
                        ContestStatus receivedContestStatus = GSON_INSTANCE.fromJson(responseBodyString, ContestStatus.class);

                        if (contestStatus == null || contestStatus.getContestWinner() != receivedContestStatus.getContestWinner() || contestStatus.getContestInProgress() != receivedContestStatus.getContestInProgress()) {
                            contestStatus = receivedContestStatus;
                        }

                        Platform.runLater(() -> {
                            gameInProgress.set(receivedContestStatus.getContestInProgress());
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
        };

        gameStatusTimer = new Timer("Get Game Status Timer", true);
        gameStatusTimer.scheduleAtFixedRate(getGameStatusRequestTask, 0, 500);
    }

    private void pullCandidatesData() {
        TimerTask getCandidatesDataTask = new TimerTask() {
            @Override
            public void run() {
                if (gameInProgress.get()) {
                    candidatesViewController.pullCandidatesData();
                } else {
                    candidatesViewController.pullCandidatesData();
                    candidatesDataTimer.cancel();
                    candidatesDataTimer.purge();
                    candidatesDataTimer = null;
                }
            }
        };

        candidatesDataTimer = new Timer("Get Candidates Data Timer", true);
        candidatesDataTimer.scheduleAtFixedRate(getCandidatesDataTask, 0, 500);
    }

    private void pullActiveTeamsDetails() {
        TimerTask getActiveTeamsDetailsRequestTask = new TimerTask() {
            @Override
            public void run() {
                if (gameInProgress.get()) {
                    teamsDetailsViewController.update();
                    activeTeamsDetailsTimer.cancel();
                    activeTeamsDetailsTimer.purge();
                    activeTeamsDetailsTimer = null;
                } else {
                    teamsDetailsViewController.update();
                }
            }
        };

        activeTeamsDetailsTimer = new Timer("Active Teams Details Timer", true);
        activeTeamsDetailsTimer.scheduleAtFixedRate(getActiveTeamsDetailsRequestTask, 0, 500);
    }

    public void reset() {
        ready.set(false);
        teamsDetailsViewController.reset();
        candidatesViewController.reset();
    }

    private void sendResetContestRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RESET_CONTEST).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        sendAsyncPostRequest(finalUrl, RequestBody.create(new byte[] {}), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> onError.accept(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Platform.runLater(() -> reset());
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });
    }

    public void updatePreDecryptionData(PreDecryptionData preDecryptionData) {
        preDecryptionOverviewController.update(preDecryptionData);
        shownPanel.set(postEncryptionPanelVBox);
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        encryptionPanelController.updateAllowedWords(allowedWords);
    }

    @FXML
    private void onReturnButtonClicked() {
        shownPanel.set(encryptionPanel);
    }

    @FXML
    private void onReadyButtonClicked() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CONTEST_STATUS).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        sendAsyncPostRequest(finalUrl, RequestBody.create(new byte[] {}), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> onError.accept(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    Platform.runLater(() -> {
                        onReady.run();
                    });
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });
    }

    public void logout() {
        if (activeTeamsDetailsTimer != null) {
            activeTeamsDetailsTimer.cancel();
            activeTeamsDetailsTimer.purge();
            activeTeamsDetailsTimer = null;
        }
    }

    public void setUpTeamsDetailsView() {
        teamsDetailsViewController.setUp(onError);
    }
}
