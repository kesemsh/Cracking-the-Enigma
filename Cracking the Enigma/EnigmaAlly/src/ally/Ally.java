package ally;

import ally.screens.login.LoginScreen;
import ally.screens.tabs.manager.TabsManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import object.automatic.decryption.status.ContestStatus;
import object.automatic.decryption.winner.ContestWinner;

public class Ally {
    private final ObjectProperty<Node> selectedPanel;
    private Stage primaryStage;
    @FXML private HBox greetingLabelsHBox;
    @FXML private Label usernameValueLabel;
    @FXML private ScrollPane loginScreen;
    @FXML private LoginScreen loginScreenController;
    @FXML private ScrollPane tabsManager;
    @FXML private TabsManager tabsManagerController;

    public Ally() {
        selectedPanel = new SimpleObjectProperty<>(null);
    }

    @FXML
    private void initialize() {
        loginScreen.visibleProperty().bind(Bindings.equal(selectedPanel, loginScreen));
        tabsManager.visibleProperty().bind(Bindings.equal(selectedPanel, tabsManager));
        selectedPanel.set(loginScreen);
        loginScreenController.setUp(this::displayErrorMessage, username -> {
            usernameValueLabel.setText(username);
            selectedPanel.set(tabsManager);
            tabsManagerController.pullDashboardTabData();
        });
        tabsManagerController.setUp(this::displayErrorMessage, this::displayWinner, usernameValueLabel.textProperty());
        greetingLabelsHBox.visibleProperty().bind(tabsManager.visibleProperty());
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void displayErrorMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("An error has occurred!");
        showAlertWithDefaultAlertSettings(alert, errorMessage);
    }

    private void showAlertWithDefaultAlertSettings(Alert alertToShow, String messageToDisplay) {
        Label messageToDisplayLabel = new Label(messageToDisplay);

        alertToShow.initOwner(primaryStage);
        alertToShow.initStyle(StageStyle.DECORATED);
        messageToDisplayLabel.setWrapText(true);
        alertToShow.getDialogPane().setContent(messageToDisplayLabel);
        alertToShow.showAndWait();
    }

    private void displayWinner(ContestWinner contestWinner) {
        if (contestWinner != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String winnerMessage = String.format("Winning Team Name: %s%n", contestWinner.getAllyName()) +
                    String.format("Winning Agent Name: %s%n", contestWinner.getAgentName()) +
                    String.format("The machine configuration: %s%n", contestWinner.getMachineConfiguration()) +
                    String.format("The original message: %s", contestWinner.getOriginalMessage());

            alert.setTitle("Contest Results!");
            alert.setHeaderText("Contest Is Over!");
            showAlertWithDefaultAlertSettings(alert, winnerMessage);
        }
    }
}
