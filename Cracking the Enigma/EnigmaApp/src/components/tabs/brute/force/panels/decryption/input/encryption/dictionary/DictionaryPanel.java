package components.tabs.brute.force.panels.decryption.input.encryption.dictionary;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import resources.components.WordComponent;
import resources.factory.ResourceFactory;

import javafx.scene.layout.FlowPane;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DictionaryPanel {
    @FXML private FlowPane dictionaryFlowPane;
    private Set<String> allowedWords;
    private WordComponent clickedWordComponent;
    private Consumer<String> addWordToTextFieldConsumer;

    private void createDictionaryPanel() {
        allowedWords.forEach(String -> {
            WordComponent currentWordComponent = ResourceFactory.createWordComponent(String);

            currentWordComponent.setOnMouseClicked(e -> {
                this.clickedWordComponent = currentWordComponent;
                addWordToTextFieldConsumer.accept(clickedWordComponent.getWord());
            });
            dictionaryFlowPane.getChildren().add(currentWordComponent);
        });
    }

    public void setUpDictionaryPanel(Consumer<String> addWordToTextFieldsConsumer) {
        this.addWordToTextFieldConsumer = addWordToTextFieldsConsumer;
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        this.allowedWords = new HashSet<>(allowedWords);
    }

    public void resetDictionaryPanel() {
        resetDictionaryWords();
        createDictionaryPanel();
    }

    private void resetDictionaryWords() {
        dictionaryFlowPane.getChildren().clear();
    }
}