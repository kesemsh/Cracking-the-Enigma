package agent;

import agent.decryption.task.DecryptionTask;
import jaxb.xml.reader.XMLReader;
import agent.machine.builder.MachineBuilder;
import agent.screens.login.LoginScreen;
import agent.screens.main.MainScreen;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import machine.Machine;
import object.automatic.decryption.data.task.details.DecryptionTaskDetails;
import object.automatic.decryption.results.DecryptionTaskResults;
import object.automatic.decryption.status.ContestStatus;
import object.automatic.decryption.winner.ContestWinner;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static agent.connection.constants.Constants.*;
import static agent.connection.settings.ConnectionSettings.*;

public class Agent {
    private final ObjectProperty<Node> selectedPanel;
    private final BooleanProperty gameInProgress;
    private final BooleanProperty joinedToBattlefield;
    private final IntegerProperty finishedTasks;
    private final StringProperty gameTitle;
    private final StringProperty messageToDecrypt;
    private final AtomicInteger atomicInteger;
    private final IntegerProperty tasksInQueue;
    private final IntegerProperty totalPulledTasks;
    private final IntegerProperty totalCandidatesFound;
    private Stage primaryStage;
    private BlockingQueue<Runnable> tasksQueue;
    private ThreadPoolExecutor threadPool;
    private int threadsAmount;
    private Timer gameStatusTimer;
    private Timer joinedToBattlefieldDataTimer;
    private Timer allyResetDataTimer;
    private Machine machine;
    private Map<Integer, Machine> threadIDToMachine;
    private String allyName;
    private ContestStatus contestStatus = null;
    @FXML private HBox greetingLabelsHBox;
    @FXML private Label usernameValueLabel;
    @FXML private ScrollPane loginScreen;
    @FXML private LoginScreen loginScreenController;
    @FXML private ScrollPane mainScreen;
    @FXML private MainScreen mainScreenController;

    public Agent() {
        selectedPanel = new SimpleObjectProperty<>(null);
        gameInProgress = new SimpleBooleanProperty(false);
        joinedToBattlefield = new SimpleBooleanProperty(false);
        gameTitle = new SimpleStringProperty();
        messageToDecrypt = new SimpleStringProperty();
        finishedTasks = new SimpleIntegerProperty(0);
        atomicInteger = new AtomicInteger(0);
        tasksInQueue = new SimpleIntegerProperty(0);
        totalPulledTasks = new SimpleIntegerProperty(0);
        totalCandidatesFound = new SimpleIntegerProperty(0);
    }

    @FXML
    private void initialize() {
        loginScreen.visibleProperty().bind(Bindings.equal(selectedPanel, loginScreen));
        mainScreen.visibleProperty().bind(Bindings.equal(selectedPanel, mainScreen));
        selectedPanel.set(loginScreen);
        loginScreenController.setUp(this::displayErrorMessage, username -> {
            usernameValueLabel.setText(username);
            selectedPanel.set(mainScreen);
            threadsAmount = loginScreenController.getThreadsAmount();
            allyName = loginScreenController.getAllyName();
            mainScreenController.setConnectedAlly(allyName);
            tasksQueue = new LinkedBlockingQueue<>();
            pullJoinedToBattlefieldData();
        });
        mainScreenController.setUp(this::displayErrorMessage, gameInProgress, joinedToBattlefield, gameTitle, messageToDecrypt,
                tasksInQueue, totalPulledTasks, finishedTasks, totalCandidatesFound);
        greetingLabelsHBox.visibleProperty().bind(mainScreen.visibleProperty());
        gameInProgress.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tasksQueue.clear();
                threadPool = new ThreadPoolExecutor(threadsAmount, threadsAmount, Integer.MAX_VALUE, TimeUnit.SECONDS, tasksQueue, r -> {
                    Thread t = Executors.defaultThreadFactory().newThread(r);

                    t.setDaemon(true);

                    return t;
                });
                threadIDToMachine = createAllMachineCopiesForThreads();
                mainScreenController.pullCandidatesData();
                startDecryption();
            } else {
                gameStatusTimer.cancel();
                gameStatusTimer.purge();
                gameStatusTimer = null;
                threadPool.shutdown();
                threadPool.shutdownNow();
                tasksQueue.clear();
                joinedToBattlefield.set(false);
                pullAllyResetData();
                displayWinner(contestStatus.getContestWinner());
            }
        });
        joinedToBattlefield.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                joinedToBattlefieldDataTimer.cancel();
                joinedToBattlefieldDataTimer.purge();
                joinedToBattlefieldDataTimer = null;
                getAgentData();
                pullGameStatus();
                mainScreenController.pullJoinedContestData();
            }
        });
    }

    private void pullAllyResetData() {
        TimerTask allyResetDataTask = new TimerTask() {
            @Override
            public void run() {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RESET_CONTEST).newBuilder();
                String finalUrl = urlBuilder.build().toString();
                Request request = new Request.Builder()
                        .url(finalUrl)
                        .build();
                Call call = HTTP_CLIENT.newCall(request);

                try (Response response = call.execute()) {
                    ResponseBody responseBody = response.body();
                    String responseBodyString = responseBody.string();

                    if (response.isSuccessful()) {
                        if (GSON_INSTANCE.fromJson(responseBodyString, boolean.class)) {
                            Platform.runLater(() -> {
                                mainScreenController.reset();
                                finishedTasks.set(0);
                                atomicInteger.set(0);
                                tasksInQueue.set(0);
                                totalPulledTasks.set(0);
                                totalCandidatesFound.set(0);
                            });
                            pullJoinedToBattlefieldData();
                            allyResetDataTimer.cancel();
                            allyResetDataTimer.purge();
                            allyResetDataTimer = null;
                        }

                        response.close();
                    } else {
                        Platform.runLater(() -> displayErrorMessage(responseBodyString));
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> displayErrorMessage(e.getMessage()));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException exception) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        allyResetDataTimer = new Timer("Get Ally Reset Data Timer", true);
        allyResetDataTimer.scheduleAtFixedRate(allyResetDataTask, 0, 500);
    }

    private void pullJoinedToBattlefieldData() {
        TimerTask joinedToBattlefieldDataTask = new TimerTask() {
            @Override
            public void run() {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + BATTLEFIELD_REGISTER).newBuilder();
                String finalUrl = urlBuilder.build().toString();
                Request request = new Request.Builder()
                        .url(finalUrl)
                        .build();
                Call call = HTTP_CLIENT.newCall(request);

                try (Response response = call.execute()) {
                    ResponseBody responseBody = response.body();
                    String responseBodyString = responseBody.string();

                    if (response.isSuccessful()) {
                        boolean joinedToBattlefieldResult = GSON_INSTANCE.fromJson(responseBodyString, boolean.class);

                        Platform.runLater(() -> joinedToBattlefield.set(joinedToBattlefieldResult));

                        response.close();
                    } else {
                        Platform.runLater(() -> displayErrorMessage(responseBodyString));
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> displayErrorMessage(e.getMessage()));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException exception) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        joinedToBattlefieldDataTimer = new Timer("Get Joined Battlefield Data Timer", true);
        joinedToBattlefieldDataTimer.scheduleAtFixedRate(joinedToBattlefieldDataTask, 0, 500);
    }

    private void getAgentData() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + AGENT_DATA).newBuilder();
        urlBuilder.addQueryParameter(AGENT_DATA_TYPE, MACHINE_STRING);
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();

            if (response.isSuccessful()) {
                machine = new MachineBuilder().buildEnigmaMachine(XMLReader.getEnigmaFromXMLFile(responseBody.byteStream()));
            } else {
                displayErrorMessage(responseBody.string());
            }
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }

        urlBuilder = HttpUrl.parse(BASE_URL + AGENT_DATA).newBuilder();
        urlBuilder.addQueryParameter(AGENT_DATA_TYPE, BATTLEFIELD_NAME_PARAMETER);
        finalUrl = urlBuilder.build().toString();
        request = new Request.Builder()
                .url(finalUrl)
                .build();
        call = HTTP_CLIENT.newCall(request);

        try (Response response = call.execute()) {
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();

            if (response.isSuccessful()) {
                gameTitle.set(GSON_INSTANCE.fromJson(responseBodyString, String.class));
            } else {
                displayErrorMessage(responseBodyString);
            }
        } catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private Map<Integer, Machine> createAllMachineCopiesForThreads() {
        Map<Integer, Machine> result = new HashMap<>();

        for (int i = 1; i <= threadsAmount; i++) {
            result.put(i, machine.clone());
        }

        return result;
    }

    private void displayErrorMessage(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Error");
            alert.setHeaderText("An error has occurred!");
            showAlertWithDefaultAlertSettings(alert, errorMessage);
        });
    }

    private void showAlertWithDefaultAlertSettings(Alert alertToShow, String messageToDisplay) {
        Label messageToDisplayLabel = new Label(messageToDisplay);

        alertToShow.initOwner(primaryStage);
        alertToShow.initStyle(StageStyle.DECORATED);
        messageToDisplayLabel.setWrapText(true);
        alertToShow.getDialogPane().setContent(messageToDisplayLabel);
        alertToShow.showAndWait();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void startDecryption() {
        threadPool.prestartAllCoreThreads();
        getTasksList();
    }

    private void onResultsReceived(DecryptionTaskResults decryptionTaskResults) {
        if (gameInProgress.get()) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + DECRYPTION_TASKS).newBuilder();
            String finalUrl = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .post(RequestBody.create(GSON_INSTANCE.toJson(decryptionTaskResults).getBytes()))
                    .build();
            Call call = HTTP_CLIENT.newCall(request);

            try (Response response = call.execute()) {
                ResponseBody responseBody = response.body();
                String responseBodyString = responseBody.string();

                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        finishedTasks.set(finishedTasks.get() + 1);
                        tasksInQueue.set(tasksInQueue.get() - 1);
                        totalCandidatesFound.set(totalCandidatesFound.get() + decryptionTaskResults.getDecryptedMessageCandidatesResultList().size());
                    });
                    atomicInteger.set(atomicInteger.get() - 1);
                    if (atomicInteger.get() == 0 && gameInProgress.get()) {
                        getTasksList();
                    }
                } else {
                    displayErrorMessage(responseBodyString);
                }
            } catch (IOException e) {
                displayErrorMessage(e.getMessage());
            }
        }
    }

    private void getTasksList() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + DECRYPTION_TASKS).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        sendAsyncGetRequest(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> displayErrorMessage(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBodyString = response.body().string();

                if (response.isSuccessful()) {
                    Type listType = new TypeToken<ArrayList<DecryptionTaskDetails>>() {}.getType();
                    List<DecryptionTaskDetails> tasksList = GSON_INSTANCE.fromJson(responseBodyString, listType);

                    atomicInteger.set(tasksList.size());
                    Platform.runLater(() -> {
                        totalPulledTasks.set(totalPulledTasks.get() + tasksList.size());
                        tasksInQueue.set(tasksList.size());
                    });
                    if (gameInProgress.get()) {
                        tasksList.forEach(task -> {
                            DecryptionTask decryptionTask = new DecryptionTask(machine.getDictionary()::areWordsInDictionary, messageToDecrypt.get(), task, Agent.this::onResultsReceived, threadIDToMachine, allyName, usernameValueLabel.getText(), mainScreenController::addTask);

                            tasksQueue.add(decryptionTask);
                        });
                    }
                } else {
                    Platform.runLater(() -> displayErrorMessage(responseBodyString));
                }
            }
        });
    }

    private void pullGameStatus() {
        TimerTask getGameStatusRequestTask = new TimerTask() {
            @Override
            public void run() {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + CONTEST_STATUS).newBuilder();
                String finalUrl = urlBuilder.build().toString();
                Request request = new Request.Builder()
                        .url(finalUrl)
                        .build();
                Call call = HTTP_CLIENT.newCall(request);

                try (Response response = call.execute()) {
                    ResponseBody responseBody = response.body();
                    String responseBodyString = responseBody.string();

                    if (response.isSuccessful()) {
                        ContestStatus receivedContestStatus = GSON_INSTANCE.fromJson(responseBodyString, ContestStatus.class);

                        if (contestStatus == null || contestStatus.getContestWinner() != receivedContestStatus.getContestWinner() || contestStatus.getContestInProgress() != receivedContestStatus.getContestInProgress()) {
                            contestStatus = receivedContestStatus;
                        }

                        Platform.runLater(() -> gameInProgress.set(receivedContestStatus.getContestInProgress()));
                        response.close();
                    } else {
                        Platform.runLater(() -> displayErrorMessage(responseBodyString));
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> displayErrorMessage(e.getMessage()));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException exception) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        gameStatusTimer = new Timer("Get Game Start Timer", true);
        gameStatusTimer.scheduleAtFixedRate(getGameStatusRequestTask, 0, 500);
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

    public void setUsername(String username) {
        loginScreenController.setUsername(username);
    }

    public void setAllyName(String allyName) {
        loginScreenController.setAllyName(allyName);
    }

    public void setThreadsAmount(int threadsAmount) {
        loginScreenController.setThreadsAmount(threadsAmount);
    }

    public void setPulledTasksAmount(int pulledTasksAmount) {
        loginScreenController.setPulledTasksAmount(pulledTasksAmount);
    }

    public void sendLoginRequest() {
        loginScreenController.sendLoginRequest();
    }
}
