package machine.components.dictionary;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Dictionary implements Serializable {
    private final Set<String> allowedWords;
    private final List<Character> excludedCharacters;

    public Set<String> getAllowedWords() {
        return allowedWords;
    }

    public List<Character> getExcludedCharacters() {
        return excludedCharacters;
    }

    public Dictionary(Set<String> allowedWords, List<Character> excludedCharacters) {
        this.allowedWords = allowedWords;
        this.excludedCharacters = excludedCharacters;
    }

    public boolean areWordsInDictionary(List<String> wordsToCheck) {
        return allowedWords.containsAll(wordsToCheck);
    }

    public String getMessageWithoutExcludedCharacters(String messageToUpdate) {
        return messageToUpdate.chars()
                .filter(x -> !excludedCharacters.contains((char) x))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String getInvalidWordFromList(List<String> wordsToCheck) {
        for (String currWord : wordsToCheck) {
            if (!allowedWords.contains(currWord)) {
                return currWord;
            }
        }

        return "";
    }
}
