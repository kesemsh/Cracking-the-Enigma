package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import enigma.app.EnigmaApp;
import org.fxmisc.cssfx.CSSFX;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private static final String MAIN_APP_FXML_PATH = "/enigma/app/EnigmaApp.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        CSSFX.start();

        FXMLLoader mainAppFXMLLoader = getMainAppFXMLLoader();
        Parent mainAppRoot = getMainAppRoot(mainAppFXMLLoader);
        EnigmaApp enigmaApp = mainAppFXMLLoader.getController();
        Scene scene = new Scene(mainAppRoot);

        enigmaApp.setPrimaryStage(primaryStage);
        primaryStage.setTitle("The Magic Enigma");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
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