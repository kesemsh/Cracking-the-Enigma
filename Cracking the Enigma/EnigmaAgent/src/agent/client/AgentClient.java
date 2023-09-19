package agent.client;

import agent.Agent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import agent.connection.settings.ConnectionSettings;

import java.io.IOException;
import java.net.URL;

public class AgentClient extends Application {
    private static final String MAIN_APP_FXML_PATH = "/agent/Agent.fxml";
    private static String agentName = null;
    private static String allyName = null;
    private static Integer threadsAmount = null;
    private static Integer pulledTasksAmount = null;

    public static void main(String[] args) {
        if (args.length > 0) {
            agentName = args[0];
            allyName = args[1];
            threadsAmount = Integer.parseInt(args[2]);
            pulledTasksAmount = Integer.parseInt(args[3]);
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader mainAppFXMLLoader = getMainAppFXMLLoader();
        Parent mainAppRoot = getMainAppRoot(mainAppFXMLLoader);
        Agent agent = mainAppFXMLLoader.getController();
        Scene scene = new Scene(mainAppRoot);

        agent.setPrimaryStage(primaryStage);
        if (agentName != null) {
            agent.setUsername(agentName);
            agent.setAllyName(allyName);
            agent.setThreadsAmount(threadsAmount);
            agent.setPulledTasksAmount(pulledTasksAmount);
            agent.sendLoginRequest();
        }
        primaryStage.setTitle("The Magic Enigma");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
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