package ally.screens.tabs.manager;

import ally.screens.tabs.contest.ContestTab;
import ally.screens.tabs.dashboard.DashboardTab;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import object.automatic.decryption.winner.ContestWinner;

import java.util.function.Consumer;

public class TabsManager {
    private final ObjectProperty<Node> selectedPanel;
    private final BooleanProperty gameChosen;
    private final StringProperty gameTitle;
    @FXML public Button dashboardTabButton;
    @FXML public Button contestTabButton;
    @FXML private ScrollPane dashboardTab;
    @FXML private DashboardTab dashboardTabController;
    @FXML private ScrollPane contestTab;
    @FXML private ContestTab contestTabController;

    public TabsManager() {
        gameChosen = new SimpleBooleanProperty(false);
        gameTitle = new SimpleStringProperty();
        selectedPanel = new SimpleObjectProperty<>(null);
    }

    @FXML
    private void initialize() {
        dashboardTab.visibleProperty().bind(Bindings.equal(selectedPanel, dashboardTab));
        dashboardTabButton.disableProperty().bind(Bindings.notEqual(selectedPanel, dashboardTab));
        contestTab.visibleProperty().bind(Bindings.equal(selectedPanel, contestTab));
        contestTabButton.disableProperty().bind(Bindings.notEqual(selectedPanel, contestTab));
        selectedPanel.set(dashboardTab);
        gameChosen.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectedPanel.set(contestTab);
            }
        });
    }

    public void setUp(Consumer<String> onError, Consumer<ContestWinner> onGameFinished, StringProperty allyName) {
        dashboardTabController.setUp(onError, gameChosen, gameTitle);
        contestTabController.setUp(onError, gameChosen, gameTitle, onGameFinished, () -> {
            dashboardTabController.reset();
            selectedPanel.set(dashboardTab);
            pullDashboardTabData();
        }, dashboardTabController.getAgentsCount(), allyName);
    }

    public void pullDashboardTabData() {
        dashboardTabController.pullTeamAgentsAndAllContestsData();
    }
}
