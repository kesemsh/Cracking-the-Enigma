package ally.screens.tabs.contest;

import ally.screens.tabs.contest.active.teams.details.TeamsDetailsView;
import ally.screens.tabs.contest.agents.progress.view.AgentsProgressView;
import ally.screens.tabs.contest.candidates.CandidatesView;
import ally.screens.tabs.contest.create.agent.CreateAgentPanel;
import ally.screens.tabs.contest.data.JoinedContestDataView;
import ally.screens.tabs.contest.dm.progress.view.DMProgressView;
import javafx.application.Platform;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import object.automatic.decryption.status.ContestStatus;
import object.automatic.decryption.winner.ContestWinner;
import okhttp3.*;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import static ally.connection.constants.Constants.*;
import static ally.connection.settings.ConnectionSettings.*;

public class ContestTab {
    private final BooleanProperty gameInProgress;
    private ContestStatus contestStatus;
    private Consumer<String> onError;
    private Timer gameStatusTimer;
    private Timer ActiveTeamsDetailsAndJoinedContestData;
    private Timer agentsProgressDataAndCandidatesDataTimer;
    private Runnable onReset;
    @FXML private Button resetContestButton;
    @FXML private VBox joinedContestDataView;
    @FXML private JoinedContestDataView joinedContestDataViewController;
    @FXML private VBox teamsDetailsView;
    @FXML private TeamsDetailsView teamsDetailsViewController;
    @FXML private ScrollPane candidatesView;
    @FXML private CandidatesView candidatesViewController;
    @FXML private ScrollPane agentsProgressView;
    @FXML private AgentsProgressView agentsProgressViewController;
    @FXML private ScrollPane dmProgressView;
    @FXML private DMProgressView dmProgressViewController;
    @FXML private VBox createAgentPanel;
    @FXML private CreateAgentPanel createAgentPanelController;
    @FXML private Button readyButton;
    @FXML private TextField taskSizeTextField;

    public ContestTab() {
        gameInProgress = new SimpleBooleanProperty(false);
        contestStatus = null;
    }

    @FXML
    private void initialize() {
        resetContestButton.setDisable(true);
    }

    public void setUp(Consumer<String> onError, BooleanProperty gameWasChosen, StringProperty gameTitle, Consumer<ContestWinner> onGameFinished, Runnable onReset,
                      IntegerBinding agentsCount, StringProperty allyName) {
        this.onError = onError;
        this.onReset = onReset;
        joinedContestDataViewController.setUp(onError, gameTitle);
        teamsDetailsViewController.setUp(onError);
        candidatesViewController.setUp(onError);
        agentsProgressViewController.setUp(onError);
        dmProgressViewController.setUp(onError);
        createAgentPanelController.setUp(onError, agentsCount, allyName);
        gameWasChosen.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                teamsDetailsViewController.setUpAlliesCount();
                pullActiveTeamsDetailsAndJoinedContestData(gameWasChosen);
            }
        });
        gameInProgress.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                pullAgentsAndDMProgressDataAndCandidatesData();
            }
            else {
                gameWasChosen.set(false);
                gameStatusTimer.cancel();
                gameStatusTimer.purge();
                gameStatusTimer = null;
                resetContestButton.setDisable(false);
                onGameFinished.accept(contestStatus.getContestWinner());
            }
        });
    }

    @FXML
    public void reset() {
        readyButton.setDisable(false);
        resetContestButton.setDisable(true);
        taskSizeTextField.clear();
        agentsProgressViewController.reset();
        dmProgressViewController.reset();
        candidatesViewController.reset();
        onReset.run();
        sendResetContestRequest();
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
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });
    }

    private void pullActiveTeamsDetailsAndJoinedContestData(BooleanProperty gameWasChosen) {
        TimerTask getActiveTeamsDetailsAndJoinedContestDataRequestTask = new TimerTask() {
            @Override
            public void run() {
                if (gameWasChosen.get()) {
                    teamsDetailsViewController.pullActiveTeamsDetails();
                    joinedContestDataViewController.pullJoinedContestData();
                } else {
                    ActiveTeamsDetailsAndJoinedContestData.cancel();
                    ActiveTeamsDetailsAndJoinedContestData.purge();
                    ActiveTeamsDetailsAndJoinedContestData = null;
                }
            }
        };

        ActiveTeamsDetailsAndJoinedContestData = new Timer("Active Teams Details Timer", true);
        ActiveTeamsDetailsAndJoinedContestData.scheduleAtFixedRate(getActiveTeamsDetailsAndJoinedContestDataRequestTask, 0, 500);
    }

    private void pullAgentsAndDMProgressDataAndCandidatesData() {
        TimerTask getAgentsProgressDataAndCandidatesDataRequestTask = new TimerTask() {
            @Override
            public void run() {
                if (gameInProgress.get()) {
                    agentsProgressViewController.pullAgentsProgressData();
                    dmProgressViewController.pullDMProgressData();
                    candidatesViewController.pullCandidatesData();
                } else {
                    agentsProgressViewController.pullAgentsProgressData();
                    dmProgressViewController.pullDMProgressData();
                    candidatesViewController.pullCandidatesData();
                    agentsProgressDataAndCandidatesDataTimer.cancel();
                    agentsProgressDataAndCandidatesDataTimer.purge();
                    agentsProgressDataAndCandidatesDataTimer = null;
                }
            }
        };

        agentsProgressDataAndCandidatesDataTimer = new Timer("Agents & DM Progress and Candidates Data Timer", true);
        agentsProgressDataAndCandidatesDataTimer.scheduleAtFixedRate(getAgentsProgressDataAndCandidatesDataRequestTask, 0, 500);
    }

    @FXML
    private void onReadyButtonClicked() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CONTEST_STATUS).newBuilder();

        urlBuilder.addQueryParameter(TASK_SIZE_PARAMETER, taskSizeTextField.getText());
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
                        readyButton.setDisable(true);
                        pullGameStatus();
                    });
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });
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

                        Platform.runLater(() -> gameInProgress.set(receivedContestStatus.getContestInProgress()));
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
}
