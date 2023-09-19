package uboat.screens.main.tabs.contest.candidates.view;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import object.automatic.decryption.active.teams.details.ActiveTeamDetails;
import object.automatic.decryption.message.candidate.DecryptedMessageCandidate;
import object.machine.configuration.MachineConfiguration;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static uboat.connection.constants.Constants.*;
import static uboat.connection.settings.ConnectionSettings.*;

public class CandidatesView {
    private Consumer<String> onError;
    private final ObservableList<DecryptedMessageCandidate> decryptedMessageCandidateObservableList = FXCollections.observableArrayList();
    @FXML private TableView<DecryptedMessageCandidate> decryptedMessageCandidatesTable;
    @FXML private TableColumn<DecryptedMessageCandidate, String> AlliesNameColumn;
    @FXML private TableColumn<DecryptedMessageCandidate, String> messageFoundColumn;
    @FXML private TableColumn<DecryptedMessageCandidate, MachineConfiguration> configurationFoundColumn;

    @FXML
    private void initialize() {
        AlliesNameColumn.setCellValueFactory(new PropertyValueFactory<>("alliesName"));
        messageFoundColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        configurationFoundColumn.setCellValueFactory(new PropertyValueFactory<>("machineConfiguration"));
        decryptedMessageCandidatesTable.setItems(decryptedMessageCandidateObservableList);
    }

    public void setUp(Consumer<String> onError) {
        this.onError = onError;
    }

    public void pullCandidatesData() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CANDIDATES).newBuilder();

        urlBuilder.addQueryParameter(CANDIDATES_LIST_SIZE_PARAMETER, String.valueOf(decryptedMessageCandidateObservableList.size()));
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();

            if (response.isSuccessful()) {
                Type listType = new TypeToken<ArrayList<ActiveTeamDetails>>(){}.getType();
                List<ActiveTeamDetails> activeTeamDetailsList = GSON_INSTANCE.fromJson(responseBodyString, listType);

                Platform.runLater(() -> {
                    Type type = new TypeToken<List<DecryptedMessageCandidate>>() { }.getType();
                    List<DecryptedMessageCandidate> deltaDecryptedMessageCandidateList = GSON_INSTANCE.fromJson(responseBodyString, type);

                    decryptedMessageCandidateObservableList.addAll(deltaDecryptedMessageCandidateList);
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

    public void reset() {
        decryptedMessageCandidateObservableList.clear();
    }
}
