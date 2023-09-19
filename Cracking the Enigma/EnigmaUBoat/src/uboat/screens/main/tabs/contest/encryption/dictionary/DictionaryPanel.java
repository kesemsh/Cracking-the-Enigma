package uboat.screens.main.tabs.contest.encryption.dictionary;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import resources.components.WordComponent;
import resources.factory.ResourceFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DictionaryPanel {
    @FXML private FlowPane dictionaryFlowPane;
    private Set<String> allowedWords = new HashSet<>();
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

    public void setUp(Consumer<String> addWordToTextFieldsConsumer) {
        this.addWordToTextFieldConsumer = addWordToTextFieldsConsumer;
    }

    public void updateAllowedWords(Set<String> allowedWords) {
        this.allowedWords = new HashSet<>(allowedWords);
        createDictionaryPanel();
    }

    public void reset() {
        resetDictionaryWords();
        createDictionaryPanel();
    }

    private void resetDictionaryWords() {
        dictionaryFlowPane.getChildren().clear();
    }
}