package uboat.screens.main.file.manager;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static uboat.connection.settings.ConnectionSettings.*;
import static uboat.connection.constants.Constants.*;

public class FileManager {
    private File loadedFile;
    private Stage primaryStage;
    private Consumer<String> onError;
    private Runnable onSuccessfulMachineLoad;
    @FXML private TextField loadedFilePathTextField;
    @FXML public Button loadMachineFromFileButton;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showLoadedFilePath() {
        loadedFilePathTextField.setText(loadedFile.getAbsolutePath());
    }

    public void setUpFileManager(Consumer<String> onError, Runnable onSuccessfulMachineLoad) {
        this.onError = onError;
        this.onSuccessfulMachineLoad = onSuccessfulMachineLoad;
    }

    @FXML
    private void loadMachineFromFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Machine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files ", "*.xml"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file == null) {
            return;
        }

        loadedFile = file;
        sendLoadMachineFileRequest();
    }

    private void sendLoadMachineFileRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FILE_MANAGER).newBuilder();
        String finalUrl = urlBuilder.build().toString();
        RequestBody requestBody =
                new MultipartBody.Builder()
                        .addFormDataPart(FILE_PARAMETER, loadedFile.getName(), RequestBody.create(loadedFile, MediaType.parse("text/plain")))
                        .build();

        sendAsyncPostRequest(finalUrl, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> onError.accept(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        showLoadedFilePath();
                        onSuccessfulMachineLoad.run();
                        loadMachineFromFileButton.setDisable(true);
                    });
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });
    }

    public void reset() {
        loadedFile = null;
        loadedFilePathTextField.setText("No file is loaded.");
    }
}