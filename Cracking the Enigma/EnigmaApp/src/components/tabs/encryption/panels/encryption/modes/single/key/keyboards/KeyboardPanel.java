package components.tabs.encryption.panels.encryption.modes.single.key.keyboards;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import resources.components.CharacterComponent;
import resources.factory.ResourceFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeyboardPanel extends HBox {
    @FXML private FlowPane characterKeyboard;
    @FXML private FlowPane lightsCharacterKeyboard;
    private List<Character> allKeys;
    private Consumer<Character> processCharacterConsumer;
    private CharacterComponent clickedCharacterComponent;
    private final ObjectProperty<CharacterComponent> litCharacterComponent;
    private static final String LIGHT_BULB_OFF_CHARACTER_IMAGE_URL = "/resources/images/LightBulbOff.png";
    private static final String LIGHT_BULB_ON_CHARACTER_IMAGE_URL = "/resources/images/LightBulbOn.png";

    public KeyboardPanel() {
        litCharacterComponent = new SimpleObjectProperty<>(null);
    }

    @FXML
    private void initialize() {
        litCharacterComponent.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lightCharacterComponentOnKeyboard(newValue);
            }

            if (oldValue != null) {
                turnOffLitCharacterComponentOnKeyboard(oldValue);
            }
        });
    }

    public void resetLitCharacterOnKeyboard() {
        litCharacterComponent.set(null);
    }

    private void setUpCharacterKeyboard() {
        allKeys.forEach(character -> {
            CharacterComponent currentCharacterComponent = ResourceFactory.createKeyCharacterComponent(character);

            currentCharacterComponent.setOnMouseClicked(e -> {
                updateCurrentlyClickedCharacterComponent(currentCharacterComponent);
                processCharacterConsumer.accept(getCharacterToProcess());
            });
            characterKeyboard.getChildren().add(currentCharacterComponent);
        });
    }

    private void setUpLightsCharacterKeyboard() {
        allKeys.forEach(character -> {
            CharacterComponent currentCharacterComponent = ResourceFactory.createLightBulbCharacterComponent(character, LIGHT_BULB_OFF_CHARACTER_IMAGE_URL);

            lightsCharacterKeyboard.getChildren().add(currentCharacterComponent);
        });
    }

    private void updateCurrentlyClickedCharacterComponent(CharacterComponent clickedCharacterComponent) {
        this.clickedCharacterComponent = clickedCharacterComponent;
    }

    public void displayProcessedCharacterOnLightsCharacterKeyboard(Character processedCharacter) {
        for (int i = 0; i < lightsCharacterKeyboard.getChildren().size(); i++) {
            CharacterComponent processedCharacterComponent = (CharacterComponent) lightsCharacterKeyboard.getChildren().get(i);

            if (processedCharacterComponent.getCharacter().equals(processedCharacter)) {
                litCharacterComponent.set(processedCharacterComponent);
                break;
            }
        }
    }

    private void lightCharacterComponentOnKeyboard(CharacterComponent characterComponent) {
        characterComponent.changeCharacterImage(LIGHT_BULB_ON_CHARACTER_IMAGE_URL);
    }

    private void turnOffLitCharacterComponentOnKeyboard(CharacterComponent characterComponent) {
        characterComponent.changeCharacterImage(LIGHT_BULB_OFF_CHARACTER_IMAGE_URL);
    }

    public void setUpEncryptionKeyboardPanel(Consumer<Character> processCharacterConsumer) {
        this.processCharacterConsumer = processCharacterConsumer;
    }

    public void createCharacterKeyboards() {
        setUpCharacterKeyboard();
        setUpLightsCharacterKeyboard();
    }

    private Character getCharacterToProcess() {
        return clickedCharacterComponent.getCharacter();
    }

    public void updateAllKeys(List<Character> allKeys) {
        this.allKeys = new ArrayList<>(allKeys);
    }

    public void resetCharacterKeyboards() {
        characterKeyboard.getChildren().clear();
        lightsCharacterKeyboard.getChildren().clear();
    }
}