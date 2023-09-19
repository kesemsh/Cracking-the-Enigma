package resources.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

import static impl.org.controlsfx.ImplUtils.getChildren;

public class WordComponent extends StackPane {
    private final String WORD_IMAGE_URL = "/resources/images/Word.png";
    private final int CHARACTER_IMAGE_HEIGHT = 100;
    private final int CHARACTER_IMAGE_WIDTH = 180;
    private final String word;
    private ImageView wordImage;

    public WordComponent(String word) {
        this.word = word;
        setWordImage();
        setWord();
    }

    public String getWord() {
        return word;
    }

    private void setWord() {
        Label wordLabel = new Label(word);

        wordLabel.setAlignment(Pos.CENTER);
        wordLabel.setStyle("-fx-font-weight: bold;");
        getChildren().add(wordLabel);
    }

    private void setWordImage() {
        try {
            this.wordImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(WORD_IMAGE_URL)).openStream()));

            wordImage.setFitHeight(CHARACTER_IMAGE_HEIGHT);
            wordImage.setFitWidth(CHARACTER_IMAGE_WIDTH);
            wordImage.setPreserveRatio(true);
            wordImage.setPickOnBounds(true);
            getChildren().add(wordImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}