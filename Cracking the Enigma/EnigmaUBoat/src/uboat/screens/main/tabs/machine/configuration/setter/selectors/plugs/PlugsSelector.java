package uboat.screens.main.tabs.machine.configuration.setter.selectors.plugs;

import uboat.screens.main.tabs.machine.configuration.setter.selectors.plugs.selection.row.PlugsSelectionRow;
import exceptions.IncompletePlugPairSelectedException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.*;

public class PlugsSelector {
    private final IntegerProperty plugsCount;
    private List<Character> allKeys;
    private ObservableSet<Character> allAvailableKeys;
    private final ListChangeListener<? super Node> plugsListVBoxListener;
    private final BooleanProperty isSelectorResettable;
    private final IntegerProperty charactersForPlugsSelectedCount;
    @FXML private VBox plugsListVBox;

    public PlugsSelector() {
        plugsCount = new SimpleIntegerProperty();
        plugsListVBoxListener = observable -> updateAllPlugIDs();
        charactersForPlugsSelectedCount = new SimpleIntegerProperty(0);
        isSelectorResettable = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        plugsCount.bind(Bindings.size(plugsListVBox.getChildren()));
        plugsListVBox.getChildren().addListener(plugsListVBoxListener);
        isSelectorResettable.bind(Bindings.notEqual(charactersForPlugsSelectedCount, 0));
    }

    public BooleanProperty isSelectorResettableProperty() {
        return isSelectorResettable;
    }

    private void updateAllPlugIDs() {
        if (plugsListVBox.getChildren().size() == 0) {
            emptyPlugsList();
        }

        for (int i = 1; i <= plugsCount.get(); i++) {
            ((PlugsSelectionRow) plugsListVBox.getChildren().get(i - 1)).setPlugID(i);
        }
    }

    public void setUp(List<Character> allKeys) {
        this.allKeys = new ArrayList<>(allKeys);
        resetSelector();
    }

    public void resetSelector() {
        allAvailableKeys = FXCollections.observableSet(new HashSet<>(allKeys));
        allAvailableKeys.addListener((SetChangeListener<? super Character>) observable -> updateAllComboBoxes());
        plugsListVBox.getChildren().removeListener(plugsListVBoxListener);
        emptyPlugsList();
        plugsListVBox.getChildren().addListener(plugsListVBoxListener);
    }

    private void updateAllComboBoxes() {
        plugsListVBox.getChildren().forEach(plugsSelectionRow -> {
            ComboBox<Character> comboBox1 = ((PlugsSelectionRow) plugsSelectionRow).getComboBox1();
            ComboBox<Character> comboBox2 = ((PlugsSelectionRow) plugsSelectionRow).getComboBox2();

            updateSingleComboBox(comboBox1);
            updateSingleComboBox(comboBox2);
        });
    }

    private void emptyPlugsList() {
        if (plugsListVBox.getChildren().size() != 0) {
            plugsListVBox.getChildren().clear();
        }

        allAvailableKeys.clear();
        allAvailableKeys.addAll(allKeys);
        addPlugSelectionRow();
    }

    private void addPlugSelectionRow() {
        PlugsSelectionRow plugsSelectionRow = new PlugsSelectionRow();

        plugsSelectionRow.getComboBox1().getItems().addAll(allAvailableKeys);
        plugsSelectionRow.getComboBox1().valueProperty().addListener((observable, oldValue, newValue) -> updateSelectedKey(oldValue, newValue));
        plugsSelectionRow.getComboBox2().getItems().addAll(allAvailableKeys);
        plugsSelectionRow.getComboBox2().valueProperty().addListener((observable, oldValue, newValue) -> updateSelectedKey(oldValue, newValue));
        plugsSelectionRow.getButtonAddNewPlug().setOnAction(e -> addPlugSelectionRow());
        plugsSelectionRow.getButtonRemovePlug().setOnAction(e -> removePlugSelectionRow(plugsSelectionRow.getPlugID()));
        plugsSelectionRow.getButtonRemovePlug().visibleProperty().bind(Bindings.isNull(plugsSelectionRow.getComboBox1().valueProperty()).and(Bindings.isNull(plugsSelectionRow.getComboBox2().valueProperty())).not());
        plugsListVBox.getChildren().add(plugsSelectionRow);
        plugsSelectionRow.setPlugID(plugsCount.get());
        plugsSelectionRow.getButtonAddNewPlug().visibleProperty().bind(Bindings.equal(Bindings.size(plugsListVBox.getChildren()), allKeys.size() / 2).or(Bindings.isNull(plugsSelectionRow.getComboBox1().valueProperty()).or(Bindings.isNull(plugsSelectionRow.getComboBox2().valueProperty()))).or(Bindings.notEqual(plugsCount, plugsSelectionRow.plugIDProperty())).not());
    }

    private void removePlugSelectionRow(int plugID) {
        PlugsSelectionRow plugsSelectionRowToRemove = ((PlugsSelectionRow) plugsListVBox.getChildren().get(plugID - 1));
        ComboBox<Character> comboBox1 = plugsSelectionRowToRemove.getComboBox1();
        ComboBox<Character> comboBox2 = plugsSelectionRowToRemove.getComboBox2();

        plugsListVBox.getChildren().remove(plugsSelectionRowToRemove);
        comboBox1.setValue(null);
        comboBox2.setValue(null);
    }

    private void updateSelectedKey(Character oldValue, Character newValue) {
        if (oldValue == null && newValue != null) {
            charactersForPlugsSelectedCount.set(charactersForPlugsSelectedCount.get() + 1);
            allAvailableKeys.remove(newValue);
        }

        if (oldValue != null && newValue == null) {
            charactersForPlugsSelectedCount.set(charactersForPlugsSelectedCount.get() - 1);
            allAvailableKeys.add(oldValue);
        }

        if (oldValue != null && newValue != null) {
            allAvailableKeys.add(oldValue);
            allAvailableKeys.remove(newValue);
        }
    }

    private void updateSingleComboBox(ComboBox<Character> comboBox) {
        List<Character> updatedList = new ArrayList<>(allAvailableKeys);
        List<Character> currentItemsList = new ArrayList<>(comboBox.getItems());

        if (comboBox.getValue() != null) {
            updatedList.add(comboBox.getValue());
        }

        currentItemsList.forEach(item -> {
            if (item != comboBox.getValue() && !updatedList.contains(item)) {
                comboBox.getItems().remove(item);
            }
        });

        updatedList.forEach(item -> {
            if (!currentItemsList.contains(item)) {
                comboBox.getItems().add(item);
            }
        });
    }

    public Map<Character, Character> getSelectedPlugs() {
        checkPlugsSelectionValidity();
        Map<Character, Character> result = new HashMap<>();

        plugsListVBox.getChildren().forEach(plugsSelectionRow -> {
            ComboBox<Character> comboBox1 = ((PlugsSelectionRow) plugsSelectionRow).getComboBox1();
            ComboBox<Character> comboBox2 = ((PlugsSelectionRow) plugsSelectionRow).getComboBox2();

            if (comboBox1.getValue() != null && comboBox2.getValue() != null) {
                result.put(comboBox1.getValue(), comboBox2.getValue());
            }
        });

        return result;
    }

    private void checkPlugsSelectionValidity() {
        plugsListVBox.getChildren().forEach(plugsSelectionRow -> {
            ComboBox<Character> comboBox1 = ((PlugsSelectionRow) plugsSelectionRow).getComboBox1();
            ComboBox<Character> comboBox2 = ((PlugsSelectionRow) plugsSelectionRow).getComboBox2();

            if ((comboBox1.getValue() != null && comboBox2.getValue() == null) || (comboBox1.getValue() == null && comboBox2.getValue() != null)) {
                throw new IncompletePlugPairSelectedException();
            }
        });
    }
}