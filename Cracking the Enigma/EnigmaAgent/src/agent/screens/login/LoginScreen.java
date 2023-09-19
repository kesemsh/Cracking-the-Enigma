package agent.screens.login;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import object.automatic.decryption.data.input.agent.AgentInputData;
import object.machine.state.MachineState;
import object.user.type.UserType;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

import static agent.connection.constants.Constants.*;
import static agent.connection.settings.ConnectionSettings.*;

public class LoginScreen {
    private final ObservableList<String> alliesNamesList;
    private Consumer<String> onError;
    private Consumer<String> onSuccessfulLogin;
    private Timer alliesDataTimer;
    private String username;
    private String allyName;
    private int threadsAmount;
    private int pulledTasksAmount;
    @FXML private Button loginButton;
    @FXML private TextField loginTextField;
    @FXML private ListView<String> alliesListView;
    @FXML private Slider threadsAmountSlider;
    @FXML private TextField pulledTasksAmountTextField;

    public LoginScreen() {
        alliesNamesList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setThreadsAmountSliderSettings();
        pullAlliesData();
    }

    private void setThreadsAmountSliderSettings() {
        threadsAmountSlider.setShowTickMarks(true);
        threadsAmountSlider.setShowTickLabels(true);
        threadsAmountSlider.setMin(1);
        threadsAmountSlider.setMax(4);
        threadsAmountSlider.setMajorTickUnit(1);
        threadsAmountSlider.setSnapToTicks(true);
        threadsAmountSlider.setMinorTickCount(0);
    }

    private void pullAlliesData() {
        TimerTask getAlliesDataRequestTask = new TimerTask() {
            @Override
            public void run() {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + ALLIES_DATA).newBuilder();
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
                            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                            List<String> alliesNamesListFromResponse = GSON_INSTANCE.fromJson(responseBodyString, listType);

                            alliesNamesListFromResponse.forEach(x -> {
                                if (!alliesNamesList.contains(x)) {
                                    alliesNamesList.add(x);
                                    alliesListView.getItems().add(x);
                                }
                            });

                            List<String> namesToRemove = new ArrayList<>();

                            alliesNamesList.forEach(x -> {
                                if (!alliesNamesListFromResponse.contains(x)) {
                                    namesToRemove.add(x);
                                    alliesListView.getItems().remove(x);
                                }
                            });

                            namesToRemove.forEach(alliesNamesList::remove);
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
        };

        alliesDataTimer = new Timer("Get Allies Data Timer", true);
        alliesDataTimer.scheduleAtFixedRate(getAlliesDataRequestTask, 0, 500);
    }

    public void setUp(Consumer<String> onError, Consumer<String> onSuccessfulLogin) {
        this.onError = onError;
        this.onSuccessfulLogin = onSuccessfulLogin;
    }

    @FXML
    private void onLoginButtonClicked() {
        if (loginTextField.getText().isEmpty()) {
            onError.accept("You must enter a name!");
        } else if (alliesListView.getSelectionModel().getSelectedItem() == null) {
            onError.accept("You must select an ally to join!");
        } else if (alliesListView.getSelectionModel().getSelectedItems().size() > 1) {
            onError.accept("You can only select 1 ally to join!!");
        } else if (!checkPulledTasksAmountSelected()) {
            onError.accept("You must enter a positive number for the pulled tasks amount!");
        } else {
            loginButton.setDisable(true);
            username = loginTextField.getText();
            allyName = alliesListView.getSelectionModel().getSelectedItem();
            threadsAmount = (int) threadsAmountSlider.getValue();
            pulledTasksAmount = Integer.parseInt(pulledTasksAmountTextField.getText());
            sendLoginRequest();
        }
    }

    private boolean checkPulledTasksAmountSelected() {
        try {
            int pulledTasksAmount = Integer.parseInt(pulledTasksAmountTextField.getText());

            return pulledTasksAmount > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void reset() {
        loginTextField.clear();
    }

    public void sendLoginRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + LOGIN).newBuilder();

        urlBuilder.addQueryParameter(USERNAME_PARAMETER, username);
        urlBuilder.addQueryParameter(USER_TYPE_PARAMETER, UserType.AGENT.getStringValue());
        String finalUrl = urlBuilder.build().toString();

        sendAsyncPostRequest(finalUrl, RequestBody.create(
                GSON_INSTANCE.toJson(
                        new AgentInputData(allyName,
                                threadsAmount,
                                pulledTasksAmount))
                        .getBytes()), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    onError.accept(e.getMessage());
                    loginButton.setDisable(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        onSuccessfulLogin.accept(username);
                        loginButton.setDisable(false);
                        alliesDataTimer.cancel();
                        alliesDataTimer.purge();
                        alliesDataTimer = null;
                    });
                    response.close();
                } else {
                    String errorMessage = response.body().string();

                    Platform.runLater(() -> {
                        onError.accept(errorMessage);
                        loginButton.setDisable(false);
                    });
                }
            }
        });
    }

    public int getThreadsAmount() {
        return threadsAmount;
    }

    public int getPulledTasksAmount() {
        return pulledTasksAmount;
    }

    public String getAllyName() {
        return allyName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAllyName(String allyName) {
        this.allyName = allyName;
    }

    public void setThreadsAmount(int threadsAmount) {
        this.threadsAmount = threadsAmount;
    }

    public void setPulledTasksAmount(int pulledTasksAmount) {
        this.pulledTasksAmount = pulledTasksAmount;
    }
}
