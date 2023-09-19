package uboat.screens.main.tabs.machine.configuration.setter.selectors.reflector;

import exceptions.ReflectorNotSelectedException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import object.numbering.RomanNumber;

public class ReflectorIDSelector {
    @FXML private ComboBox<String> reflectorIDComboBox;
    private int reflectorsInStorageCount;
    private final BooleanProperty isSelectorResettable;

    public ReflectorIDSelector() {
        isSelectorResettable = new SimpleBooleanProperty();
    }

    @FXML
    private void initialize() {
        isSelectorResettable.bind(Bindings.isNotNull(reflectorIDComboBox.valueProperty()));
    }

    public BooleanProperty isSelectorResettableProperty() {
        return isSelectorResettable;
    }

    public void setUp(int reflectorsInStorageCount) {
        this.reflectorsInStorageCount = reflectorsInStorageCount;
        resetSelector();
    }

    public void resetSelector() {
        clearSelectedReflector();
        reflectorIDComboBox.getItems().clear();
        addAllReflectorIDOptions();
    }

    private void addAllReflectorIDOptions() {
        for (int i = 1; i <= reflectorsInStorageCount; i++) {
            reflectorIDComboBox.getItems().add(RomanNumber.fromInt(i).getStringValue());
        }
    }

    public void clearSelectedReflector() {
        reflectorIDComboBox.valueProperty().set(null);
    }

    public RomanNumber getSelectedReflectorID() {
        if (reflectorIDComboBox.getValue() == null) {
            throw new ReflectorNotSelectedException();
        }
        else {
            return RomanNumber.fromString(reflectorIDComboBox.getValue());
        }
    }
}