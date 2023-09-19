package uboat.screens.main.tabs.machine.configuration.setter.selectors.rotors.ids;

import exceptions.NotEnoughRotorsSelectedException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import resources.components.RotorComponent;
import resources.factory.ResourceFactory;

import java.util.ArrayList;
import java.util.List;

public class RotorIDsSelector {
    @FXML private HBox availableSelectionBox;
    @FXML private HBox selectedBox;
    @FXML private Label selectedRotorsLabel;
    @FXML private Label activeRotorsLabel;
    private final IntegerProperty selectedRotorsCount;
    private final IntegerProperty activeRotorsCount;
    private final IntegerProperty totalRotorsCount;
    private final BooleanProperty isSelectionComplete;
    private final BooleanProperty isSelectorResettable;

    public RotorIDsSelector() {
        selectedRotorsCount = new SimpleIntegerProperty();
        activeRotorsCount = new SimpleIntegerProperty();
        totalRotorsCount = new SimpleIntegerProperty();
        isSelectionComplete = new SimpleBooleanProperty();
        isSelectorResettable = new SimpleBooleanProperty();
    }

    @FXML
    private void initialize() {
        activeRotorsLabel.textProperty().bind(activeRotorsCount.asString());
        selectedRotorsCount.bind(Bindings.size(selectedBox.getChildren()));
        selectedRotorsLabel.textProperty().bind(selectedRotorsCount.asString());
        isSelectionComplete.bind(Bindings.equal(selectedRotorsCount, activeRotorsCount));
        availableSelectionBox.disableProperty().bind(isSelectionComplete);
        isSelectorResettable.bind(Bindings.notEqual(selectedRotorsCount, 0));
    }

    public BooleanProperty isSelectorResettableProperty() {
        return isSelectorResettable;
    }

    public void updateRotorsCount(int activeRotorsCount, int totalRotorsCount) {
        this.activeRotorsCount.set(activeRotorsCount);
        this.totalRotorsCount.set(totalRotorsCount);
        resetSelector();
    }

    public void resetSelector() {
        selectedBox.getChildren().clear();
        availableSelectionBox.getChildren().clear();
        setUpAllRotors(totalRotorsCount.get());
    }

    private void setUpAllRotors(int rotorsCount) {
        for (int i = 1; i <= rotorsCount; i++) {
            RotorComponent currentRotorComponent = ResourceFactory.createRotorComponent(i);

            currentRotorComponent.setOnMouseClicked(e -> rotorComponentClicked(currentRotorComponent));
            addRotorComponentToAvailableBox(currentRotorComponent);
        }
    }

    private void rotorComponentClicked(RotorComponent clickedRotorComponent) {
        if (selectedBox.getChildren().contains(clickedRotorComponent)) {
            selectedBox.getChildren().remove(clickedRotorComponent);
            addRotorComponentToAvailableBox(clickedRotorComponent);
        }
        else {
            availableSelectionBox.getChildren().remove(clickedRotorComponent);
            selectedBox.getChildren().add(clickedRotorComponent);
        }
    }

    private void addRotorComponentToAvailableBox(RotorComponent rotorToAdd) {
        if (!availableSelectionBox.getChildren().isEmpty()) {
            for (int i = 0; i < availableSelectionBox.getChildren().size(); i++) {
                if (((RotorComponent) availableSelectionBox.getChildren().get(i)).getRotorID() > rotorToAdd.getRotorID()) {
                    availableSelectionBox.getChildren().add(i, rotorToAdd);
                    return;
                }
            }
        }

        availableSelectionBox.getChildren().add(rotorToAdd);
    }

    public List<Integer> getSelectedRotorIDs() {
        if (!isSelectionComplete.get()) {
            throw new NotEnoughRotorsSelectedException(activeRotorsCount.get(), selectedRotorsCount.get());
        }
        else {
            List<Integer> result = new ArrayList<>();

            selectedBox.getChildren().forEach(selectedRotor -> result.add(((RotorComponent) selectedRotor).getRotorID()));

            return result;
        }
    }
}