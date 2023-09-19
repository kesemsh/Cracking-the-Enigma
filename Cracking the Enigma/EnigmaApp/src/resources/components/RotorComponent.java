package resources.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class RotorComponent extends VBox {
    private final String ROTOR_IMAGE_URL = "/resources/images/Rotor.png";
    private final int ROTOR_IMAGE_HEIGHT = 50;
    private final int ROTOR_IMAGE_WIDTH = 50;
    private final int rotorID;

    public RotorComponent(int rotorID) {
        this.rotorID = rotorID;
        setRotorImage();
        setRotorID();
    }

    public Integer getRotorID() {
        return rotorID;
    }

    private void setRotorID() {
        Label rotorIDLabel = new Label("-" + rotorID + "-");
        rotorIDLabel.setStyle("-fx-font-weight: bold;");

        HBox rotorIDBox = new HBox(rotorIDLabel);
        rotorIDBox.setAlignment(Pos.CENTER);

        getChildren().add(rotorIDBox);
    }

    private void setRotorImage() {
        try {
            ImageView rotorImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(ROTOR_IMAGE_URL)).openStream()));

            rotorImage.setFitHeight(ROTOR_IMAGE_HEIGHT);
            rotorImage.setFitWidth(ROTOR_IMAGE_WIDTH);
            rotorImage.setPreserveRatio(true);
            rotorImage.setPickOnBounds(true);
            getChildren().add(rotorImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}