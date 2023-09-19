package components.tabs.brute.force.panels.decryption.input;

import components.tabs.brute.force.panels.decryption.input.encryption.BruteForceEncryptionPanel;
import components.tabs.brute.force.panels.decryption.input.overview.PreDecryptionOverview;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import exceptions.DecryptionDifficultyNotSelectedException;
import exceptions.TaskSizeIsInvalidException;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import machine.automatic.decryption.difficulty.DecryptionDifficulty;
import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.pre.decryption.data.PreDecryptionData;
import machine.automatic.decryption.task.results.DecryptionTaskResults;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class DecryptionInputPanel {
    @FXML private BruteForceEncryptionPanel bruteForceEncryptionPanelController;
    @FXML private Slider agentsCountSlider;
    @FXML private ComboBox<String> decryptionDifficultyComboBox;
    @FXML private Spinner<Integer> taskSizeSpinner;
    @FXML private Button evaluateDataButton;
    @FXML private Button startProcessButton;
    @FXML private PreDecryptionOverview preDecryptionOverviewController;
    @FXML private VBox overViewVBox;
    private Consumer<DecryptionTaskResults> onDecryptionTaskResultsReceived;
    private Consumer<String> onErrorReceived;
    private final ObjectProperty<PreDecryptionData> preDecryptionDataObjectProperty;

    public DecryptionInputPanel() {
        preDecryptionDataObjectProperty = new SimpleObjectProperty<>(null);
    }

    @FXML
    private void initialize() {
        overViewVBox.visibleProperty().bind(Bindings.isNotNull(preDecryptionDataObjectProperty));
    }

    @FXML
    private void clearSelection() {
        decryptionDifficultyComboBox.valueProperty().set(null);
        agentsCountSlider.setValue(2);
        taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1000, 100));
        bruteForceEncryptionPanelController.clearEncryptionTextField();

    }

    public DecryptionInputData getDecryptionInputData() {
        DecryptionDifficulty decryptionDifficulty = getSelectedDecryptionDifficulty();
        String originalMessage = getSelectedMessage();
        int agentsCount = (int) getSelectedNumberOfAgents();
        int tasksSize = getSelectedTaskSize();

        return new DecryptionInputData(decryptionDifficulty, originalMessage, agentsCount, tasksSize, onDecryptionTaskResultsReceived);
    }

    public void setUpDecryptionInputPanel(Consumer<DecryptionInputData> decryptionInputDataConsumer,
                                          EventHandler<ActionEvent> resetConfigurationEvent,
                                          Consumer<DecryptionTaskResults> onDecryptionTaskResultsReceived,
                                          Consumer<String> onErrorReceived,
                                          EventHandler<ActionEvent> startAutomaticDecryption) {
        setUpDecryptionDifficultyOptions();
        agentsCountSlider.setShowTickMarks(true);
        agentsCountSlider.setShowTickLabels(true);
        agentsCountSlider.setMin(2);
        agentsCountSlider.setMajorTickUnit(1);
        agentsCountSlider.setSnapToTicks(true);
        agentsCountSlider.setMinorTickCount(0);
        setTaskSizeSpinnerEditorFormat(taskSizeSpinner);
        taskSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1000, 100));
        bruteForceEncryptionPanelController.setUpBruteForceEncryptionPanel(resetConfigurationEvent);
        evaluateDataButton.setOnAction(event -> {
            try {
                decryptionInputDataConsumer.accept(getDecryptionInputData());
            } catch (Exception e) {
                onErrorReceived.accept(e.getMessage());
            }
        });
        startProcessButton.setOnAction(startAutomaticDecryption);
        this.onDecryptionTaskResultsReceived = onDecryptionTaskResultsReceived;
        this.onErrorReceived = onErrorReceived;
    }

    private void setTaskSizeSpinnerEditorFormat(Spinner<Integer> taskSizeSpinner) {
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (c.isContentChange()) {
                ParsePosition parsePosition = new ParsePosition(0);
                NumberFormat.getIntegerInstance().parse(c.getControlNewText(), parsePosition);
                if (parsePosition.getIndex() == 0 ||
                        parsePosition.getIndex() < c.getControlNewText().length()) {
                    return null;
                }
            }
            return c;
        };

        TextFormatter<Integer> integerTextFormatter = new TextFormatter<Integer>(
                new IntegerStringConverter(), 0, filter);
        taskSizeSpinner.getEditor().setTextFormatter(integerTextFormatter);
    }

    private void setUpDecryptionDifficultyOptions() {
        for (int i = 1; i <= 4; i++) {
            decryptionDifficultyComboBox.getItems().add(DecryptionDifficulty.fromInt(i).getStringValue());
        }
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        bruteForceEncryptionPanelController.updateAllowedWords(allowedWords);
    }

    public void resetBruteForceEncryptionPanel() {
        bruteForceEncryptionPanelController.resetBruteForceEncryptionPanel();
        preDecryptionOverviewController.resetOverviewPanel();
        clearSelection();
        preDecryptionDataObjectProperty.set(null);
    }

    public double getSelectedNumberOfAgents() {
        return agentsCountSlider.getValue();
    }

    public DecryptionDifficulty getSelectedDecryptionDifficulty() {
        if (decryptionDifficultyComboBox.getValue() == null) {
            throw new DecryptionDifficultyNotSelectedException();
        }
        else {
            return DecryptionDifficulty.fromString(decryptionDifficultyComboBox.getValue());
        }
    }

    public int getSelectedTaskSize() {
        if (taskSizeSpinner.getValue() == null || taskSizeSpinner.getValue().intValue() == 0) {
            throw new TaskSizeIsInvalidException();
        }
        else {
            return taskSizeSpinner.getValue().intValue();
        }
    }

    public String getSelectedMessage() {
        return bruteForceEncryptionPanelController.getSelectedMessage();
    }

    public void updateAgentsCount(int agentsCount) {
        agentsCountSlider.setMax(agentsCount);
    }

    public void updatePreDecryptionData(PreDecryptionData preDecryptionData) {
        preDecryptionOverviewController.updatePreDecryptionData(preDecryptionData);
        preDecryptionDataObjectProperty.set(preDecryptionData);
    }
}
