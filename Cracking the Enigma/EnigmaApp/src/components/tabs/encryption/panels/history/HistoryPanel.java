package components.tabs.encryption.panels.history;

import components.tabs.encryption.panels.history.view.SingleConfigurationTableView;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import object.machine.history.MachineHistoryPerConfiguration;

import java.util.List;

public class HistoryPanel {
    @FXML private Accordion machineHistoryAccordion;

    public void resetHistoryPanel() {
        machineHistoryAccordion.getPanes().clear();
    }

    public void updateMachineHistory(List<MachineHistoryPerConfiguration> machineHistoryList) {
        machineHistoryList.forEach(this::addToHistory);
    }

    private void addToHistory(MachineHistoryPerConfiguration machineHistoryPerConfiguration) {
        boolean foundHistory = false;

        for (int i = 0; i < machineHistoryAccordion.getPanes().size(); i++) {
            if (((SingleConfigurationTableView) machineHistoryAccordion.getPanes().get(i)).getMachineConfiguration() == machineHistoryPerConfiguration.getMachineConfiguration()) {
                foundHistory = true;
                ((SingleConfigurationTableView) machineHistoryAccordion.getPanes().get(i)).addDeltaHistories(machineHistoryPerConfiguration.getSingleProcessHistories());
            }
        }

        if (!foundHistory) {
            machineHistoryAccordion.getPanes().add(new SingleConfigurationTableView(machineHistoryPerConfiguration.getMachineConfiguration(), machineHistoryPerConfiguration.getSingleProcessHistories()));
        }
    }
}