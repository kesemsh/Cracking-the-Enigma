package components.tabs.encryption.panels.history.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import object.machine.configuration.MachineConfiguration;
import object.machine.history.SingleProcessHistory;

import java.util.List;

public class SingleConfigurationTableView extends TitledPane {
    private final TableView<SingleProcessHistory> machineHistoryTable;
    private final TableColumn<SingleProcessHistory, String> preProcessedMessageColumn;
    private final TableColumn<SingleProcessHistory, String> processedMessageColumn;
    private final TableColumn<SingleProcessHistory, Long> timeTakenColumn;
    private final MachineConfiguration machineConfiguration;
    private final ObservableList<SingleProcessHistory> machineHistoriesList;

    public SingleConfigurationTableView(MachineConfiguration machineConfiguration, List<SingleProcessHistory> historiesList) {
        this.machineConfiguration = machineConfiguration;
        setText(machineConfiguration.toString());
        setMinHeight(USE_PREF_SIZE);
        machineHistoryTable = new TableView<>();
        preProcessedMessageColumn = new TableColumn<>("Pre-Processed Message");
        processedMessageColumn = new TableColumn<>("Processed Message");
        timeTakenColumn = new TableColumn<>("Time Taken (Nano-Seconds)");
        preProcessedMessageColumn.setCellValueFactory(new PropertyValueFactory<>("unprocessedInput"));
        machineHistoryTable.getColumns().add(preProcessedMessageColumn);
        processedMessageColumn.setCellValueFactory(new PropertyValueFactory<>("processedInput"));
        machineHistoryTable.getColumns().add(processedMessageColumn);
        timeTakenColumn.setCellValueFactory(new PropertyValueFactory<>("timeTaken"));
        machineHistoryTable.getColumns().add(timeTakenColumn);
        machineHistoriesList = FXCollections.observableArrayList(historiesList);
        machineHistoryTable.setItems(machineHistoriesList);
        setContent(machineHistoryTable);
    }

    public MachineConfiguration getMachineConfiguration() {
        return machineConfiguration;
    }

    public void addDeltaHistories(List<SingleProcessHistory> recentHistoriesList) {
        if (machineHistoriesList.size() < recentHistoriesList.size()) {
            recentHistoriesList.forEach(x -> {
                if (!machineHistoriesList.contains(x)) {
                    machineHistoriesList.add(x);
                }
            });
        }
    }
}