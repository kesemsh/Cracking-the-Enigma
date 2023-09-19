package uboat.screens.main.tabs.contest.encryption;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import object.automatic.decryption.data.pre.decryption.PreDecryptionData;
import okhttp3.*;
import uboat.screens.main.tabs.contest.encryption.dictionary.DictionaryPanel;

import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

import static uboat.connection.constants.Constants.*;
import static uboat.connection.settings.ConnectionSettings.*;

public class EncryptionPanel {
    @FXML private TextField selectedMessageTextField;
    @FXML private VBox dictionaryPanel;
    @FXML private DictionaryPanel dictionaryPanelController;
    private Consumer<String> onError;
    private Consumer<PreDecryptionData> onPreDecryptionDataReceived;
    private Runnable onConfigurationSet;
    private Runnable onActionDone;

    @FXML
    private void clearTextFields() {
        clearEncryptionTextField();
    }

    @FXML
    private void resetToInitialConfiguration() {
        clearEncryptionTextField();
        resetConfiguration();
    }

    public void setUp(Runnable onConfigurationSet, Runnable onActionDone, Consumer<String> onError, Consumer<PreDecryptionData> onPreDecryptionDataReceived) {
        this.onConfigurationSet = onConfigurationSet;
        this.onActionDone = onActionDone;
        this.onError = onError;
        this.onPreDecryptionDataReceived = onPreDecryptionDataReceived;

        dictionaryPanelController.setUp(this::displayChosenDictionaryWordInTextField);
    }

    private void displayChosenDictionaryWordInTextField(String dictionaryWord) {
        if (selectedMessageTextField.getText().isEmpty()) {
            selectedMessageTextField.setText(selectedMessageTextField.getText() + dictionaryWord);
        } else {
            selectedMessageTextField.setText(selectedMessageTextField.getText() + " " + dictionaryWord);
        }
    }

    private void clearEncryptionTextField() {
        selectedMessageTextField.clear();
    }

    private void resetConfiguration() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + SET_CONFIGURATION).newBuilder();

        String finalUrl = urlBuilder.build().toString();

        sendAsyncPutRequest(finalUrl, RequestBody.create(new byte[] {}), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    onError.accept(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        onConfigurationSet.run();
                        response.close();
                    } else {
                        try {
                            onError.accept(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        dictionaryPanelController.updateAllowedWords(allowedWords);
    }

    public void reset() {
        clearEncryptionTextField();
        dictionaryPanelController.reset();
    }

    public String getSelectedMessage() {
        return selectedMessageTextField.getText();
    }

    @FXML
    private void evaluateMessage() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + DECRYPTION_INPUT_DATA).newBuilder();

        urlBuilder.addQueryParameter(MESSAGE, getSelectedMessage());
        String finalUrl = urlBuilder.build().toString();

        sendAsyncPostRequest(finalUrl, RequestBody.create(new byte[] {}), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> onError.accept(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    PreDecryptionData preDecryptionData = GSON_INSTANCE.fromJson(response.body().string(), PreDecryptionData.class);

                    Platform.runLater(() -> {
                        onPreDecryptionDataReceived.accept(preDecryptionData);
                        onActionDone.run();
                    });
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> onError.accept(errorMessage));
                }
            }
        });
    }
}
