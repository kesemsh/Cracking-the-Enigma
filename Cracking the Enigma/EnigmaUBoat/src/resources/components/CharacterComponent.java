package resources.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

public class CharacterComponent extends StackPane {
    private String characterImageUrl;
    private final int CHARACTER_IMAGE_HEIGHT = 50;
    private final int CHARACTER_IMAGE_WIDTH = 50;
    private final Character character;
    private ImageView characterImage;

    public CharacterComponent(Character character, String characterImageUrl) {
        this.character = character;
        this.characterImageUrl = characterImageUrl;
        setCharacterImage();
        setCharacter();
    }

    public void changeCharacterImage(String CHARACTER_IMAGE_URL) {
        getChildren().remove(characterImage);
        this.characterImageUrl = CHARACTER_IMAGE_URL;
        setCharacterImage();
        setCharacter();
    }

    public Character getCharacter() {
        return character;
    }

    private void setCharacter() {
        Label characterLabel = new Label(character.toString());

        characterLabel.setAlignment(Pos.CENTER);
        characterLabel.setStyle("-fx-font-weight: bold;");
        getChildren().add(characterLabel);
    }

    private void setCharacterImage() {
        try {
            this.characterImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(characterImageUrl)).openStream()));

            characterImage.setFitHeight(CHARACTER_IMAGE_HEIGHT);
            characterImage.setFitWidth(CHARACTER_IMAGE_WIDTH);
            characterImage.setPreserveRatio(true);
            characterImage.setPickOnBounds(true);
            getChildren().add(characterImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}