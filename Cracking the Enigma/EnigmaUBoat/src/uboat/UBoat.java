package uboat;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import object.automatic.decryption.winner.ContestWinner;
import uboat.connection.settings.ConnectionSettings;
import uboat.screens.login.LoginScreen;
import uboat.screens.main.MainScreen;

public class UBoat {
    private final ObjectProperty<Node> selectedPanel;
    private final BooleanProperty ready;
    private Stage primaryStage;
    @FXML private VBox greetingLabelsVBox;
    @FXML private Label usernameValueLabel;
    @FXML private Button logoutButton;
    @FXML private ScrollPane loginScreen;
    @FXML private LoginScreen loginScreenController;
    @FXML private ScrollPane mainScreen;
    @FXML private MainScreen mainScreenController;

    public UBoat() {
        selectedPanel = new SimpleObjectProperty<>(null);
        ready = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        loginScreen.visibleProperty().bind(Bindings.equal(selectedPanel, loginScreen));
        mainScreen.visibleProperty().bind(Bindings.equal(selectedPanel, mainScreen));
        selectedPanel.set(loginScreen);
        loginScreenController.setUp(this::displayErrorMessage, username -> {
            usernameValueLabel.setText(username);
            selectedPanel.set(mainScreen);
        });
        mainScreenController.setUp(this::displaySuccessMessage, this::displayErrorMessage, this::displayWinner, this::onReady, ready);
        logoutButton.visibleProperty().bind(mainScreen.visibleProperty());
        logoutButton.disableProperty().bind(ready);
        greetingLabelsVBox.visibleProperty().bind(mainScreen.visibleProperty());
    }

    private void displayErrorMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("An error has occurred!");
        showAlertWithDefaultAlertSettings(alert, errorMessage);
    }

    private void displaySuccessMessage(String successMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Success");
        alert.setHeaderText("Success!");
        showAlertWithDefaultAlertSettings(alert, successMessage);
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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainScreenController.setPrimaryStage(primaryStage);
    }

    @FXML
    private void logout() {
        loginScreenController.reset();
        mainScreenController.reset();
        selectedPanel.set(loginScreen);
        mainScreenController.logout();
        ConnectionSettings.logout();
    }

    private void onReady() {
        ready.set(true);
    }
}
