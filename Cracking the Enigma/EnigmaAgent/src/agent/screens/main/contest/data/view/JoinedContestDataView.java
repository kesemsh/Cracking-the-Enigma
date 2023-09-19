package agent.screens.main.contest.data.view;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import object.automatic.decryption.data.contest.ContestData;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

import static agent.connection.constants.Constants.*;
import static agent.connection.settings.ConnectionSettings.*;

public class JoinedContestDataView {
    private Consumer<String> onError;
    private StringProperty gameTitle;
    private StringProperty messageToDecrypt;
    @FXML private Label gameTitleLabel;
    @FXML private Label uBoatNameLabel;
    @FXML private Label gameInProgressLabel;
    @FXML private Label decryptionDifficultyLabel;
    @FXML private Label alliesCountLabel;
    @FXML private Label messageToDecryptLabel;

    public JoinedContestDataView() {
        this.gameTitle = new SimpleStringProperty();
    }

    public void setUp(Consumer<String> onError, StringProperty gameTitle, StringProperty messageToDecrypt) {
        this.onError = onError;
        this.gameTitle = gameTitle;
        this.messageToDecrypt = messageToDecrypt;
    }

    private void update(ContestData contestData) {
        gameTitleLabel.setText(contestData.getGameTitle());
        uBoatNameLabel.setText(contestData.getUBoatName());
        gameInProgressLabel.setText(String.valueOf(contestData.isGameInProgress()));
        decryptionDifficultyLabel.setText(contestData.getDecryptionDifficulty().getStringValue());
        alliesCountLabel.setText(String.valueOf(contestData.getTotalAlliesCount()));
        messageToDecryptLabel.setText(contestData.getMessageToDecrypt());
    }

    public void reset() {
        gameTitleLabel.setText("");
        uBoatNameLabel.setText("");
        gameInProgressLabel.setText("");
        decryptionDifficultyLabel.setText("");
        alliesCountLabel.setText("");
        messageToDecryptLabel.setText("");
    }

    public void pullJoinedContestData() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CONTESTS_DATA).newBuilder();

        urlBuilder.addQueryParameter(BATTLEFIELD_NAME_PARAMETER, gameTitle.get());
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();

            if (response.isSuccessful()) {
                Platform.runLater(() -> {
                    Type type = new TypeToken<List<ContestData>>() { }.getType();
                    List<ContestData> JoinedContestDataList = GSON_INSTANCE.fromJson(responseBodyString, type);

                    update(JoinedContestDataList.get(0));
                    messageToDecrypt.set(JoinedContestDataList.get(0).getMessageToDecrypt());
                });
                response.close();
            } else {
                Platform.runLater(() -> onError.accept(responseBodyString));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> onError.accept(e.getMessage()));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exception) {
                throw new RuntimeException(e);
            }
        }
    }
}
