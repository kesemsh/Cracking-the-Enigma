package agent.screens.main;

import agent.decryption.task.DecryptionTask;
import agent.screens.main.candidates.view.CandidatesView;
import agent.screens.main.contest.data.view.JoinedContestDataView;
import agent.screens.main.progress.view.ProgressView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MainScreen {
    private Consumer<String> onError;
    private BooleanProperty gameInProgress;
    private BooleanProperty joinedBattlefield;
    private Timer candidatesDataTimer;
    private Timer joinedContestDataTimer;
    @FXML private Label connectedAllyLabel;
    @FXML private Label statusLabel;
    @FXML private ScrollPane candidatesView;
    @FXML private CandidatesView candidatesViewController;
    @FXML private VBox joinedContestDataView;
    @FXML private JoinedContestDataView joinedContestDataViewController;
    @FXML private VBox progressView;
    @FXML private ProgressView progressViewController;

    public void setUp(Consumer<String> onError, BooleanProperty gameInProgress, BooleanProperty joinedBattlefield, StringProperty gameTitle, StringProperty messageToDecrypt,
                      IntegerProperty tasksInQueue, IntegerProperty totalPulledTasks, IntegerProperty totalFinishedTasks, IntegerProperty totalCandidatesFound) {
        this.onError = onError;
        this.gameInProgress = gameInProgress;
        this.joinedBattlefield = joinedBattlefield;
        candidatesViewController.setUp(onError);
        joinedContestDataViewController.setUp(onError, gameTitle, messageToDecrypt);
        progressViewController.setUp(tasksInQueue, totalPulledTasks, totalFinishedTasks, totalCandidatesFound);
        gameInProgress.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                statusLabel.setText("Decrypting..");
                showProgressAndCandidates(true);
            } else {
                statusLabel.setText("Idle");
            }
        });
        joinedContestDataView.visibleProperty().bind(joinedBattlefield);
        showProgressAndCandidates(false);
        joinedBattlefield.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                joinedContestDataTimer.cancel();
                joinedContestDataTimer.purge();
                joinedContestDataTimer = null;
            }
        });
    }

    public void setConnectedAlly(String allyName) {
        connectedAllyLabel.setText(allyName);
    }

    public void showProgressAndCandidates(boolean show) {
        candidatesView.setVisible(show);
        progressView.setVisible(show);
    }

    public void addTask(DecryptionTask decryptionTask) {
        progressViewController.addTask(decryptionTask);
    }

    public void pullCandidatesData() {
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

    public void pullJoinedContestData() {
        TimerTask joinedContestDataTask = new TimerTask() {
            @Override
            public void run() {
                joinedContestDataViewController.pullJoinedContestData();
            }
        };

        joinedContestDataTimer = new Timer("Get Joined Contest Data Timer", true);
        joinedContestDataTimer.scheduleAtFixedRate(joinedContestDataTask, 0, 500);
    }

    public void reset() {
        candidatesViewController.reset();
        showProgressAndCandidates(false);
        progressViewController.resetTasksQueue();
    }
}