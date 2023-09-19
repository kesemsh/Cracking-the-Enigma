package components.tabs.machine.configuration.setter.selectors.rotors.start.positions;

import exceptions.NotEnoughRotorStartPositionsSelectedException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import resources.components.CharacterComponent;
import resources.factory.ResourceFactory;
import java.util.ArrayList;
import java.util.List;

public class RotorStartPositionsSelector {
    @FXML private HBox availableSelectionBox;
    @FXML private HBox selectedBox;
    @FXML private Label selectedRotorStartPositionsLabel;
    @FXML private Label rotorsCountLabel;
    private List<Character> allKeys;
    private final IntegerProperty selectedRotorStartPositionsCount;
    private final IntegerProperty rotorsCount;
    private final BooleanProperty isSelectionComplete;
    private final BooleanProperty isSelectorResettable;

    public RotorStartPositionsSelector() {
        selectedRotorStartPositionsCount = new SimpleIntegerProperty();
        rotorsCount = new SimpleIntegerProperty();
        isSelectionComplete = new SimpleBooleanProperty();
        isSelectorResettable = new SimpleBooleanProperty();
    }

    @FXML
    private void initialize() {
        rotorsCountLabel.textProperty().bind(rotorsCount.asString());
        selectedRotorStartPositionsCount.bind(Bindings.size(selectedBox.getChildren()));
        selectedRotorStartPositionsLabel.textProperty().bind(selectedRotorStartPositionsCount.asString());
        isSelectionComplete.bind(Bindings.equal(selectedRotorStartPositionsCount, rotorsCount));
        availableSelectionBox.disableProperty().bind(isSelectionComplete);
        isSelectorResettable.bind(Bindings.notEqual(selectedRotorStartPositionsCount, 0));
    }

    public BooleanProperty isSelectorResettableProperty() {
        return isSelectorResettable;
    }

    private void setUpAllStartPositions() {
        allKeys.forEach(character -> {
            CharacterComponent currentCharacterComponent = ResourceFactory.createRotorPositionCharacterComponent(character);

            currentCharacterComponent.setOnMouseClicked(e -> characterComponentClicked(currentCharacterComponent));
            addCharacterComponentToAvailableBox(currentCharacterComponent);
        });
    }

    private void clearSelectedRotorStartPositions() {
        selectedBox.getChildren().clear();
    }

    private void clearAvailableRotorStartPositions() {
        availableSelectionBox.getChildren().clear();
    }

    public void updateAllKeys(List<Character> allKeys, int rotorsCount) {
        this.rotorsCount.set(rotorsCount);
        this.allKeys = new ArrayList<>(allKeys);
        resetSelector();
    }

    public void resetSelector() {
        clearAvailableRotorStartPositions();
        clearSelectedRotorStartPositions();
        setUpAllStartPositions();
    }

    private void characterComponentClicked(CharacterComponent clickedCharacterComponent) {
        if (selectedBox.getChildren().contains(clickedCharacterComponent)) {
            selectedBox.getChildren().remove(clickedCharacterComponent);
        }
        else {
            CharacterComponent newCharacterComponent = ResourceFactory.createRotorPositionCharacterComponent(clickedCharacterComponent.getCharacter());

            newCharacterComponent.setOnMouseClicked(e -> characterComponentClicked(newCharacterComponent));
            selectedBox.getChildren().add(newCharacterComponent);
        }
    }

    private void addCharacterComponentToAvailableBox(CharacterComponent rotorToAdd) {
        if (!availableSelectionBox.getChildren().isEmpty()) {
            for (int i = 0; i < availableSelectionBox.getChildren().size(); i++) {
                if (((CharacterComponent) availableSelectionBox.getChildren().get(i)).getCharacter() > rotorToAdd.getCharacter()) {
                    availableSelectionBox.getChildren().add(i, rotorToAdd);
                    return;
                }
            }
        }

        availableSelectionBox.getChildren().add(rotorToAdd);
    }

    public List<Character> getSelectedRotorStartPositions() {
        if (!isSelectionComplete.get()) {
            throw new NotEnoughRotorStartPositionsSelectedException(rotorsCount.get(), selectedRotorStartPositionsCount.get());
        }
        else {
            List<Character> result = new ArrayList<>();

            selectedBox.getChildren().forEach(selectedRotor -> result.add(((CharacterComponent) selectedRotor).getCharacter()));

            return result;
        }
    }
}