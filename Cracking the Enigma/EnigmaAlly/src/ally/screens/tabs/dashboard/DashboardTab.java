package ally.screens.tabs.dashboard;

import ally.screens.tabs.dashboard.contests.data.AllContestsDataView;
import ally.screens.tabs.dashboard.team.agents.data.TeamAgentsDataView;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class DashboardTab {
    private Timer dashboardDataTimer;
    @FXML private ScrollPane teamAgentsDataView;
    @FXML private TeamAgentsDataView teamAgentsDataViewController;
    @FXML private ScrollPane allContestsDataView;
    @FXML private AllContestsDataView allContestsDataViewController;

    public void setUp(Consumer<String> onError, BooleanProperty gameChosen, StringProperty gameTitle) {
        teamAgentsDataViewController.setUp(onError);
        allContestsDataViewController.setUp(onError, gameChosen, gameTitle);
    }

    public void pullTeamAgentsAndAllContestsData() {
        TimerTask getDashboardDataRequestTask = new TimerTask() {
            @Override
            public void run() {
                if (allContestsDataViewController.getGameJoined()) {
                    dashboardDataTimer.cancel();
                    dashboardDataTimer.purge();
                    dashboardDataTimer = null;
                } else {
                    teamAgentsDataViewController.pullTeamAgentsData();
                    allContestsDataViewController.pullAllContestsData();
                }
            }
        };

        dashboardDataTimer = new Timer("Dashboard data timer", true);
        dashboardDataTimer.scheduleAtFixedRate(getDashboardDataRequestTask, 0, 500);
    }

    public void reset() {
        allContestsDataViewController.reset();
    }

    public IntegerBinding getAgentsCount() {
        return teamAgentsDataViewController.getAgentsCount();
    }
}
