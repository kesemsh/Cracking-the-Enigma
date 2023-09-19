package uboat.screens.main.tabs.machine.configuration.setter.selectors.plugs.selection.row;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class PlugsSelectionRow extends HBox {
    private final IntegerProperty plugID;
    private final ComboBox<Character> comboBox1;
    private final ComboBox<Character> comboBox2;
    private final Button buttonAddNewPlug;
    private final Button buttonRemovePlug;

    public PlugsSelectionRow() {
        plugID = new SimpleIntegerProperty();
        comboBox1 = new ComboBox<>();
        comboBox2 = new ComboBox<>();
        buttonAddNewPlug = new Button("+");
        buttonRemovePlug = new Button("x");
        setMinWidth(150);
        setAlignment(Pos.CENTER_LEFT);
        setLabels();
        setComboBoxes();
        setButtons();
    }

    private void setButtons() {
        buttonRemovePlug.setPrefWidth(25);
        buttonRemovePlug.setPrefHeight(25);
        getChildren().add(buttonRemovePlug);
        setMargin(buttonRemovePlug, new Insets(0, 5, 0, 0));
        buttonAddNewPlug.setPrefWidth(25);
        buttonAddNewPlug.setPrefHeight(25);
        getChildren().add(buttonAddNewPlug);
    }

    private void setComboBoxes() {
        comboBox1.setMinWidth(60);
        getChildren().add(comboBox1);
        setMargin(comboBox1, new Insets(0, 10, 0, 10));
        comboBox2.setMinWidth(60);
        getChildren().add(comboBox2);
        setMargin(comboBox2, new Insets(0, 10, 0, 0));
    }

    public ComboBox<Character> getComboBox1() {
        return comboBox1;
    }

    public ComboBox<Character> getComboBox2() {
        return comboBox2;
    }

    public int getPlugID() {
        return plugID.get();
    }

    public IntegerProperty plugIDProperty() {
        return plugID;
    }

    public void setPlugID(int plugID) {
        this.plugID.set(plugID);
    }

    public Button getButtonAddNewPlug() {
        return buttonAddNewPlug;
    }

    public Button getButtonRemovePlug() {
        return buttonRemovePlug;
    }

    private void setLabels() {
        getChildren().add(new Label("Plug #"));
        Label plugIDLabel = new Label();

        plugIDLabel.textProperty().bind(plugID.asString());
        getChildren().add(plugIDLabel);
        getChildren().add(new Label(":"));
    }
}