package ally.client;

import ally.Ally;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ally.connection.settings.ConnectionSettings;

import java.io.IOException;
import java.net.URL;

public class AllyClient extends Application {
    private static final String MAIN_APP_FXML_PATH = "/ally/Ally.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader mainAppFXMLLoader = getMainAppFXMLLoader();
        Parent mainAppRoot = getMainAppRoot(mainAppFXMLLoader);
        Ally ally = mainAppFXMLLoader.getController();
        Scene scene = new Scene(mainAppRoot);

        ally.setPrimaryStage(primaryStage);
        primaryStage.setTitle("The Magic Enigma");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);
        primaryStage.setMinHeight(400);
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