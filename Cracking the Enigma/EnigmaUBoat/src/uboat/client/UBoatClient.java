package uboat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import uboat.UBoat;
import uboat.connection.settings.ConnectionSettings;

import java.io.IOException;
import java.net.URL;

public class UBoatClient extends Application {
    private static final String MAIN_APP_FXML_PATH = "/uboat/UBoat.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader mainAppFXMLLoader = getMainAppFXMLLoader();
        Parent mainAppRoot = getMainAppRoot(mainAppFXMLLoader);
        UBoat uBoat = mainAppFXMLLoader.getController();
        Scene scene = new Scene(mainAppRoot);

        uBoat.setPrimaryStage(primaryStage);
        primaryStage.setTitle("The Magic Enigma");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
            ConnectionSettings.logout();
            ConnectionSettings.shutdown();
        });
        primaryStage.show();
    }

    private Parent getMainAppRoot(FXMLLoader fxmlLoader) throws IOException {
        return fxmlLoader.load(fxmlLoader.getLocation().openStream());
    }

    private FXMLLoader getMainAppFXMLLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(MAIN_APP_FXML_PATH);
        fxmlLoader.setLocation(url);
        return fxmlLoader;
    }
}